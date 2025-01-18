package io.kinoplan.utils.implicits

import scala.collection.{BuildFrom, IterableOps}
import scala.collection.generic.IsIterable

final private[implicits] class CollectionOps[Repr, A, C](
  private val value: Repr,
  it: IterableOps[A, Iterable, C]
) extends CollectionBase[A] {

  @inline
  def diffBy[B >: A, That](f: A => B)(container: Iterable[B])(implicit
    bf: BuildFrom[Repr, A, That]
  ): That = bf.fromSpecific(value)(it.view.filterNot(a => container.exists(_ == f(a))))

  @inline
  def diffByMerge[B >: A, That](f: A => B)(container: Iterable[A])(implicit
    bf: BuildFrom[Repr, A, That]
  ): That = bf.fromSpecific(value)(it.++(container.view.filterNot(a => it.view.exists(f(_) == f(a)))))

  @inline
  def filterIf[That](cond: Boolean)(f: A => Boolean)(implicit
    bf: BuildFrom[Repr, A, That]
  ): That = bf.fromSpecific(value)(
    if (cond) it.view.filter(f)
    else it.view
  )

  @inline
  def mapIf[That](cond: A => Boolean)(f: A => A)(implicit
    bf: BuildFrom[Repr, A, That]
  ): That = bf.fromSpecific(value)(
    it.view
      .map(a =>
        if (cond(a)) f(a)
        else a
      )
  )

  @inline
  def intersectBy[B >: A, That](f: A => B)(container: Iterable[B])(implicit
    bf: BuildFrom[Repr, A, That]
  ): That = bf.fromSpecific(value)(it.view.filter(a => container.exists(_ == f(a))))

  @inline
  def intersectByMerge[B >: A, That](f: A => B)(container: Iterable[A])(implicit
    bf: BuildFrom[Repr, A, That]
  ): That = bf.fromSpecific(value)(it.view.filter(a => container.exists(f(_) == f(a))))

  @inline
  def sumBy[B](f: A => B)(implicit
    num: Numeric[B]
  ): B = it.foldLeft(num.zero)((a, b) => num.plus(a, f(b)))

  @inline
  def zipWith[B](f: A => B): Map[B, A] = it.view.map(a => f(a) -> a).toMap

  @inline
  def zipBoth[B, I](k: A => B, v: A => I): Map[B, I] = it.view.map(a => k(a) -> v(a)).toMap

}

trait CollectionSyntax {

  implicit final def syntaxCollectionOps[Repr](value: Repr)(implicit
    it: IsIterable[Repr]
  ): CollectionOps[Repr, it.A, it.C] = new CollectionOps(value, it(value))

}

object CollectionSyntax extends CollectionSyntax
