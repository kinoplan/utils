package io.kinoplan.utils.date

trait DateTimePatternExtension extends DatePatternExtension with TimePatternExtension {
  protected def toString(pattern: String): String

  @inline
  def `yyyyMMddHHmmss`: String = toString(DateTimePattern.`yyyyMMddHHmmss`)

  @inline
  def `dd MMMM yyyy HH:mm`: String = toString(DateTimePattern.`dd MMMM yyyy HH:mm`)

  @inline
  def `dd.MM.yyyy HH:mm`: String = toString(DateTimePattern.`dd.MM.yyyy HH:mm`)

  @inline
  def `dd.MM.yyyy / HH:mm`: String = toString(DateTimePattern.`dd.MM.yyyy / HH:mm`)

  @inline
  def `dd_MM_yyyy_HH_mm`: String = toString(DateTimePattern.`dd_MM_yyyy_HH_mm`)

  @inline
  def `dd.MM.yy HH:mm`: String = toString(DateTimePattern.`dd.MM.yy HH:mm`)

  @inline
  def `dd.MM.yyyy HH:mm:ss`: String = toString(DateTimePattern.`dd.MM.yyyy HH:mm:ss`)

  @inline
  def `dd-MM-yyyy HH:mm:ss`: String = toString(DateTimePattern.`dd-MM-yyyy HH:mm:ss`)

  @inline
  def `yyyy-MM-dd HH:mm:ss`: String = toString(DateTimePattern.`yyyy-MM-dd HH:mm:ss`)

  @inline
  def `yyyy-MM-dd HH:mm:ss.SSS`: String = toString(DateTimePattern.`yyyy-MM-dd HH:mm:ss.SSS`)

  @inline
  def `yyyy-MM-dd HH:mm`: String = toString(DateTimePattern.`yyyy-MM-dd HH:mm`)

  @inline
  def `yyyy-MM-dd'T'HH:mm:ss`: String = toString(DateTimePattern.`yyyy-MM-dd'T'HH:mm:ss`)

  @inline
  def `yyyy-MM-dd'T'HH:mm:ss.SSS`: String = toString(DateTimePattern.`yyyy-MM-dd'T'HH:mm:ss.SSS`)

}
