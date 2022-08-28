package io.kinoplan.utils.date

trait DatePattern {
  val `yyyy-MM-dd` = "yyyy-MM-dd"
  val `dd-MM-yyyy` = "dd-MM-yyyy"
  val `dd.MM.yyyy` = "dd.MM.yyyy"
  val `dd.MM.yy` = "dd.MM.yy"
  val `d MMMM` = "d MMMM"
  val `dd MMMM yyyy` = "dd MMMM yyyy"
  val `d MMMM yyyy` = "d MMMM yyyy"
  val `d MMMM, EEEE` = "d MMMM, EEEE"
  val `yyyy` = "yyyy"
  val `MMMM` = "MMMM"
  val `MM/dd/yyyy` = "MM/dd/yyyy"
}

object DatePattern extends DatePattern
