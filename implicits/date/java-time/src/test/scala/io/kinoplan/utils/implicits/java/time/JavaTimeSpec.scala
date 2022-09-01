package io.kinoplan.utils.implicits.java.time

import java.time.{LocalDate, LocalDateTime, LocalTime, OffsetDateTime, OffsetTime}
import java.util.TimeZone

import org.scalatest.wordspec.AnyWordSpec

class JavaTimeSpec extends AnyWordSpec {
  val offsetDateTime = "2022-06-07T16:06:54Z"
  val localDateTime = "2022-06-07T16:06:54"
  val localDate = "2022-06-07"
  val localTime = "16:06:54"
  val offsetTime = "16:06:54Z"

  TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))

  "offsetDateTime#fromUnixTimestamp" should {
    "return correct value" in {
      assert(
        JavaTime
          .offsetDateTime
          .fromUnixTimestampO(1661898297)
          .contains(OffsetDateTime.parse("2022-08-31T01:24:57+03:00"))
      )
      assert(
        JavaTime
          .offsetDateTime
          .fromUnixTimestampO(1661898297L)
          .contains(OffsetDateTime.parse("2022-08-31T01:24:57+03:00"))
      )
      assert(JavaTime.offsetDateTime.fromUnixTimestampO(0).isEmpty)
      assert(JavaTime.offsetDateTime.fromUnixTimestampO(-1).isEmpty)
    }
  }

  "offsetDateTime#parseO" should {
    "return correct value" in
      assert(
        JavaTime
          .offsetDateTime
          .parseO(offsetDateTime)
          .contains(OffsetDateTime.parse("2022-06-07T16:06:54Z"))
      )
  }

  "localDate#fromUnixTimestamp" should {
    "return correct value" in {
      assert(JavaTime.localDate.fromUnixTimestampO(1661898297).contains(LocalDate.parse("2022-08-31")))
      assert(
        JavaTime.localDate.fromUnixTimestampO(1661898297L).contains(LocalDate.parse("2022-08-31"))
      )
      assert(JavaTime.localDate.fromUnixTimestampO(0).isEmpty)
      assert(JavaTime.localDate.fromUnixTimestampO(-1).isEmpty)
    }
  }

  "localDate#parseO" should {
    "return correct value" in
      assert(JavaTime.localDate.parseO(localDate).contains(LocalDate.parse("2022-06-07")))
  }

  "localDateTime#fromUnixTimestamp" should {
    "return correct value" in {
      assert(
        JavaTime
          .localDateTime
          .fromUnixTimestampO(1661898297)
          .contains(LocalDateTime.parse("2022-08-31T01:24:57"))
      )
      assert(
        JavaTime
          .localDateTime
          .fromUnixTimestampO(1661898297L)
          .contains(LocalDateTime.parse("2022-08-31T01:24:57"))
      )
      assert(JavaTime.localDateTime.fromUnixTimestampO(0).isEmpty)
      assert(JavaTime.localDateTime.fromUnixTimestampO(-1).isEmpty)
    }
  }

  "localDateTime#parseO" should {
    "return correct value" in
      assert(
        JavaTime
          .localDateTime
          .parseO(localDateTime)
          .contains(LocalDateTime.parse("2022-06-07T16:06:54"))
      )
  }

  "localTime#fromUnixTimestamp" should {
    "return correct value" in {
      assert(JavaTime.localTime.fromUnixTimestampO(1661898297).contains(LocalTime.parse("01:24:57")))
      assert(JavaTime.localTime.fromUnixTimestampO(1661898297L).contains(LocalTime.parse("01:24:57")))
      assert(JavaTime.localTime.fromUnixTimestampO(0).isEmpty)
      assert(JavaTime.localTime.fromUnixTimestampO(-1).isEmpty)
    }
  }

  "localTime#parseO" should {
    "return correct value" in
      assert(JavaTime.localTime.parseO(localTime).contains(LocalTime.parse("16:06:54")))
  }

  "offsetTime#fromUnixTimestamp" should {
    "return correct value" in {
      assert(
        JavaTime
          .offsetTime
          .fromUnixTimestampO(1661898297)
          .contains(OffsetTime.parse("01:24:57+03:00"))
      )
      assert(
        JavaTime
          .offsetTime
          .fromUnixTimestampO(1661898297L)
          .contains(OffsetTime.parse("01:24:57+03:00"))
      )
      assert(JavaTime.offsetTime.fromUnixTimestampO(0).isEmpty)
      assert(JavaTime.offsetTime.fromUnixTimestampO(-1).isEmpty)
    }
  }

  "offsetTime#parseO" should {
    "return correct value" in
      assert(JavaTime.offsetTime.parseO(offsetTime).contains(OffsetTime.parse("16:06:54Z")))
  }

}
