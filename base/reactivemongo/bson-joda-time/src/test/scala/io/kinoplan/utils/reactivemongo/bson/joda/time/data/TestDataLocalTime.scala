package io.kinoplan.utils.reactivemongo.bson.joda.time.data

import org.joda.time.LocalTime
import reactivemongo.api.bson.{BSONDocumentHandler, Macros}

import io.kinoplan.utils.reactivemongo.bson.joda.time.BsonJodaTimeHandlers

case class TestDataLocalTime(value: LocalTime)

trait TestDataLocalTimeBson extends BsonJodaTimeHandlers {
  implicit val handler: BSONDocumentHandler[TestDataLocalTime] = Macros.handler
}

object TestDataLocalTime extends TestDataLocalTimeBson
