package io.kinoplan.utils.reactivemongo.bson.joda.time.models.cases

import org.joda.time.LocalTime
import reactivemongo.api.bson.{BSONArray, BSONDateTime, BSONDocumentHandler, Macros, document}

import io.kinoplan.utils.reactivemongo.bson.joda.time.BsonJodaTimeHandlers
import io.kinoplan.utils.reactivemongo.bson.joda.time.models.TestScenario

case class TestCase3(localTime: LocalTime = LocalTime.now)

trait TestCase3Bson extends BsonJodaTimeHandlers {
  implicit val handler: BSONDocumentHandler[TestCase3] = Macros.handler
}

object TestCase3 extends TestCase3Bson {
  val description = "TestCase3#LocalTime"

  def scenario(): TestScenario[TestCase3] = {
    val data = TestCase3()
    val handler = TestCase3.handler
    val targetField = "localTime"

    val bson = document(targetField -> BSONDateTime(data.localTime.getMillisOfDay.toLong))
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
