package io.kinoplan.utils.implicits.joda.time

import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

import io.kinoplan.utils.date.TimePatternExtension

final private[implicits] class LocalTimeOps(private val value: LocalTime)
    extends TimePatternExtension {

  override def toString(pattern: String): String = DateTimeFormat.forPattern(pattern).print(value)

}

trait LocalTimeSyntax {
  implicit final def syntaxLocalTimeOps(value: LocalTime): LocalTimeOps = new LocalTimeOps(value)
}

object LocalTimeSyntax extends LocalTimeSyntax
