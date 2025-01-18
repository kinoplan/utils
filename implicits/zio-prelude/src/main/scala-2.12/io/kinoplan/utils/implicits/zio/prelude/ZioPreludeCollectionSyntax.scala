package io.kinoplan.utils.implicits.zio.prelude

import scala.collection.IterableLike

import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

final private[prelude] class ZioPreludeCollectionOps[A, Repr](private val it: IterableLike[A, Repr]) {

  @inline
  def toNel: Option[NonEmptyList[A]] = NonEmptyList.fromIterableOption(it.toIterable)

  @inline
  def toNec: Option[NonEmptyChunk[A]] = NonEmptyChunk.fromIterableOption(it.toIterable)

  @inline
  def toNes: Option[NonEmptySet[A]] = NonEmptySet.fromIterableOption(it.toIterable)

  @inline
  def toSortedNes(implicit
    o: Ordering[A]
  ): Option[NonEmptySortedSet[A]] = NonEmptySortedSet.fromIterableOption(it.toIterable)

  @inline
  def toNem[K, V](implicit
    ev: A <:< (K, V)
  ): Option[NonEmptyMap[K, V]] = NonEmptyMap.fromIterableOption[K, V](it.toIterable.map(ev))

  @inline
  def toSortedNem[K, V](implicit
    ev: A <:< (K, V),
    ordering: Ordering[K]
  ): Option[NonEmptySortedMap[K, V]] =
    NonEmptySortedMap.fromIterableOption[K, V](it.toIterable.map(ev))

}

trait ZioPreludeCollectionSyntax {

  implicit final def syntaxZioPreludeCollectionOps[A, Repr](
    value: IterableLike[A, Repr]
  ): ZioPreludeCollectionOps[A, Repr] = new ZioPreludeCollectionOps(value)

}

object ZioPreludeCollectionSyntax extends ZioPreludeCollectionSyntax
