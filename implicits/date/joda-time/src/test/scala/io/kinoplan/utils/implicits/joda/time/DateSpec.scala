package io.kinoplan.utils.implicits.joda.time

import org.joda.time.{DateTime, Interval, LocalDateTime, LocalTime}
import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.date.DatePattern

class DateSpec extends AnyWordSpec {
  val date = "2022-06-07T16:06:54.786"

  "Date#parseDateTime" should {
    "return correct value" in {
      assert(
        Date.parseDateTime(DatePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`, date).contains(
          new DateTime("2022-06-07T16:06:54.786Z")
        )
      )
    }
  }

  "Date#parseLocalDateTime" should {
    "return correct value" in {
      assert(
        Date.parseLocalDateTime(DatePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`, date).contains(
          new LocalDateTime("2022-06-07T16:06:54.786")
        )
      )
    }
  }

  "Date#parseLocalTime" should {
    "return correct value" in {
      assert(
        Date.parseLocalTime(DatePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`, date).contains(new LocalTime(
          16,
          6,
          54,
          786
        ))
      )
    }
  }

  "Date#parseInterval" should {
    "return correct value" in {
      assert(
        Date.parseInterval("2022-06-07", "2022-06-08").contains(
          new Interval(
            new DateTime("2022-06-07T00:00:00.000Z"),
            new DateTime("2022-06-08T00:00:00.000Z")
          )
        )
      )
    }
  }

}
