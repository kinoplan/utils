package io.kinoplan.utils.implicits.java.time

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import io.kinoplan.utils.date.DateTimePatternExtension

final private[implicits] class OffsetDateTimeOps(private val value: OffsetDateTime)
    extends DateTimePatternExtension {

  @inline
  def timestamp: Int = timestampLong.toInt

  @inline
  def timestampLong: Long = value.toInstant.toEpochMilli / 1000

  override def toString(pattern: String): String = DateTimeFormatter.ofPattern(pattern).format(value)

}

trait OffsetDateTimeSyntax {

  implicit final def syntaxOffsetDateTimeOps(value: OffsetDateTime): OffsetDateTimeOps =
    new OffsetDateTimeOps(value)

}

object OffsetDateTimeSyntax extends OffsetDateTimeSyntax
