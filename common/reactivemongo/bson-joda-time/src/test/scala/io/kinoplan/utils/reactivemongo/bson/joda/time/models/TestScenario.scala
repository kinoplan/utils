package io.kinoplan.utils.reactivemongo.bson.joda.time.models

import reactivemongo.api.bson.{BSONDocument, BSONDocumentHandler}
import reactivemongo.api.bson.exceptions.HandlerException

case class TestScenario[T](
  data: T,
  handler: BSONDocumentHandler[T],
  bson: BSONDocument,
  bsonIncorrect: BSONDocument,
  exceptionMessage: String
)

object TestScenario {

  def create[T](
    data: T,
    handler: BSONDocumentHandler[T],
    bson: BSONDocument,
    bsonIncorrect: BSONDocument,
    exceptionField: String,
    exceptionMessage: String
  ): TestScenario[T] = TestScenario(
    data,
    handler,
    bson,
    bsonIncorrect,
    HandlerException(exceptionField, new IllegalArgumentException(exceptionMessage)).getMessage
  )

}
