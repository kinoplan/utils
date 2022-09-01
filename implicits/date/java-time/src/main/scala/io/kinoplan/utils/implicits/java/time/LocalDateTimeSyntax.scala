package io.kinoplan.utils.implicits.java.time

import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

import io.kinoplan.utils.date.DateTimePatternExtension

final private[implicits] class LocalDateTimeOps(private val value: LocalDateTime)
    extends DateTimePatternExtension {

  @inline
  def timestamp: Int = timestampLong.toInt

  @inline
  def timestampLong: Long = value.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli / 1000

  override def toString(pattern: String): String = DateTimeFormatter.ofPattern(pattern).format(value)

}

trait LocalDateTimeSyntax {

  implicit final def syntaxLocalDateTimeOps(value: LocalDateTime): LocalDateTimeOps =
    new LocalDateTimeOps(value)

}

object LocalDateTimeSyntax extends LocalDateTimeSyntax
