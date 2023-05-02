package io.kinoplan.utils.reactivemongo.bson.refined

import eu.timepit.refined.types.all.{NonEmptyString, PosInt}
import reactivemongo.api.bson.{BSONDocumentHandler, Macros}

import io.kinoplan.utils.reactivemongo.bson.refined.RefinedCustomTypes.Percent

case class TestData(name: NonEmptyString, level: PosInt, percent: Percent)

trait TestDataBson extends BsonRefinedHandlers {
  implicit val handler: BSONDocumentHandler[TestData] = Macros.handler
}

object TestData extends TestDataBson
