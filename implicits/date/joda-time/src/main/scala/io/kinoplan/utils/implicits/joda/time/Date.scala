package io.kinoplan.utils.implicits.joda.time

import scala.util.Try

import org.joda.time.{DateTime, Interval, LocalDateTime, LocalTime}
import org.joda.time.format.DateTimeFormat

object Date {

  def fromUnixTimestamp(value: Int): DateTime = new DateTime(value * 1000L)

  def fromUnixTimestamp(value: Long): DateTime = new DateTime(value * 1000L)

  def parseDateTime(pattern: String, value: String): Try[DateTime] =
    Try(DateTimeFormat.forPattern(pattern).parseDateTime(value))

  def parseLocalDateTime(pattern: String, date: String): Try[LocalDateTime] =
    Try(DateTimeFormat.forPattern(pattern).parseLocalDateTime(date))

  def parseLocalTime(pattern: String, value: String): Try[LocalTime] =
    Try(DateTimeFormat.forPattern(pattern).parseLocalTime(value))

  def parseInterval(from: String, to: String): Try[Interval] = Try(Interval.parse(s"$from/$to"))

  def parseDateTimeO(pattern: String, value: String): Option[DateTime] =
    parseDateTime(pattern, value).toOption

  def parseLocalDateTimeO(pattern: String, value: String): Option[LocalDateTime] =
    parseLocalDateTime(pattern, value).toOption

  def parseLocalTimeO(pattern: String, value: String): Option[LocalTime] =
    parseLocalTime(pattern, value).toOption

  def parseIntervalO(from: String, to: String): Option[Interval] = parseInterval(from, to).toOption

}
