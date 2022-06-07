package io.kinoplan.utils.implicits.joda.time

import java.util.Locale

import org.joda.time.DateTime
import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.implicits.joda.time.DateTimeSyntax.syntaxDateTimeOps

class DateTimeSyntaxSpec extends AnyWordSpec {
  def setLocale(lang: String): Unit = Locale.setDefault(new Locale(lang))

  val date = new DateTime("2022-06-07T16:06:54.786+01:00")

  "DateTimeSyntax#timestamp" should {
    "return correct value" in {
      assert(date.timestamp === 1654614414)
    }
  }

  "DateTimeSyntax#`yyyy-MM-dd`" should {
    "return correct value" in {
      assert(date.`yyyy-MM-dd` === "2022-06-07")
    }
  }

  "DateTimeSyntax#`dd-MM-yyyy`" should {
    "return correct value" in {
      assert(date.`dd-MM-yyyy` === "07-06-2022")
    }
  }

  "DateTimeSyntax#`dd.MM.yyyy`" should {
    "return correct value" in {
      assert(date.`dd.MM.yyyy` === "07.06.2022")
    }
  }

  "DateTimeSyntax#`dd.MM.yy`" should {
    "return correct value" in {
      assert(date.`dd.MM.yy` === "07.06.22")
    }
  }

  "DateTimeSyntax#`d MMMM`" should {
    "return correct value ru" in {
      assert(date.`d MMMM` === "7 июня")
      setLocale("en")
      assert(date.`d MMMM` === "7 June")
      setLocale("ru")
    }
  }

  "DateTimeSyntax#`dd MMMM yyyy`" should {
    "return correct value" in {
      assert(date.`dd MMMM yyyy` === "07 июня 2022")
      setLocale("en")
      assert(date.`dd MMMM yyyy` === "07 June 2022")
      setLocale("ru")
    }
  }

  "DateTimeSyntax#`d MMMM yyyy`" should {
    "return correct value" in {
      assert(date.`d MMMM yyyy` === "7 июня 2022")
      setLocale("en")
      assert(date.`d MMMM yyyy` === "7 June 2022")
      setLocale("ru")
    }
  }

  "DateTimeSyntax#`d MMMM, EEEE`" should {
    "return correct value" in {
      assert(date.`d MMMM, EEEE` === "7 июня, вторник")
      setLocale("en")
      assert(date.`d MMMM, EEEE` === "7 June, Tuesday")
      setLocale("ru")
    }
  }

  "DateTimeSyntax#`yyyy`" should {
    "return correct value" in {
      assert(date.`yyyy` === "2022")
    }
  }

  "DateTimeSyntax#`MMMM`" should {
    "return correct value" in {
      assert(date.`MMMM` === "июня")
      setLocale("en")
      assert(date.`MMMM` === "June")
      setLocale("ru")
    }
  }

  "DateTimeSyntax#`HH:mm:ss`" should {
    "return correct value" in {
      assert(date.`HH:mm:ss` === "15:06:54")
    }
  }

  "DateTimeSyntax#`HH:mm`" should {
    "return correct value" in {
      assert(date.`HH:mm` === "15:06")
    }
  }

  "DateTimeSyntax#`mm:ss`" should {
    "return correct value" in {
      assert(date.`mm:ss` === "06:54")
    }
  }

  "DateTimeSyntax#`yyyyMMddHHmmss`" should {
    "return correct value" in {
      assert(date.`yyyyMMddHHmmss` === "20220607150654")
    }
  }

  "DateTimeSyntax#`dd MMMM yyyy HH:mm`" should {
    "return correct value" in {
      assert(date.`dd MMMM yyyy HH:mm` === "07 июня 2022 15:06")
      setLocale("en")
      assert(date.`dd MMMM yyyy HH:mm` === "07 June 2022 15:06")
      setLocale("ru")
    }
  }

  "DateTimeSyntax#`dd.MM.yyyy HH:mm`" should {
    "return correct value" in {
      assert(date.`dd.MM.yyyy HH:mm` === "07.06.2022 15:06")
    }
  }

  "DateTimeSyntax#`dd.MM.yyyy / HH:mm`" should {
    "return correct value" in {
      assert(date.`dd.MM.yyyy / HH:mm` === "07.06.2022 / 15:06")
    }
  }

  "DateTimeSyntax#`dd.MM.yy HH:mm`" should {
    "return correct value" in {
      assert(date.`dd.MM.yy HH:mm` === "07.06.22 15:06")
    }
  }

  "DateTimeSyntax#`dd.MM.yyyy HH:mm:ss`" should {
    "return correct value" in {
      assert(date.`dd.MM.yyyy HH:mm:ss` === "07.06.2022 15:06:54")
    }
  }

  "DateTimeSyntax#`dd-MM-yyyy HH:mm:ss`" should {
    "return correct value" in {
      assert(date.`dd-MM-yyyy HH:mm:ss` === "07-06-2022 15:06:54")
    }
  }

  "DateTimeSyntax#`yyyy-MM-dd HH:mm:ss`" should {
    "return correct value" in {
      assert(date.`yyyy-MM-dd HH:mm:ss` === "2022-06-07 15:06:54")
    }
  }

  "DateTimeSyntax#`yyyy-MM-dd HH:mm:ss.SSS`" should {
    "return correct value" in {
      assert(date.`yyyy-MM-dd HH:mm:ss.SSS` === "2022-06-07 15:06:54.786")
    }
  }

  "DateTimeSyntax#`yyyy-MM-dd HH:mm`" should {
    "return correct value" in {
      assert(date.`yyyy-MM-dd HH:mm` === "2022-06-07 15:06")
    }
  }

  "DateTimeSyntax#`yyyy-MM-dd'T'HH:mm:ss`" should {
    "return correct value" in {
      assert(date.`yyyy-MM-dd'T'HH:mm:ss` === "2022-06-07T15:06:54")
    }
  }

  "DateTimeSyntax#`yyyy-MM-dd'T'HH:mm:ss.SSS`" should {
    "return correct value" in {
      assert(
        date.`yyyy-MM-dd'T'HH:mm:ss.SSS` === "2022-06-07T15:06:54.786"
      )
    }
  }

}
