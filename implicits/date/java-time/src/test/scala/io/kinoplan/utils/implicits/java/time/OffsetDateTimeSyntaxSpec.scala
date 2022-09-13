package io.kinoplan.utils.implicits.java.time

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.{Locale, TimeZone}

import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.implicits.java.time.OffsetDateTimeSyntax.syntaxOffsetDateTimeOps

class OffsetDateTimeSyntaxSpec extends AnyWordSpec {
  def setLocale(lang: String): Unit = Locale.setDefault(Locale.forLanguageTag(lang))

  TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))

  val date: OffsetDateTime = OffsetDateTime
    .parse("2022-06-07T16:06:54.786+01:00")
    .toInstant
    .atOffset(ZoneOffset.UTC)

  "timestamp" should {
    "return correct value" in assert(date.timestamp === 1654614414)
  }

  "timestampLong" should {
    "return correct value" in assert(date.timestampLong === 1654614414L)
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

  "`HH:mm:ss`" should {
    "return correct value" in assert(date.`HH:mm:ss` === "18:06:54")
  }

  "`HH:mm`" should {
    "return correct value" in assert(date.`HH:mm` === "18:06")
  }

  "`mm:ss`" should {
    "return correct value" in assert(date.`mm:ss` === "06:54")
  }

  "`yyyyMMddHHmmss`" should {
    "return correct value" in assert(date.`yyyyMMddHHmmss` === "20220607180654")
  }

  "`dd MMMM yyyy HH:mm`" should {
    "return correct value" in {
      assert(date.`dd MMMM yyyy HH:mm` === "07 июня 2022 18:06")
      setLocale("en")
      assert(date.`dd MMMM yyyy HH:mm` === "07 June 2022 18:06")
      setLocale("ru")
    }
  }

  "`dd MMMM yyyy, HH:mm`" should {
    "return correct value" in {
      assert(date.`dd MMMM yyyy, HH:mm` === "07 июня 2022, 18:06")
      setLocale("en")
      assert(date.`dd MMMM yyyy, HH:mm` === "07 June 2022, 18:06")
      setLocale("ru")
    }
  }

  "`dd.MM.yyyy HH:mm`" should {
    "return correct value" in assert(date.`dd.MM.yyyy HH:mm` === "07.06.2022 18:06")
  }

  "`dd.MM.yyyy HH_mm`" should {
    "return correct value" in assert(date.`dd.MM.yyyy HH_mm` === "07.06.2022 18_06")
  }

  "`dd.MM.yyyy / HH:mm`" should {
    "return correct value" in assert(date.`dd.MM.yyyy / HH:mm` === "07.06.2022 / 18:06")
  }

  "`dd_MM_yyyy_HH_mm`" should {
    "return correct value" in assert(date.`dd_MM_yyyy_HH_mm` === "07_06_2022_18_06")
  }

  "`dd.MM.yy HH:mm`" should {
    "return correct value" in assert(date.`dd.MM.yy HH:mm` === "07.06.22 18:06")
  }

  "`dd.MM.yyyy HH:mm:ss`" should {
    "return correct value" in assert(date.`dd.MM.yyyy HH:mm:ss` === "07.06.2022 18:06:54")
  }

  "`dd-MM-yyyy HH:mm:ss`" should {
    "return correct value" in assert(date.`dd-MM-yyyy HH:mm:ss` === "07-06-2022 18:06:54")
  }

  "`yyyy-MM-dd HH:mm:ss`" should {
    "return correct value" in assert(date.`yyyy-MM-dd HH:mm:ss` === "2022-06-07 18:06:54")
  }

  "`yyyy-MM-dd HH:mm:ss.SSS`" should {
    "return correct value" in assert(date.`yyyy-MM-dd HH:mm:ss.SSS` === "2022-06-07 18:06:54.786")
  }

  "`yyyy-MM-dd HH:mm`" should {
    "return correct value" in assert(date.`yyyy-MM-dd HH:mm` === "2022-06-07 18:06")
  }

  "`yyyy-MM-dd'T'HH:mm:ss`" should {
    "return correct value" in assert(date.`yyyy-MM-dd'T'HH:mm:ss` === "2022-06-07T18:06:54")
  }

  "`yyyy-MM-dd'T'HH:mm:ss.SSS`" should {
    "return correct value" in assert(date.`yyyy-MM-dd'T'HH:mm:ss.SSS` === "2022-06-07T18:06:54.786")
  }

  "`HH:mm:ss dd.MM.yyyy`" should {
    "return correct value" in assert(date.`HH:mm:ss dd.MM.yyyy` === "18:06:54 07.06.2022")
  }

}
