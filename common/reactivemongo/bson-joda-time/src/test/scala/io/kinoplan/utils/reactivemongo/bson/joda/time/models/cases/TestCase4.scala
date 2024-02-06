package io.kinoplan.utils.reactivemongo.bson.joda.time.models.cases

import org.joda.time.{LocalDate, LocalTime}
import reactivemongo.api.bson._

import io.kinoplan.utils.reactivemongo.bson.HandlerStrategy
import io.kinoplan.utils.reactivemongo.bson.joda.time.BsonJodaTimeHandlers
import io.kinoplan.utils.reactivemongo.bson.joda.time.models.TestScenario

case class TestCase4(localTime: LocalTime = LocalTime.now, localDate: LocalDate = LocalDate.now)

trait TestCase4Bson extends BsonJodaTimeHandlers {

  implicit override val bsonLocalTimeHandler: BSONHandler[LocalTime] = BsonJodaTimeHandlers
    .localTime(HandlerStrategy.BSONStringFormat)

  implicit val handler: BSONDocumentHandler[TestCase4] = Macros.handler
}

object TestCase4 extends TestCase4Bson {
  val description = "TestCase4#LocalTime BSONString with LocalDate default"

  def scenario(): TestScenario[TestCase4] = {
    val data = TestCase4(LocalTime.now, LocalDate.now)
    val handler = TestCase4.handler
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
