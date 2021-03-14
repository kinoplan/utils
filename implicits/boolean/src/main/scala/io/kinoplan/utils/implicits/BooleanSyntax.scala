package io.kinoplan.utils.implicits

final private[implicits] class BooleanOps(private val value: Boolean) extends AnyVal {

  @inline
  def toOption: Option[Boolean] = if (value) Some(true) else None

  @inline
  def toRight[L](left: => L): Either[L, Unit] = toEither(left, ())

  @inline
  def toEither[L, R](left: => L, right: => R): Either[L, R] = Either.cond(value, right, left)

  @inline
  def toInt: Int = if (value) 1 else 0

  @inline
  def fold[T](falseAction: => T)(trueAction: => T): T = if (value) trueAction else falseAction

  @inline
  def foldList[T](falseAction: => List[T]): List[T] = if (value) List.empty[T] else falseAction

  @inline
  def when[T](action: => T): Option[T] = if (value) Some(action) else None

  @inline
  def unless[T](action: => T): Option[T] = if (value) None else Some(action)

}

trait BooleanSyntax {
  implicit final def syntaxBooleanOps(value: Boolean): BooleanOps = new BooleanOps(value)
}

object BooleanSyntax extends BooleanSyntax
