package io.kinoplan.utils.implicits.java.time

import java.time.{LocalDate, OffsetDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

import io.kinoplan.utils.date.DatePatternExtension

final private[implicits] class LocalDateOps(private val value: LocalDate)
    extends DatePatternExtension {

  @inline
  def toOffsetDateTime: OffsetDateTime = value.atStartOfDay().atOffset(ZoneOffset.UTC)

  override def toString(pattern: String): String = DateTimeFormatter.ofPattern(pattern).format(value)

}

trait LocalDateSyntax {
  implicit final def syntaxLocalDateOps(value: LocalDate): LocalDateOps = new LocalDateOps(value)
}

object LocalDateSyntax extends LocalDateSyntax
