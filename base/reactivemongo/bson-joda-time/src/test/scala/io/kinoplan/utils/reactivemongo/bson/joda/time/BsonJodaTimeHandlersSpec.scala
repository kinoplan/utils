package io.kinoplan.utils.reactivemongo.bson.joda.time

import scala.annotation.nowarn

import org.joda.time.{DateTime, LocalDate, LocalTime}
import org.scalatest.wordspec.AnyWordSpec
import reactivemongo.api.bson.{BSONArray, BSONDateTime, document}
import reactivemongo.api.bson.exceptions.HandlerException

import io.kinoplan.utils.reactivemongo.bson.joda.time.data.{
  TestDataDateTime,
  TestDataLocalDate,
  TestDataLocalTime
}

class BsonJodaTimeHandlersSpec extends AnyWordSpec {

  "DateTime" should {
    val dateTime: DateTime = DateTime.now
    val bsonDateTime: BSONDateTime = BSONDateTime(dateTime.getMillis)

    "return correct value writeTry" in {
      assert(
        TestDataDateTime.handler.writeTry(TestDataDateTime(dateTime)).toOption.contains(
          document(
            "value" -> bsonDateTime
          )
        )
      )
    }
    "return correct value readTry" in {
      assert(
        TestDataDateTime.handler.readTry(document("value" -> bsonDateTime)).toOption.contains(
          TestDataDateTime(dateTime)
        )
      )
    }
    "return incorrect value readTry" in {
      assert(
        TestDataDateTime.handler.readTry(document("value" -> BSONArray.empty)).failed.toOption.map(
          _.getMessage
        ).contains(
          HandlerException(
            "value",
            new IllegalArgumentException(s"expected BSONDateTime, but found ${BSONArray.empty}")
          ).getMessage
        )
      )
    }
  }

  "LocalTime" should {
    val localTime: LocalTime = LocalTime.now
    @nowarn
    val bsonDateTime: BSONDateTime = BSONDateTime(localTime.getMillisOfDay)

    "return correct value writeTry" in {
      assert(
        TestDataLocalTime.handler.writeTry(TestDataLocalTime(localTime)).toOption.contains(
          document(
            "value" -> bsonDateTime
          )
        )
      )
    }
    "return correct value readTry" in {
      assert(
        TestDataLocalTime.handler.readTry(document("value" -> bsonDateTime)).toOption.contains(
          TestDataLocalTime(localTime)
        )
      )
    }
    "return incorrect value readTry" in {
      assert(
        TestDataLocalTime.handler.readTry(document("value" -> BSONArray.empty)).failed.toOption.map(
          _.getMessage
        ).contains(
          HandlerException(
            "value",
            new IllegalArgumentException(s"expected BSONDateTime, but found ${BSONArray.empty}")
          ).getMessage
        )
      )
    }
  }

  "LocalDate" should {
    val localDate: LocalDate = LocalDate.now
    val bsonDateTime: BSONDateTime = BSONDateTime(localDate.toDateTimeAtStartOfDay.getMillis)

    "return correct value writeTry" in {
      assert(
        TestDataLocalDate.handler.writeTry(TestDataLocalDate(localDate)).toOption.contains(
          document(
            "value" -> bsonDateTime
          )
        )
      )
    }
    "return correct value readTry" in {
      assert(
        TestDataLocalDate.handler.readTry(document("value" -> bsonDateTime)).toOption.contains(
          TestDataLocalDate(localDate)
        )
      )
    }
    "return incorrect value readTry" in {
      assert(
        TestDataLocalDate.handler.readTry(document("value" -> BSONArray.empty)).failed.toOption.map(
          _.getMessage
        ).contains(
          HandlerException(
            "value",
            new IllegalArgumentException(s"expected BSONDateTime, but found ${BSONArray.empty}")
          ).getMessage
        )
      )
    }
  }

}
