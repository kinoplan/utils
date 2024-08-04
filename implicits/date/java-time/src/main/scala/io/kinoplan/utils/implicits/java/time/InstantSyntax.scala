package io.kinoplan.utils.implicits.java.time

import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter

import io.kinoplan.utils.date.DateTimePatternExtension

final private[implicits] class InstantOps(private val value: Instant)
    extends DateTimePatternExtension {

  @inline
  def timestamp: Int = timestampLong.toInt

  @inline
  def timestampLong: Long = value.toEpochMilli / 1000

  override def toString(pattern: String): String = DateTimeFormatter
    .ofPattern(pattern)
    .withZone(ZoneId.of("UTC"))
    .format(value)

}

trait InstantSyntax {
  implicit final def syntaxInstantOps(value: Instant): InstantOps = new InstantOps(value)
}

object InstantSyntax extends InstantSyntax
