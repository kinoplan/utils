package io.kinoplan.utils.implicits.identity

final private[implicits] class IdentityOps[T](private val entity: T) extends AnyVal {

  @inline
  def applyTo[A](f: T => A): A = f(entity)

  @inline
  def applyOption[A](value: Option[A])(f: (T, A) => T): T = value.map(f(entity, _)).getOrElse(entity)

  @inline
  def applyOption[A](value: T => Option[A])(f: (T, A) => T): T = applyOption(value(entity))(f)

  @inline
  def applyOptionFold[A](value: Option[A])(onTrue: (T, A) => T, onFalse: T => T): T = value
    .map(onTrue(entity, _))
    .getOrElse(onFalse(entity))

  @inline
  def applyOptionFold[A](value: T => Option[A])(onTrue: (T, A) => T, onFalse: T => T): T =
    applyOptionFold(value(entity))(onTrue, onFalse)

  @inline
  def applyNonEmpty[A, Collection[+Element] <: Iterable[Element]](
    value: Collection[A]
  )(f: (T, Collection[A]) => T): T =
    if (value.nonEmpty) f(entity, value)
    else entity

  @inline
  def applyNonEmpty[A, Collection[+Element] <: Iterable[Element]](value: T => Collection[A])(
    f: (T, Collection[A]) => T
  ): T = applyNonEmpty(value(entity))(f)

  @inline
  def applyWhen(value: Boolean)(f: T => T): T =
    if (value) f(entity)
    else entity

  @inline
  def applyWhen(value: T => Boolean)(f: T => T): T = applyWhen(value(entity))(f)

  @inline
  def applyUnless(value: Boolean)(f: T => T): T = applyWhen(!value)(f)

  @inline
  def applyUnless(value: T => Boolean)(f: T => T): T = applyUnless(value(entity))(f)

  @inline
  def applyIf(value: Boolean)(onTrue: T => T, onFalse: T => T): T =
    if (value) onTrue(entity)
    else onFalse(entity)

  @inline
  def applyIf(value: T => Boolean)(onTrue: T => T, onFalse: T => T): T =
    applyIf(value(entity))(onTrue, onFalse)

}

trait IdentitySyntax {
  implicit final def syntaxIdentityOps[T](entity: T): IdentityOps[T] = new IdentityOps(entity)
}

object IdentitySyntax extends IdentitySyntax
