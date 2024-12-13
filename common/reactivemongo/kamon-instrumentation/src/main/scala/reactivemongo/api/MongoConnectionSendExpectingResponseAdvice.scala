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
import reactivemongo.api.bson.BSONDocument.pretty
import reactivemongo.api.bson.buffer.ReadableBuffer
import reactivemongo.api.bson.collection.BSONSerializationPack.readFromBuffer
import reactivemongo.core.actors.ExpectingResponse
import reactivemongo.core.protocol.{CollectionAwareRequestOp, Response}

class MongoConnectionSendExpectingResponseAdvice

object MongoConnectionSendExpectingResponseAdvice {

  @Advice.OnMethodEnter(suppress = classOf[Throwable])
  @static
  def enter(
    @Advice.Argument(0)
    expectingResponse: ExpectingResponse
  ): Span = {
    val buf = expectingResponse.requestMaker.payload.duplicate()
    Try[BSONDocument] {
      val sz = buf.getIntLE(buf.readerIndex)
      val bytes = Array.ofDim[Byte](sz)
      buf.readBytes(bytes)
      readFromBuffer(ReadableBuffer(bytes))
    } match {
      case Failure(_) => Span.Empty
      case Success(document) =>
        buf.resetReaderIndex()

        val dbO = expectingResponse.requestMaker.op match {
          case op: CollectionAwareRequestOp => Some(op.db)
          case _                            => None
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
          case (None, None)             => DbSystemIncubatingValues.MONGODB
        }

        val metricsTag = TagSet
          .builder()
          .add(DB_SYSTEM.getKey, DbSystemIncubatingValues.MONGODB)
          .add(DB_NAMESPACE.getKey, dbO.getOrElse("undefined"))
          .add(DB_OPERATION_NAME.getKey, operationNameO.getOrElse("undefined"))
          .add(collectionNameO.fold(TagSet.Empty)(TagSet.of(DB_COLLECTION_NAME.getKey, _)))
          .build()

        Kamon
          .clientSpanBuilder(spanOperationName, "org.reactivemongo")
          .tagMetrics(metricsTag)
          .tag(DB_QUERY_TEXT.getKey, pretty(document))
          .start()
    }

  }

  @Advice.OnMethodExit(suppress = classOf[Throwable])
  @static
  def exit(@Advice.Enter span: Span, @Advice.Return future: Future[Response]): Unit = future
    .onComplete {
      case Failure(exception) => span.fail(exception).finish()
      case Success(response) => response
          .error
          .fold(
            span.tag(DB_RESPONSE_RETURNED_ROWS.getKey, response.reply.numberReturned.toLong).finish()
          )(span.fail(_).finish())
    }(CallingThreadExecutionContext)

}
