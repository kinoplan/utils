package io.kinoplan.utils.implicits

import scala.collection.{GenIterable, IterableLike}
import scala.collection.generic.CanBuildFrom

final private[implicits] class CollectionOps[A, Repr](private val value: IterableLike[A, Repr])
    extends CollectionSeqLike[A, Repr](value)
      with CollectionBase[A] {

  @inline
  def diffBy[B, That](f: A => B)(container: GenIterable[B])(implicit
    cbf: CanBuildFrom[Repr, A, That]
  ): That = computesOperationBy[B, That](f, container, CollectionOperation.Difference)

  @inline
  def diffByMerge[B, That](f: A => B)(container: GenIterable[A])(implicit
    cbf: CanBuildFrom[Repr, A, That]
  ): That = computesOperationByMerge[B, That](f, container, CollectionOperation.Difference)

  @inline
  def distinctBy[B, That](f: A => B)(implicit
    cbf: CanBuildFrom[Repr, A, That]
  ): That = {
    val builder = cbf(value.repr)
    val i = value.iterator

    var temp = Set[B]()

    i.foreach { o =>
      val b = f(o)

      if (!temp.contains(b)) {
        temp += b
        builder += o
      }
    }

    builder.result
  }

  @inline
  def filterIf[That](cond: Boolean)(f: A => Boolean)(implicit
    cbf: CanBuildFrom[Repr, A, That]
  ): That = {
    val builder = cbf(value.repr)
    val i = value.iterator

    builder ++=
      (if (cond) i.filter(f)
       else i)

    builder.result()
  }

  @inline
  def intersectBy[B, That](f: A => B)(container: GenIterable[B])(implicit
    cbf: CanBuildFrom[Repr, A, That]
  ): That = computesOperationBy[B, That](f, container, CollectionOperation.Intersection)

  @inline
  def intersectByMerge[B, That](f: A => B)(container: GenIterable[A])(implicit
    cbf: CanBuildFrom[Repr, A, That]
  ): That = computesOperationByMerge[B, That](f, container, CollectionOperation.Intersection)

  @inline
  def maxOption(implicit
    cmp: Ordering[A]
  ): Option[A] =
    if (value.isEmpty) None
    else Some(value.max)

  @inline
  def maxByOption[B](f: A => B)(implicit
    cmp: Ordering[B]
  ): Option[A] =
    if (value.isEmpty) None
    else Some(value.maxBy(f)(cmp))

  @inline
  def minOption(implicit
    cmp: Ordering[A]
  ): Option[A] =
    if (value.isEmpty) None
    else Some(value.min)

  @inline
  def minByOption[B](f: A => B)(implicit
    cmp: Ordering[B]
  ): Option[A] =
    if (value.isEmpty) None
    else Some(value.minBy(f)(cmp))

  @inline
  def sumBy[B](f: A => B)(implicit
    num: Numeric[B]
  ): B = value.foldLeft(num.zero)((a, b) => num.plus(a, f(b)))

  @inline
  def zipWith[B](f: A => B): Map[B, A] = {
    var builder = Map[B, A]()
    val i = value.iterator

    i.foreach(o => builder += f(o) -> o)

    builder
  }

  @inline
  def zipBoth[B, I](k: A => B, v: A => I): Map[B, I] = {
    var builder = Map[B, I]()
    val i = value.iterator

    i.foreach(o => builder += k(o) -> v(o))

    builder
  }

}

trait CollectionSyntax {

  implicit final def syntaxCollectionOps[A, Repr](
    value: IterableLike[A, Repr]
  ): CollectionOps[A, Repr] = new CollectionOps(value)

}

object CollectionSyntax extends CollectionSyntax
