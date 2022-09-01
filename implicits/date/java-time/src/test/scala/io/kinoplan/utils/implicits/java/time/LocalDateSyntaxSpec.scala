package io.kinoplan.utils.implicits.java.time

import java.time.{LocalDate, OffsetDateTime}
import java.util.{Locale, TimeZone}

import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.implicits.java.time.LocalDateSyntax.syntaxLocalDateOps

class LocalDateSyntaxSpec extends AnyWordSpec {
  def setLocale(lang: String): Unit = Locale.setDefault(Locale.forLanguageTag(lang))

  TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))

  val date: LocalDate = LocalDate.parse("2022-06-07")

  "toOffsetDateTime" should {
    "return correct value" in
      assert(date.toOffsetDateTime === OffsetDateTime.parse("2022-06-07T00:00:00.00Z"))
  }

  "`yyyy-MM-dd`" should {
    "return correct value" in assert(date.`yyyy-MM-dd` === "2022-06-07")
  }

  "`dd-MM-yyyy`" should {
    "return correct value" in assert(date.`dd-MM-yyyy` === "07-06-2022")
  }

  "`dd.MM.yyyy`" should {
    "return correct value" in assert(date.`dd.MM.yyyy` === "07.06.2022")
  }

  "`dd.MM.yy`" should {
    "return correct value" in assert(date.`dd.MM.yy` === "07.06.22")
  }

  "`d MMMM`" should {
    "return correct value" in {
      setLocale("ru")
      assert(date.`d MMMM` === "7 июня")
      setLocale("en")
      assert(date.`d MMMM` === "7 June")
      setLocale("ru")
    }
  }

  "`dd MMMM yyyy`" should {
    "return correct value" in {
      assert(date.`dd MMMM yyyy` === "07 июня 2022")
      setLocale("en")
      assert(date.`dd MMMM yyyy` === "07 June 2022")
      setLocale("ru")
    }
  }

  "`d MMMM yyyy`" should {
    "return correct value" in {
      assert(date.`d MMMM yyyy` === "7 июня 2022")
      setLocale("en")
      assert(date.`d MMMM yyyy` === "7 June 2022")
      setLocale("ru")
    }
  }

  "`d MMMM, EEEE`" should {
    "return correct value" in {
      assert(date.`d MMMM, EEEE` === "7 июня, вторник")
      setLocale("en")
      assert(date.`d MMMM, EEEE` === "7 June, Tuesday")
      setLocale("ru")
    }
  }

  "`yyyy`" should {
    "return correct value" in assert(date.`yyyy` === "2022")
  }

  "`MMMM`" should {
    "return correct value" in {
      assert(date.`MMMM` === "июня")
      setLocale("en")
      assert(date.`MMMM` === "June")
      setLocale("ru")
    }
  }

  "`MM/dd/yyyy`" should {
    "return correct value" in assert(date.`MM/dd/yyyy` === "06/07/2022")
  }

}
