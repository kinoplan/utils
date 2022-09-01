package io.kinoplan.utils.date

trait TimePattern {
  val `HH:mm:ss` = "HH:mm:ss"
  val `HH:mm` = "HH:mm"
  val `HH_mm` = "HH_mm"
  val `mm:ss` = "mm:ss"
}

object TimePattern extends TimePattern
