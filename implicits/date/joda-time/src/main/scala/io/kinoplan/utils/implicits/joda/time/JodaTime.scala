package io.kinoplan.utils.implicits.joda.time

import scala.util.Try

import org.joda.time.{DateTime, Interval, LocalDate, LocalDateTime, LocalTime}
import org.joda.time.format.DateTimeFormat

object JodaTime {

  object dateTime {
    def fromUnixTimestamp(value: Int): DateTime = new DateTime(value * 1000L)

    def fromUnixTimestamp(value: Long): DateTime = new DateTime(value * 1000L)

    def parse(pattern: String, value: String): Try[DateTime] =
      Try(DateTimeFormat.forPattern(pattern).parseDateTime(value))

    def parseO(pattern: String, value: String): Option[DateTime] = parse(pattern, value).toOption
  }

  object localDate {
    def fromUnixTimestamp(value: Int): LocalDate = new LocalDate(value * 1000L)

    def fromUnixTimestamp(value: Long): LocalDate = new LocalDate(value * 1000L)

    def parse(pattern: String, value: String): Try[LocalDate] =
      Try(DateTimeFormat.forPattern(pattern).parseLocalDate(value))

    def parseO(pattern: String, value: String): Option[LocalDate] = parse(pattern, value).toOption
  }

  object localDateTime {
    def fromUnixTimestamp(value: Int): LocalDateTime = new LocalDateTime(value * 1000L)

    def fromUnixTimestamp(value: Long): LocalDateTime = new LocalDateTime(value * 1000L)

    def parse(pattern: String, value: String): Try[LocalDateTime] =
      Try(DateTimeFormat.forPattern(pattern).parseLocalDateTime(value))

    def parseO(pattern: String, value: String): Option[LocalDateTime] = parse(pattern, value)
      .toOption

  }

  object localTime {
    def fromUnixTimestamp(value: Int): LocalTime = new LocalTime(value * 1000L)

    def fromUnixTimestamp(value: Long): LocalTime = new LocalTime(value * 1000L)

    def parse(pattern: String, value: String): Try[LocalTime] =
      Try(DateTimeFormat.forPattern(pattern).parseLocalTime(value))

    def parseO(pattern: String, value: String): Option[LocalTime] = parse(pattern, value).toOption
  }

  object interval {
    def parse(from: String, to: String): Try[Interval] = Try(Interval.parse(s"$from/$to"))

    def parseO(from: String, to: String): Option[Interval] = parse(from, to).toOption
  }

}
