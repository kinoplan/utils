package io.kinoplan.utils.reactivemongo.bson.joda.time.models.cases

import org.joda.time.LocalDate
import reactivemongo.api.bson.{BSONArray, BSONDateTime, BSONDocumentHandler, Macros, document}

import io.kinoplan.utils.reactivemongo.bson.joda.time.BsonJodaTimeHandlers
import io.kinoplan.utils.reactivemongo.bson.joda.time.models.TestScenario

case class TestCase2(localDate: LocalDate = LocalDate.now)

trait TestCase2Bson extends BsonJodaTimeHandlers {
  implicit val handler: BSONDocumentHandler[TestCase2] = Macros.handler
}

object TestCase2 extends TestCase2Bson {
  val description = "TestCase2#LocalDate"

  def scenario(): TestScenario[TestCase2] = {
    val data = TestCase2()
    val handler = TestCase2.handler
    val targetField = "localDate"

    val bson = document(targetField -> BSONDateTime(data.localDate.toDateTimeAtStartOfDay.getMillis))
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
