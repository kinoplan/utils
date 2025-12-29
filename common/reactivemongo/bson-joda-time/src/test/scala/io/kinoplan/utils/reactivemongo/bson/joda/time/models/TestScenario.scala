package io.kinoplan.utils.reactivemongo.bson.joda.time.models

import reactivemongo.api.bson.{BSONDocument, BSONDocumentHandler}

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
    exceptionMessage: String
  ): TestScenario[T] = TestScenario(data, handler, bson, bsonIncorrect, exceptionMessage)

}
