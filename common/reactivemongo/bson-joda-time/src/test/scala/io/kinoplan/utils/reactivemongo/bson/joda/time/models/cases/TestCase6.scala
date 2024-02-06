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

case class TestCase6(localTime: LocalTime = LocalTime.now, localDate: LocalDate = LocalDate.now)

trait TestCase6Bson extends BsonJodaTimeHandlers {
  override def bsonLocalTimeHandlerStrategy: HandlerStrategy = HandlerStrategy.BSONStringFormat
  implicit val handler: BSONDocumentHandler[TestCase6] = Macros.handler
}

object TestCase6 extends TestCase6Bson {
  val description = "TestCase6#LocalTime HandlerStrategy.BSONStringFormat with LocalDate default"

  def scenario(): TestScenario[TestCase6] = {
    val data = TestCase6(LocalTime.now, LocalDate.now)
    val handler = TestCase6.handler
    val targetField = "localTime"

    val bson = document(
      targetField -> BSONString(data.localTime.toString),
      "localDate" -> BSONDateTime(data.localDate.toDateTimeAtStartOfDay.getMillis)
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
