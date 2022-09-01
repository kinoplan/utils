package io.kinoplan.utils.date

trait DateTimePattern {
  val `yyyyMMddHHmmss` = "yyyyMMddHHmmss"
  val `dd MMMM yyyy HH:mm` = "dd MMMM yyyy HH:mm"
  val `dd MMMM yyyy, HH:mm` = "dd MMMM yyyy, HH:mm"
  val `dd.MM.yyyy HH:mm` = "dd.MM.yyyy HH:mm"
  val `dd.MM.yyyy HH_mm` = "dd.MM.yyyy HH_mm"
  val `dd.MM.yyyy / HH:mm` = "dd.MM.yyyy / HH:mm"
  val `dd_MM_yyyy_HH_mm` = "dd_MM_yyyy_HH_mm"
  val `dd.MM.yy HH:mm` = "dd.MM.yy HH:mm"
  val `dd.MM.yyyy HH:mm:ss` = "dd.MM.yyyy HH:mm:ss"
  val `dd-MM-yyyy HH:mm:ss` = "dd-MM-yyyy HH:mm:ss"
  val `yyyy-MM-dd HH:mm:ss` = "yyyy-MM-dd HH:mm:ss"
  val `yyyy-MM-dd HH:mm:ss.SSS` = "yyyy-MM-dd HH:mm:ss.SSS"
  val `yyyy-MM-dd HH:mm` = "yyyy-MM-dd HH:mm"
  val `yyyy-MM-dd'T'HH:mm:ss` = "yyyy-MM-dd'T'HH:mm:ss"
  val `yyyy-MM-dd'T'HH:mm:ss.SSS` = "yyyy-MM-dd'T'HH:mm:ss.SSS"
  val `HH:mm:ss dd.MM.yyyy` = "HH:mm:ss dd.MM.yyyy"
}

object DateTimePattern extends DateTimePattern
