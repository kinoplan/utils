package reactivemongo.api

import scala.annotation.static
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

import io.opentelemetry.semconv.incubating.DbIncubatingAttributes._
import kamon.Kamon
import kamon.tag.TagSet
import kamon.trace.Span
import kamon.util.CallingThreadExecutionContext
import kanela.agent.libs.net.bytebuddy.asm.Advice
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.buffer.ReadableBuffer
import reactivemongo.api.bson.collection.BSONSerializationPack.readFromBuffer
import reactivemongo.core.actors.ExpectingResponse
import reactivemongo.core.protocol.{CollectionAwareRequestOp, Message, Response}

import io.kinoplan.utils.reactivemongo.kamon.instrumentation.ReactiveMongoClientInstrumentation

class MongoConnectionSendExpectingResponseAdvice

object MongoConnectionSendExpectingResponseAdvice {

  @Advice.OnMethodEnter(suppress = classOf[Throwable])
  @static
  def enter(
    @Advice.Argument(0)
    expectingResponse: ExpectingResponse
  ): Span = startSpan(expectingResponse)

  @Advice.OnMethodExit(suppress = classOf[Throwable])
  @static
  def exit(@Advice.Enter span: Span, @Advice.Return future: Future[Response]): Unit =
    finishSpan(span, future)

  def startSpan(expectingResponse: ExpectingResponse): Span = {
    val requestMaker = expectingResponse.requestMaker
    Try[(Int, BSONDocument)] {
      val buf = requestMaker.payload
      val index = requestMaker.op match {
        // OP_MSG section starts with its kind, the driver sends only kind 0 - a single document
        case _: Message =>
          require(buf.getByte(buf.readerIndex) == 0, "Unsupported OP_MSG section kind")
          buf.readerIndex + 1
        case _ => buf.readerIndex
      }
      val sz = buf.getIntLE(index)
      val bytes = Array.ofDim[Byte](sz)
      buf.getBytes(index, bytes)
      (sz, readFromBuffer(ReadableBuffer(bytes)))
    } match {
      case Failure(_)                => Span.Empty
      case Success((size, document)) =>
        val dbO = requestMaker.op match {
          case op: CollectionAwareRequestOp => Some(op.db)
          case _                            => document.getAsOpt[String]("$db") // OP_MSG
        }

        // at first position there is the operation name as key
        val operationNameO = document.headOption.map(_.name)
        val collectionNameO = document
          .headOption
          .flatMap(
            _.value.asOpt[String].orElse(document.getAsOpt[String]("collection"))
          ) // in case of getMore

        val spanOperationName = (operationNameO, collectionNameO.orElse(dbO)) match {
          case (Some(op), Some(target)) => s"$op $target"
          case (Some(op), None)         => op
          case (None, Some(target))     => target
          case (None, None)             => DbSystemNameIncubatingValues.MONGODB
        }

        val metricsTag = TagSet
          .builder()
          .add(DB_SYSTEM_NAME.getKey, DbSystemNameIncubatingValues.MONGODB)
          .add(DB_NAMESPACE.getKey, dbO.getOrElse("undefined"))
          .add(DB_OPERATION_NAME.getKey, operationNameO.getOrElse("undefined"))
          .add(collectionNameO.fold(TagSet.Empty)(TagSet.of(DB_COLLECTION_NAME.getKey, _)))
          .build()

        val printer = ReactiveMongoClientInstrumentation.printer

        Kamon
          .clientSpanBuilder(spanOperationName, "org.reactivemongo")
          .tagMetrics(metricsTag)
          .tag(
            DB_QUERY_TEXT.getKey,
            printer.print(document, math.max(16, math.min(size, printer.maxLength)))
          )
          .start()
    }

  }

  def finishSpan(span: Span, future: Future[Response]): Unit = future.onComplete {
    case Failure(exception) => span.fail(exception).finish()
    case Success(response)  => response
        .error
        .fold(
          span.tag(DB_RESPONSE_RETURNED_ROWS.getKey, response.reply.numberReturned.toLong).finish()
        )(span.fail(_).finish())
  }(CallingThreadExecutionContext)

}
