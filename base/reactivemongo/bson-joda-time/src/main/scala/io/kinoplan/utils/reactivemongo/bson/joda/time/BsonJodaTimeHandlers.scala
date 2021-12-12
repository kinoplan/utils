package io.kinoplan.utils.reactivemongo.bson.joda.time

import scala.util.{Failure, Success, Try}

import org.joda.time.DateTime
import reactivemongo.api.bson.{BSONDateTime, BSONHandler, BSONValue}

trait BsonJodaTimeHandlers {

  implicit object BSONJodaDateTimeHandler extends BSONHandler[DateTime] {

    override def writeTry(dateTime: DateTime): Try[BSONValue] =
      Success(BSONDateTime(dateTime.getMillis))

    override def readTry(bson: BSONValue): Try[DateTime] = bson.asTry[BSONDateTime] match {
      case Success(bsonDateTime) => Try(new DateTime(bsonDateTime.value))
      case Failure(ex) =>
        throw new IllegalArgumentException(s"expected BSONDateTime, but found $bson, $ex")
    }

  }

}
