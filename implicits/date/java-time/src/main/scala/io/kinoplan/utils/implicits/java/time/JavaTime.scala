package io.kinoplan.utils.implicits.java.time

import java.time._

import scala.util.Try

object JavaTime {

  object offsetDateTime {

    def fromUnixTimestamp(value: Int): OffsetDateTime = fromUnixTimestamp(value.toLong)

    def fromUnixTimestamp(value: Long): OffsetDateTime = OffsetDateTime
      .ofInstant(Instant.ofEpochSecond(value), ZoneId.systemDefault())

    def fromUnixTimestampO(value: Int): Option[OffsetDateTime] = fromUnixTimestampO(value.toLong)

    def fromUnixTimestampO(value: Long): Option[OffsetDateTime] =
      if (value > 0) Some(fromUnixTimestamp(value))
      else None

    def parse(value: String): Try[OffsetDateTime] = Try(OffsetDateTime.parse(value))

    def parseO(value: String): Option[OffsetDateTime] = parse(value).toOption

  }

  object localDate {

    def fromUnixTimestamp(value: Int): LocalDate = offsetDateTime
      .fromUnixTimestamp(value)
      .toLocalDate

    def fromUnixTimestamp(value: Long): LocalDate = offsetDateTime
      .fromUnixTimestamp(value)
      .toLocalDate

    def fromUnixTimestampO(value: Int): Option[LocalDate] = fromUnixTimestampO(value.toLong)

    def fromUnixTimestampO(value: Long): Option[LocalDate] =
      if (value > 0) Some(fromUnixTimestamp(value))
      else None

    def parse(value: String): Try[LocalDate] = Try(LocalDate.parse(value))

    def parseO(value: String): Option[LocalDate] = parse(value).toOption

  }

  object localDateTime {
    def fromUnixTimestamp(value: Int): LocalDateTime = fromUnixTimestamp(value.toLong)

    def fromUnixTimestamp(value: Long): LocalDateTime = LocalDateTime
      .ofInstant(Instant.ofEpochSecond(value), ZoneId.systemDefault())

    def fromUnixTimestampO(value: Int): Option[LocalDateTime] = fromUnixTimestampO(value.toLong)

    def fromUnixTimestampO(value: Long): Option[LocalDateTime] =
      if (value > 0) Some(fromUnixTimestamp(value))
      else None

    def parse(value: String): Try[LocalDateTime] = Try(LocalDateTime.parse(value))

    def parseO(value: String): Option[LocalDateTime] = parse(value).toOption

  }

  object localTime {

    def fromUnixTimestamp(value: Int): LocalTime = offsetDateTime
      .fromUnixTimestamp(value)
      .toLocalTime

    def fromUnixTimestamp(value: Long): LocalTime = offsetDateTime
      .fromUnixTimestamp(value)
      .toLocalTime

    def fromUnixTimestampO(value: Int): Option[LocalTime] = fromUnixTimestampO(value.toLong)

    def fromUnixTimestampO(value: Long): Option[LocalTime] =
      if (value > 0) Some(fromUnixTimestamp(value))
      else None

    def parse(value: String): Try[LocalTime] = Try(LocalTime.parse(value))

    def parseO(value: String): Option[LocalTime] = parse(value).toOption

  }

  object offsetTime {

    def fromUnixTimestamp(value: Int): OffsetTime = offsetDateTime
      .fromUnixTimestamp(value)
      .toOffsetTime

    def fromUnixTimestamp(value: Long): OffsetTime = offsetDateTime
      .fromUnixTimestamp(value)
      .toOffsetTime

    def fromUnixTimestampO(value: Int): Option[OffsetTime] = fromUnixTimestampO(value.toLong)

    def fromUnixTimestampO(value: Long): Option[OffsetTime] =
      if (value > 0) Some(fromUnixTimestamp(value))
      else None

    def parse(value: String): Try[OffsetTime] = Try(OffsetTime.parse(value))

    def parseO(value: String): Option[OffsetTime] = parse(value).toOption

  }

}
