package io.kinoplan.utils.implicits.zio.prelude

import scala.collection.IterableOps
import scala.collection.generic.IsIterable

import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

final private[prelude] class ZioPreludeCollectionOps[A, C](it: IterableOps[A, Iterable, C]) {

  @inline
  def toNel: Option[NonEmptyList[A]] = NonEmptyList.fromIterableOption(it.to(Iterable))

  @inline
  def toNec: Option[NonEmptyChunk[A]] = NonEmptyChunk.fromIterableOption(it.to(Iterable))

  @inline
  def toNes: Option[NonEmptySet[A]] = NonEmptySet.fromIterableOption(it.to(Iterable))

  @inline
  def toSortedNes(implicit
    o: Ordering[A]
  ): Option[NonEmptySortedSet[A]] = NonEmptySortedSet.fromIterableOption(it.to(Iterable))

  @inline
  def toNem[K, V](implicit
    ev: A <:< (K, V)
  ): Option[NonEmptyMap[K, V]] = NonEmptyMap.fromIterableOption[K, V](it.map(ev))

  @inline
  def toSortedNem[K, V](implicit
    ev: A <:< (K, V),
    ordering: Ordering[K]
  ): Option[NonEmptySortedMap[K, V]] = NonEmptySortedMap.fromIterableOption[K, V](it.map(ev))

}

trait ZioPreludeCollectionSyntax {

  implicit final def syntaxZioPreludeCollectionOps[Repr](value: Repr)(implicit
    it: IsIterable[Repr]
  ): ZioPreludeCollectionOps[it.A, it.C] = new ZioPreludeCollectionOps(it(value))

}

object ZioPreludeCollectionSyntax extends ZioPreludeCollectionSyntax
