package io.kinoplan.utils.implicits

import scala.util.Try

final private[implicits] class AnyOps(private val value: Any) extends AnyVal {

  @inline
  def toIntOption: Option[Int] = value match {
    case i: Int    => Some(i)
    case d: Double => Some(d.toInt)
    case f: Float  => Some(f.toInt)
    case l: Long   => Some(l.toInt)
    case s: String => Try(s.toInt).toOption
    case _         => None
  }

}

trait AnySyntax {
  implicit final def syntaxAnyOps(value: Any): AnyOps = new AnyOps(value)
}

object AnySyntax extends AnySyntax
