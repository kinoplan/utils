package io.kinoplan.utils.implicits.java.time

import java.time.{Instant, OffsetTime, ZoneId, ZoneOffset}
import java.time.format.DateTimeFormatter

import io.kinoplan.utils.date.TimePatternExtension

final private[implicits] class OffsetTimeOps(private val value: OffsetTime)
    extends TimePatternExtension {

  override def toString(pattern: String): String = {
    val normalizedValue =
      if (value.getOffset == ZoneOffset.UTC) {
        val zoneId = ZoneId.systemDefault()
        if (zoneId.getId == "GMT" || zoneId.getId == "UTC") value
        else value.withOffsetSameInstant(zoneId.getRules.getOffset(Instant.now()))
      } else value

    DateTimeFormatter.ofPattern(pattern).format(normalizedValue)
  }

}

trait OffsetTimeSyntax {
  implicit final def syntaxOffsetTimeOps(value: OffsetTime): OffsetTimeOps = new OffsetTimeOps(value)
}

object OffsetTimeSyntax extends OffsetTimeSyntax
