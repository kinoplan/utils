package io.kinoplan.utils.implicits.joda.time

import scala.util.Try

import org.joda.time.{DateTime, Interval, LocalDateTime, LocalTime}
import org.joda.time.format.DateTimeFormat

object Date {

  def parseDateTime(pattern: String, value: String): Option[DateTime] =
    Try(DateTimeFormat.forPattern(pattern).parseDateTime(value)).toOption

  def parseLocalDateTime(pattern: String, date: String): Option[LocalDateTime] =
    Try(DateTimeFormat.forPattern(pattern).parseLocalDateTime(date)).toOption

  def parseLocalTime(pattern: String, value: String): Option[LocalTime] =
    Try(DateTimeFormat.forPattern(pattern).parseLocalTime(value)).toOption

  def parseInterval(from: String, to: String): Option[Interval] =
    Try(Interval.parse(s"$from/$to")).toOption

}
