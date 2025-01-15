package reactivemongo.api

import scala.util.{Failure, Success, Try}

import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.buffer.ReadableBuffer
import reactivemongo.api.bson.collection.BSONSerializationPack.readFromBuffer
import reactivemongo.core.actors.ExpectingResponse
import reactivemongo.core.protocol.CollectionAwareRequestOp

case class ExpectingResponseWrapper(expectingResponse: ExpectingResponse) {

  lazy val statement: Option[BSONDocument] = {
    val buf = expectingResponse.requestMaker.payload.duplicate()
    Try[BSONDocument] {
      val sz = buf.getIntLE(buf.readerIndex)
      val bytes = Array.ofDim[Byte](sz)
      buf.readBytes(bytes)
      readFromBuffer(ReadableBuffer(bytes))
    } match {
      case Failure(_) => None
      case Success(value) =>
        buf.resetReaderIndex()
        Some(value)
    }
  }

  lazy val collectionName: Option[String] = statement.flatMap { document =>
    document
      .headOption
      .flatMap(
        _.value.asOpt[String].orElse(document.getAsOpt[String]("collection")) // in case of getMore
      )
  }

  lazy val dbName: Option[String] = expectingResponse.requestMaker.op match {
    case op: CollectionAwareRequestOp => Some(op.db)
    case _                            => None
  }

  lazy val operationName: Option[String] = statement.flatMap(_.headOption.map(_.name))

}
