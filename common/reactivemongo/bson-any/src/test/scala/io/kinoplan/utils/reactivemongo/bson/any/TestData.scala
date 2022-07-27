package io.kinoplan.utils.reactivemongo.bson.any

import reactivemongo.api.bson.{BSONDocumentHandler, Macros}

case class TestData(value: Any)

trait TestDataBson extends BsonAnyHandlers {
  implicit val handler: BSONDocumentHandler[TestData] = Macros.handler
}

object TestData extends TestDataBson
