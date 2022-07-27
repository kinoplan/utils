package io.kinoplan.utils.reactivemongo.bson.joda.time

import scala.annotation.nowarn
import scala.util.{Failure, Success, Try}

import org.joda.time.{DateTime, LocalDate, LocalTime}
import reactivemongo.api.bson.{BSONDateTime, BSONHandler, BSONValue}

trait BsonJodaTimeHandlers {

  implicit object BSONJodaDateTimeHandler extends BSONHandler[DateTime] {

    override def writeTry(t: DateTime): Try[BSONValue] = Success(BSONDateTime(t.getMillis))

    override def readTry(bson: BSONValue): Try[DateTime] = bson.asTry[BSONDateTime] match {
      case Success(bsonDateTime) => Try(new DateTime(bsonDateTime.value))
      case _ => Failure(new IllegalArgumentException(s"expected BSONDateTime, but found $bson"))
    }

  }

  implicit object BSONLocalTimeHandler extends BSONHandler[LocalTime] {

    @nowarn
    override def writeTry(t: LocalTime): Try[BSONValue] = Success(BSONDateTime(t.getMillisOfDay))

    override def readTry(bson: BSONValue): Try[LocalTime] = bson.asTry[BSONDateTime] match {
      case Success(bsonDateTime) => Try(new LocalTime(bsonDateTime.value))
      case _ => Failure(new IllegalArgumentException(s"expected BSONDateTime, but found $bson"))
    }

  }

  implicit object BSONLocalDateHandler extends BSONHandler[LocalDate] {

    override def writeTry(t: LocalDate): Try[BSONValue] =
      Success(BSONDateTime(t.toDateTimeAtStartOfDay.getMillis))

    override def readTry(bson: BSONValue): Try[LocalDate] = bson.asTry[BSONDateTime] match {
      case Success(bsonDateTime) => Try(new LocalDate(bsonDateTime.value))
      case _ => Failure(new IllegalArgumentException(s"expected BSONDateTime, but found $bson"))
    }

  }

}
