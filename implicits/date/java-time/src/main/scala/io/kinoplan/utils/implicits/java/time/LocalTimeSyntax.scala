package io.kinoplan.utils.implicits.java.time

import java.time.LocalTime
import java.time.format.DateTimeFormatter

import io.kinoplan.utils.date.TimePatternExtension

final private[implicits] class LocalTimeOps(private val value: LocalTime)
    extends TimePatternExtension {

  override def toString(pattern: String): String = DateTimeFormatter.ofPattern(pattern).format(value)

}

trait LocalTimeSyntax {
  implicit final def syntaxLocalTimeOps(value: LocalTime): LocalTimeOps = new LocalTimeOps(value)
}

object LocalTimeSyntax extends LocalTimeSyntax
