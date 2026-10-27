package reactivemongo.kinoplan.opentelemetry

import java.util.Locale
import java.util.logging.{Level, Logger}

import scala.util.Try

import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.buffer.ReadableBuffer
import reactivemongo.api.bson.collection.BSONSerializationPack.readFromBuffer
import reactivemongo.core.protocol.{CollectionAwareRequestOp, Message, RequestMaker}

final case class ReactiveMongoRequest(
  dbName: Option[String],
  operationName: Option[String],
  collectionName: Option[String]
)

object ReactiveMongoRequest {

  private val logger = Logger.getLogger(classOf[ReactiveMongoRequest].getName)

  private val QueryTextMaxLengthProperty =
    "otel.instrumentation.reactivemongo.query-text.max-length"

  private val printer = Printer.withLimit(
    property(QueryTextMaxLengthProperty).flatMap(value => Try(value.toInt).toOption).getOrElse(16384)
  )

  // A system property or the environment variable, like the agent reads its own properties
  private def property(name: String): Option[String] = sys
    .props
    .get(name)
    .orElse(sys.env.get(name.toUpperCase(Locale.ROOT).replace('-', '_').replace('.', '_')))

  def apply(
    requestMaker: RequestMaker,
    statement: Option[(Int, BSONDocument)]
  ): ReactiveMongoRequest = {
    val dbName = requestMaker.op match {
      case op: CollectionAwareRequestOp => Some(op.db)
      case _                            => statement.flatMap(_._2.getAsOpt[String]("$db")) // OP_MSG
    }

    statement match {
      case Some((_, document)) =>
        // at first position there is the operation name as key
        val operationName = document.headOption.map(_.name)
        val collectionName = document
          .headOption
          .flatMap(
            _.value.asOpt[String].orElse(document.getAsOpt[String]("collection"))
          ) // in case of getMore

        ReactiveMongoRequest(dbName, operationName, collectionName)
      case None => ReactiveMongoRequest(dbName, None, None)
    }
  }

  def queryText(statement: (Int, BSONDocument)): String = {
    val (size, document) = statement
    printer.print(document, math.max(16, math.min(size, printer.maxLength)))
  }

  // The command document with its size in bytes. The payload must be read eagerly:
  // its buffer is released once the request is sent
  def readStatement(requestMaker: RequestMaker): Option[(Int, BSONDocument)] = Try {
    val buf = requestMaker.payload
    val index = requestMaker.op match {
      // OP_MSG section starts with its kind, the driver sends only kind 0 - a single document
      case _: Message =>
        require(buf.getByte(buf.readerIndex) == 0, "Unsupported OP_MSG section kind")
        buf.readerIndex + 1
      case _ => buf.readerIndex
    }
    val size = buf.getIntLE(index)
    val bytes = Array.ofDim[Byte](size)
    buf.getBytes(index, bytes)
    (size, readFromBuffer(ReadableBuffer(bytes)))
  }.fold(
    error => {
      logger.log(Level.FINE, "Failed to read a ReactiveMongo command", error)
      None
    },
    Some(_)
  )

}
