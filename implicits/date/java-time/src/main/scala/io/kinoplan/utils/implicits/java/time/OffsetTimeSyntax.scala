package io.kinoplan.utils.implicits.java.time

import java.time.OffsetTime
import java.time.format.DateTimeFormatter

import io.kinoplan.utils.date.TimePatternExtension

final private[implicits] class OffsetTimeOps(private val value: OffsetTime)
    extends TimePatternExtension {

  override def toString(pattern: String): String = DateTimeFormatter.ofPattern(pattern).format(value)

}

trait OffsetTimeSyntax {
  implicit final def syntaxOffsetTimeOps(value: OffsetTime): OffsetTimeOps = new OffsetTimeOps(value)
}

object OffsetTimeSyntax extends OffsetTimeSyntax
