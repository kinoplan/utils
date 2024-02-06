package io.kinoplan.utils.reactivemongo.bson.joda.time.models.cases

import org.joda.time.{LocalDate, LocalTime}
import reactivemongo.api.bson._

import io.kinoplan.utils.reactivemongo.bson.HandlerStrategy
import io.kinoplan.utils.reactivemongo.bson.joda.time.BsonJodaTimeHandlers
import io.kinoplan.utils.reactivemongo.bson.joda.time.models.TestScenario

case class TestCase5(localDate: LocalDate = LocalDate.now, localTime: LocalTime = LocalTime.now)

trait TestCase5Bson extends BsonJodaTimeHandlers {

  implicit override val bsonLocalDateHandler: BSONHandler[LocalDate] = BsonJodaTimeHandlers
    .localDate(HandlerStrategy.BSONStringFormat)

  implicit val handler: BSONDocumentHandler[TestCase5] = Macros.handler
}

object TestCase5 extends TestCase5Bson {
  val description = "TestCase5#LocalTime default with LocalDate BSONString"

  def scenario(): TestScenario[TestCase5] = {
    val data = TestCase5()
    val handler = TestCase5.handler
    val targetField = "localDate"

    val bson = document(
      targetField -> BSONString(data.localDate.toString),
      "localTime" -> BSONDateTime(data.localTime.getMillisOfDay.toLong)
    )
    val bsonIncorrect = document(targetField -> BSONArray.empty)

    TestScenario.create(
      data,
      handler,
      bson,
      bsonIncorrect,
      exceptionField = targetField,
      exceptionMessage = s"expected BSONString, but found ${BSONArray.empty}"
    )
  }

}
