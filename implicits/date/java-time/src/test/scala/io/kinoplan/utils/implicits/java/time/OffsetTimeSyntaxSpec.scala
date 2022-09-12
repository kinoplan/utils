package io.kinoplan.utils.implicits.java.time

import java.time.OffsetTime
import java.util.TimeZone

import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.implicits.java.time.OffsetTimeSyntax.syntaxOffsetTimeOps

class OffsetTimeSyntaxSpec extends AnyWordSpec {
  val date: OffsetTime = OffsetTime.parse("15:06:54Z")

  TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))

  "`HH:mm:ss`" should {
    "return correct value" in assert(date.`HH:mm:ss` === "18:06:54")
  }

  "`HH:mm`" should {
    "return correct value" in assert(date.`HH:mm` === "18:06")
  }

  "`HH_mm`" should {
    "return correct value" in assert(date.`HH_mm` === "18_06")
  }

  "`mm:ss`" should {
    "return correct value" in assert(date.`mm:ss` === "06:54")
  }

}
