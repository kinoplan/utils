package io.kinoplan.utils.date

trait DatePatternExtension {
  protected def toString(pattern: String): String

  @inline
  def `yyyy-MM-dd`: String = toString(DatePattern.`yyyy-MM-dd`)

  @inline
  def `dd-MM-yyyy`: String = toString(DatePattern.`dd-MM-yyyy`)

  @inline
  def `dd.MM.yyyy`: String = toString(DatePattern.`dd.MM.yyyy`)

  @inline
  def `dd.MM.yy`: String = toString(DatePattern.`dd.MM.yy`)

  @inline
  def `d MMMM`: String = toString(DatePattern.`d MMMM`)

  @inline
  def `dd MMMM yyyy`: String = toString(DatePattern.`dd MMMM yyyy`)

  @inline
  def `d MMMM yyyy`: String = toString(DatePattern.`d MMMM yyyy`)

  @inline
  def `d MMMM, EEEE`: String = toString(DatePattern.`d MMMM, EEEE`)

  @inline
  def `yyyy`: String = toString(DatePattern.`yyyy`)

  @inline
  def `MMMM`: String = toString(DatePattern.`MMMM`)

  @inline
  def `HH:mm:ss`: String = toString(DatePattern.`HH:mm:ss`)

  @inline
  def `HH:mm`: String = toString(DatePattern.`HH:mm`)

  @inline
  def `mm:ss`: String = toString(DatePattern.`mm:ss`)

  @inline
  def `yyyyMMddHHmmss`: String = toString(DatePattern.`yyyyMMddHHmmss`)

  @inline
  def `dd MMMM yyyy HH:mm`: String = toString(DatePattern.`dd MMMM yyyy HH:mm`)

  @inline
  def `dd.MM.yyyy HH:mm`: String = toString(DatePattern.`dd.MM.yyyy HH:mm`)

  @inline
  def `dd.MM.yyyy / HH:mm`: String = toString(DatePattern.`dd.MM.yyyy / HH:mm`)

  @inline
  def `dd.MM.yy HH:mm`: String = toString(DatePattern.`dd.MM.yy HH:mm`)

  @inline
  def `dd.MM.yyyy HH:mm:ss`: String = toString(DatePattern.`dd.MM.yyyy HH:mm:ss`)

  @inline
  def `dd-MM-yyyy HH:mm:ss`: String = toString(DatePattern.`dd-MM-yyyy HH:mm:ss`)

  @inline
  def `yyyy-MM-dd HH:mm:ss`: String = toString(DatePattern.`yyyy-MM-dd HH:mm:ss`)

  @inline
  def `yyyy-MM-dd HH:mm:ss.SSS`: String = toString(DatePattern.`yyyy-MM-dd HH:mm:ss.SSS`)

  @inline
  def `yyyy-MM-dd HH:mm`: String = toString(DatePattern.`yyyy-MM-dd HH:mm`)

  @inline
  def `yyyy-MM-dd'T'HH:mm:ss`: String = toString(DatePattern.`yyyy-MM-dd'T'HH:mm:ss`)

  @inline
  def `yyyy-MM-dd'T'HH:mm:ss.SSS`: String = toString(DatePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`)

}
