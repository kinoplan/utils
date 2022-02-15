package io.kinoplan.utils.reactivemongo.bson.joda.time.data

import org.joda.time.LocalDate
import reactivemongo.api.bson.{BSONDocumentHandler, Macros}

import io.kinoplan.utils.reactivemongo.bson.joda.time.BsonJodaTimeHandlers

case class TestDataLocalDate(value: LocalDate)

trait TestDataLocalDateBson extends BsonJodaTimeHandlers {
  implicit val handler: BSONDocumentHandler[TestDataLocalDate] = Macros.handler
}

object TestDataLocalDate extends TestDataLocalDateBson
