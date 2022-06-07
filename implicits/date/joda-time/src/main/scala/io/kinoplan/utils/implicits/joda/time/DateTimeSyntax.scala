package io.kinoplan.utils.implicits.joda.time

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import io.kinoplan.utils.date.DatePatternExtension

final private[implicits] class DateTimeOps(private val value: DateTime) extends DatePatternExtension {

  @inline def timestamp: Long = value.getMillis / 1000

  override def toString(pattern: String): String = DateTimeFormat.forPattern(pattern).print(value)

}

trait DateTimeSyntax {
  implicit final def syntaxDateTimeOps(value: DateTime): DateTimeOps = new DateTimeOps(value)
}

object DateTimeSyntax extends DateTimeSyntax
