package io.kinoplan.utils.implicits

final class BooleanOps(private val value: Boolean) extends AnyVal {

  @inline
  def toOption: Option[Boolean] = if (value) Some(true) else None

}

trait BooleanSyntax {
  implicit def syntaxBooleanOps(value: Boolean): BooleanOps = new BooleanOps(value)
}

object BooleanSyntax extends BooleanSyntax
