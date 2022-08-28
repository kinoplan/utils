package io.kinoplan.utils.implicits.joda.time

import org.joda.time.{DateTime, Interval, LocalDate, LocalDateTime, LocalTime}
import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.date.DateTimePattern

class JodaTimeSpec extends AnyWordSpec {
  val date = "2022-06-07T16:06:54.786"

  "dateTime#fromUnixTimestamp" should {
    "return correct value" in {
      assert(JodaTime.dateTime.fromUnixTimestamp(1661568682) == new DateTime(1661568682 * 1000L))
      assert(JodaTime.dateTime.fromUnixTimestamp(1661568682L) == new DateTime(1661568682L * 1000L))
      assert(JodaTime.dateTime.fromUnixTimestamp(1661568682) == new DateTime("2022-08-27T02:51:22"))
      assert(JodaTime.dateTime.fromUnixTimestamp(1661568682L) == new DateTime("2022-08-27T02:51:22"))
    }
  }

  "dateTime#parseO" should {
    "return correct value" in
      assert(
        JodaTime
          .dateTime
          .parseO(DateTimePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`, date)
          .contains(new DateTime("2022-06-07T16:06:54.786Z"))
      )
  }

  "localDate#fromUnixTimestamp" should {
    "return correct value" in {
      assert(JodaTime.localDate.fromUnixTimestamp(1661568682) == new LocalDate(1661568682 * 1000L))
      assert(JodaTime.localDate.fromUnixTimestamp(1661568682L) == new LocalDate(1661568682L * 1000L))
      assert(JodaTime.localDate.fromUnixTimestamp(1661568682) == new LocalDate("2022-08-27"))
      assert(JodaTime.localDate.fromUnixTimestamp(1661568682L) == new LocalDate("2022-08-27"))
    }
  }

  "localDate#parseO" should {
    "return correct value" in
      assert(
        JodaTime
          .localDate
          .parseO(DateTimePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`, date)
          .contains(new LocalDate("2022-06-07"))
      )
  }

  "localDateTime#fromUnixTimestamp" should {
    "return correct value" in {
      assert(
        JodaTime.localDateTime.fromUnixTimestamp(1661568682) == new LocalDateTime(1661568682 * 1000L)
      )
      assert(
        JodaTime.localDateTime.fromUnixTimestamp(1661568682L) ==
          new LocalDateTime(1661568682L * 1000L)
      )
      assert(
        JodaTime.localDateTime.fromUnixTimestamp(1661568682) ==
          new LocalDateTime("2022-08-27T02:51:22")
      )
      assert(
        JodaTime.localDateTime.fromUnixTimestamp(1661568682L) ==
          new LocalDateTime("2022-08-27T02:51:22")
      )
    }
  }

  "localDateTime#parseO" should {
    "return correct value" in
      assert(
        JodaTime
          .localDateTime
          .parseO(DateTimePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`, date)
          .contains(new LocalDateTime("2022-06-07T16:06:54.786"))
      )
  }

  "localTime#fromUnixTimestamp" should {
    "return correct value" in {
      assert(JodaTime.localTime.fromUnixTimestamp(1661568682) == new LocalTime(1661568682 * 1000L))
      assert(JodaTime.localTime.fromUnixTimestamp(1661568682L) == new LocalTime(1661568682L * 1000L))
      assert(JodaTime.localTime.fromUnixTimestamp(1661568682) == new LocalTime("02:51:22"))
      assert(JodaTime.localTime.fromUnixTimestamp(1661568682L) == new LocalTime("02:51:22"))
    }
  }

  "localTime#parseO" should {
    "return correct value" in
      assert(
        JodaTime
          .localTime
          .parseO(DateTimePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`, date)
          .contains(new LocalTime("16:06:54.786"))
      )
  }

  "interval#parse" should {
    "return correct value" in
      assert(
        JodaTime
          .interval
          .parseO("2022-06-07", "2022-06-08")
          .contains(
            new Interval(
              new DateTime("2022-06-07T00:00:00.000Z"),
              new DateTime("2022-06-08T00:00:00.000Z")
            )
          )
      )
  }

}
