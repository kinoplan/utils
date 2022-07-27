package io.kinoplan.utils.reactivemongo.bson.joda.time.data

import org.joda.time.DateTime
import reactivemongo.api.bson.{BSONDocumentHandler, Macros}

import io.kinoplan.utils.reactivemongo.bson.joda.time.BsonJodaTimeHandlers

case class TestDataDateTime(value: DateTime)

trait TestDataDateTimeBson extends BsonJodaTimeHandlers {
  implicit val handler: BSONDocumentHandler[TestDataDateTime] = Macros.handler
}

object TestDataDateTime extends TestDataDateTimeBson
