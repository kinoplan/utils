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
  def `MM/dd/yyyy`: String = toString(DatePattern.`MM/dd/yyyy`)

}
