package io.kinoplan.utils.nullable.core

/** Option-like data type that distinguishes null values from absent ones. */
sealed trait Nullable[+A] extends Product with Serializable {
  import Nullable._

  def fold[B](ifNull: => B, ifAbsent: => B, ifPresent: A => B): B = this match {
    case Null       => ifNull
    case Absent     => ifAbsent
    case NonNull(a) => ifPresent(a)
  }

  /** Fold if non-Absent. */
  def foldPresent[B](ifPresent: Option[A] => B): Option[B] =
    fold(Some(ifPresent(None)), None, a => Some(ifPresent(Some(a))))

  def exists(p: A => Boolean): Boolean = fold(false, false, a => p(a))
  def map[B](f: A => B): Nullable[B] = fold(Null, Absent, a => NonNull(f(a)))
  def flatMap[B](f: A => Nullable[B]): Nullable[B] = fold(Null, Absent, f)
  def orElse[B >: A](nb: Nullable[B]): Nullable[B] = fold(nb, nb, NonNull(_))
  def toOption: Option[A] = fold(None, None, Some(_))

  def toOptionOption: Option[Option[A]] = fold(Some(None), None, a => Some(Some(a)))

  def isNull: Boolean = fold(ifNull = true, ifAbsent = false, _ => false)

  def isAbsent: Boolean = fold(ifNull = false, ifAbsent = true, _ => false)

  def isPresent: Boolean = fold(ifNull = false, ifAbsent = false, _ => true)

}

object Nullable {

  case object Null extends Nullable[Nothing]
  case object Absent extends Nullable[Nothing]
  case class NonNull[A](value: A) extends Nullable[A]

  def orNull[A](o: Option[A]): Nullable[A] = o.fold(Null: Nullable[A])(NonNull(_))

  def orAbsent[A](o: Option[A]): Nullable[A] = o.fold(Absent: Nullable[A])(NonNull(_))

}
