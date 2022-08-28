package io.kinoplan.utils.implicits.joda.time

import org.joda.time.LocalTime
import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.implicits.joda.time.LocalTimeSyntax.syntaxLocalTimeOps

class LocalTimeSyntaxSpec extends AnyWordSpec {
  val date = new LocalTime("16:06:54.786")

  "`HH:mm:ss`" should {
    "return correct value" in assert(date.`HH:mm:ss` === "16:06:54")
  }

  "`HH:mm`" should {
    "return correct value" in assert(date.`HH:mm` === "16:06")
  }

  "`mm:ss`" should {
    "return correct value" in assert(date.`mm:ss` === "06:54")
  }

}
