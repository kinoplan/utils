package io.kinoplan.utils.implicits.joda.time

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

import io.kinoplan.utils.date.DatePatternExtension

final private[implicits] class LocalDateOps(private val value: LocalDate)
    extends DatePatternExtension {

  override def toString(pattern: String): String = DateTimeFormat.forPattern(pattern).print(value)

}

trait LocalDateSyntax {
  implicit final def syntaxLocalDateOps(value: LocalDate): LocalDateOps = new LocalDateOps(value)
}

object LocalDateSyntax extends LocalDateSyntax
