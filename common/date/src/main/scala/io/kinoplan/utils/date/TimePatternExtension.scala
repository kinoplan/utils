package io.kinoplan.utils.date

trait TimePatternExtension {
  protected def toString(pattern: String): String

  @inline
  def `HH:mm:ss`: String = toString(TimePattern.`HH:mm:ss`)

  @inline
  def `HH:mm`: String = toString(TimePattern.`HH:mm`)

  @inline
  def `HH_mm`: String = toString(TimePattern.`HH_mm`)

  @inline
  def `mm:ss`: String = toString(TimePattern.`mm:ss`)

}
