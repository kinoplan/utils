package io.kinoplan.utils.reactivemongo.bson.joda.time.models.cases

import org.joda.time.DateTime
import reactivemongo.api.bson.{BSONArray, BSONDateTime, BSONDocumentHandler, Macros, document}

import io.kinoplan.utils.reactivemongo.bson.joda.time.BsonJodaTimeHandlers
import io.kinoplan.utils.reactivemongo.bson.joda.time.models.TestScenario

case class TestCase1(dateTime: DateTime = DateTime.now)

trait TestCase1Bson extends BsonJodaTimeHandlers {
  implicit val handler: BSONDocumentHandler[TestCase1] = Macros.handler
}

object TestCase1 extends TestCase1Bson {
  val description = "TestCase1#dateTime"

  def scenario(): TestScenario[TestCase1] = {
    val data = TestCase1()
    val handler = TestCase1.handler
    val targetField = "dateTime"

    val bson = document(targetField -> BSONDateTime(data.dateTime.getMillis))
    val bsonIncorrect = document(targetField -> BSONArray.empty)

    TestScenario.create(
      data,
      handler,
      bson,
      bsonIncorrect,
      exceptionField = targetField,
      exceptionMessage = s"expected BSONDateTime, but found ${BSONArray.empty}"
    )
  }

}
