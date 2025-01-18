package io.kinoplan.utils.reactivemongo.bson.joda.time

import scala.util.{Failure, Success, Try}

import org.joda.time.{DateTime, LocalDate, LocalTime}
import reactivemongo.api.bson.{BSONDateTime, BSONHandler, BSONString, BSONValue}

import io.kinoplan.utils.reactivemongo.bson.HandlerStrategy

trait BsonJodaTimeHandlers {

  def bsonLocalTimeHandlerStrategy: HandlerStrategy = HandlerStrategy.BSONDateTimeFormat
  def bsonLocalDateHandlerStrategy: HandlerStrategy = HandlerStrategy.BSONDateTimeFormat

  implicit val bsonDateTimeHandler: BSONHandler[DateTime] = BsonJodaTimeHandlers.dateTime

  implicit val bsonLocalTimeHandler: BSONHandler[LocalTime] =
    BsonJodaTimeHandlers.localTime(bsonLocalTimeHandlerStrategy)

  implicit val bsonLocalDateHandler: BSONHandler[LocalDate] =
    BsonJodaTimeHandlers.localDate(bsonLocalDateHandlerStrategy)

}

object BsonJodaTimeHandlers {

  private def onFailure(bson: BSONValue, handlerStrategy: HandlerStrategy) = Failure(
    new IllegalArgumentException(s"expected ${handlerStrategy.expectedName}, but found $bson")
  )

  def dateTime: BSONHandler[DateTime] = BSONHandler.from[DateTime](
    {
      case BSONDateTime(value) => Try(new DateTime(value))
      case bson: BSONValue     => onFailure(bson, HandlerStrategy.BSONDateTimeFormat)
    },
    t => Success(BSONDateTime(t.getMillis))
  )

  def localTime(
    handlerStrategy: HandlerStrategy = HandlerStrategy.BSONDateTimeFormat
  ): BSONHandler[LocalTime] = handlerStrategy match {
    case HandlerStrategy.BSONStringFormat => BSONHandler.from[LocalTime](
        {
          case BSONString(value) => Try(LocalTime.parse(value))
          case bson: BSONValue   => onFailure(bson, handlerStrategy)
        },
        t => Success(BSONString(t.toString))
      )
    case _ => BSONHandler.from[LocalTime](
        {
          case BSONDateTime(value) => Try(new LocalTime(value))
          case bson: BSONValue     => onFailure(bson, handlerStrategy)
        },
        t => Success(BSONDateTime(t.getMillisOfDay.toLong))
      )
  }

  def localDate(
    handlerStrategy: HandlerStrategy = HandlerStrategy.BSONDateTimeFormat
  ): BSONHandler[LocalDate] = handlerStrategy match {
    case HandlerStrategy.BSONStringFormat => BSONHandler.from[LocalDate](
        {
          case BSONString(value) => Try(LocalDate.parse(value))
          case bson: BSONValue   => onFailure(bson, handlerStrategy)
        },
        t => Success(BSONString(t.toString))
      )
    case _ => BSONHandler.from[LocalDate](
        {
          case BSONDateTime(value) => Try(new LocalDate(value))
          case bson: BSONValue     => onFailure(bson, handlerStrategy)
        },
        t => Success(BSONDateTime(t.toDateTimeAtStartOfDay.getMillis))
      )
  }

}
