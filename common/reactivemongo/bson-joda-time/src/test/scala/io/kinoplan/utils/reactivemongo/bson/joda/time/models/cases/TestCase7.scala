package io.kinoplan.utils.reactivemongo.bson.joda.time.models.cases

import org.joda.time.{LocalDate, LocalTime}
import reactivemongo.api.bson.{
  BSONArray,
  BSONDateTime,
  BSONDocumentHandler,
  BSONString,
  Macros,
  document
}

import io.kinoplan.utils.reactivemongo.bson.HandlerStrategy
import io.kinoplan.utils.reactivemongo.bson.joda.time.BsonJodaTimeHandlers
import io.kinoplan.utils.reactivemongo.bson.joda.time.models.TestScenario

case class TestCase7(localDate: LocalDate = LocalDate.now, localTime: LocalTime = LocalTime.now)

trait TestCase7Bson extends BsonJodaTimeHandlers {
  override def bsonLocalDateHandlerStrategy: HandlerStrategy = HandlerStrategy.BSONStringFormat
  implicit val handler: BSONDocumentHandler[TestCase7] = Macros.handler
}

object TestCase7 extends TestCase7Bson {
  val description = "TestCase7#LocalDate HandlerStrategy.BSONStringFormat with LocalTime default"

  def scenario(): TestScenario[TestCase7] = {
    val data = TestCase7()
    val handler = TestCase7.handler
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
