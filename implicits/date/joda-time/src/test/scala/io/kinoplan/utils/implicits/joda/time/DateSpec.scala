package io.kinoplan.utils.implicits.joda.time

import org.joda.time.{DateTime, Interval, LocalDateTime, LocalTime}
import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.date.DatePattern

class DateSpec extends AnyWordSpec {
  val date = "2022-06-07T16:06:54.786"

  "Date#parseDateTimeO" should {
    "return correct value" in
      assert(
        Date
          .parseDateTimeO(DatePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`, date)
          .contains(new DateTime("2022-06-07T16:06:54.786Z"))
      )
  }

  "Date#parseLocalDateTimeO" should {
    "return correct value" in
      assert(
        Date
          .parseLocalDateTimeO(DatePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`, date)
          .contains(new LocalDateTime("2022-06-07T16:06:54.786"))
      )
  }

  "Date#parseLocalTimeO" should {
    "return correct value" in
      assert(
        Date
          .parseLocalTimeO(DatePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`, date)
          .contains(new LocalTime(16, 6, 54, 786))
      )
  }

  "Date#parseIntervalO" should {
    "return correct value" in
      assert(
        Date
          .parseIntervalO("2022-06-07", "2022-06-08")
          .contains(
            new Interval(
              new DateTime("2022-06-07T00:00:00.000Z"),
              new DateTime("2022-06-08T00:00:00.000Z")
            )
          )
      )
  }

}
