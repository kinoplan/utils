package io.kinoplan.utils.implicits.joda.time

import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat

import io.kinoplan.utils.date.DateTimePatternExtension

final private[implicits] class LocalDateTimeOps(private val value: LocalDateTime)
    extends DateTimePatternExtension {

  override def toString(pattern: String): String = DateTimeFormat.forPattern(pattern).print(value)

}

trait LocalDateTimeSyntax {

  implicit final def syntaxLocalDateTimeOps(value: LocalDateTime): LocalDateTimeOps =
    new LocalDateTimeOps(value)

}

object LocalDateTimeSyntax extends LocalDateTimeSyntax
