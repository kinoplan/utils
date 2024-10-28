package io.kinoplan.utils.chimney.zio.prelude

import scala.collection.compat.Factory
import scala.collection.mutable

import io.scalaland.chimney.integrations._
import io.scalaland.chimney.partial
import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

trait ChimneyImplicitsPrelude {

  implicit def totalOuterTransformerForNonEmptyList[A, B]: TotalOuterTransformer[NonEmptyList[A], NonEmptyList[B], A, B] =
    new TotalOuterTransformer[NonEmptyList[A], NonEmptyList[B], A, B] {

      def transformWithTotalInner(src: NonEmptyList[A], inner: A => B): NonEmptyList[B] = src
        .map(inner)

      def transformWithPartialInner(
        src: NonEmptyList[A],
        failFast: Boolean,
        inner: A => partial.Result[B]
      ): partial.Result[NonEmptyList[B]] = partial
        .Result
        .traverse[Seq[B], A, B](src.iterator, inner, failFast)
        .map(seq => NonEmptyList(seq.head, seq.tail: _*))
    }

  implicit def totalOuterTransformerForNonEmptyChunk[A, B]: TotalOuterTransformer[NonEmptyChunk[A], NonEmptyChunk[B], A, B] =
    new TotalOuterTransformer[NonEmptyChunk[A], NonEmptyChunk[B], A, B] {

      def transformWithTotalInner(src: NonEmptyChunk[A], inner: A => B): NonEmptyChunk[B] = src
        .map(inner)

      def transformWithPartialInner(
        src: NonEmptyChunk[A],
        failFast: Boolean,
        inner: A => partial.Result[B]
      ): partial.Result[NonEmptyChunk[B]] = partial
        .Result
        .traverse[Seq[B], A, B](src.iterator, inner, failFast)
        .map(seq => NonEmptyChunk(seq.head, seq.tail: _*))
    }

  implicit def totalOuterTransformerForNonEmptySet[A, B]: TotalOuterTransformer[NonEmptySet[A], NonEmptySet[B], A, B] =
    new TotalOuterTransformer[NonEmptySet[A], NonEmptySet[B], A, B] {

      def transformWithTotalInner(src: NonEmptySet[A], inner: A => B): NonEmptySet[B] = src
        .map(inner)

      def transformWithPartialInner(
        src: NonEmptySet[A],
        failFast: Boolean,
        inner: A => partial.Result[B]
      ): partial.Result[NonEmptySet[B]] = partial
        .Result
        .traverse[Seq[B], A, B](src.iterator, inner, failFast)
        .map(seq => NonEmptySet(seq.head, seq.tail: _*))
    }

  implicit def totalOuterTransformerForNonEmptySortedSet[A, B: Ordering]: TotalOuterTransformer[NonEmptySortedSet[A], NonEmptySortedSet[B], A, B] =
    new TotalOuterTransformer[NonEmptySortedSet[A], NonEmptySortedSet[B], A, B] {

      def transformWithTotalInner(src: NonEmptySortedSet[A], inner: A => B): NonEmptySortedSet[B] =
        src.map(inner)

      def transformWithPartialInner(
        src: NonEmptySortedSet[A],
        failFast: Boolean,
        inner: A => partial.Result[B]
      ): partial.Result[NonEmptySortedSet[B]] = partial
        .Result
        .traverse[Seq[B], A, B](src.iterator, inner, failFast)
        .map(seq => NonEmptySortedSet(seq.head, seq.tail: _*))
    }

  implicit def totalOuterTransformerForNonEmptyMap[A, B, C, D]: TotalOuterTransformer[NonEmptyMap[A, B], NonEmptyMap[C, D], (A, B), (C, D)] =
    new TotalOuterTransformer[NonEmptyMap[A, B], NonEmptyMap[C, D], (A, B), (C, D)] {

      def transformWithTotalInner(
        src: NonEmptyMap[A, B],
        inner: ((A, B)) => (C, D)
      ): NonEmptyMap[C, D] = NonEmptyMap.fromMap(inner(src.head), src.tail.map(inner))

      def transformWithPartialInner(
        src: NonEmptyMap[A, B],
        failFast: Boolean,
        inner: ((A, B)) => partial.Result[(C, D)]
      ): partial.Result[NonEmptyMap[C, D]] = partial
        .Result
        .traverse[Seq[(C, D)], (A, B), (C, D)](src.iterator, inner, failFast)
        .map(seq => NonEmptyMap(seq.head, seq.tail: _*))
    }

  implicit def totalOuterTransformerForNonEmptySortedMap[
    A,
    B,
    C: Ordering,
    D
  ]: TotalOuterTransformer[NonEmptySortedMap[A, B], NonEmptySortedMap[C, D], (A, B), (C, D)] =
    new TotalOuterTransformer[NonEmptySortedMap[A, B], NonEmptySortedMap[C, D], (A, B), (C, D)] {

      def transformWithTotalInner(
        src: NonEmptySortedMap[A, B],
        inner: ((A, B)) => (C, D)
      ): NonEmptySortedMap[C, D] = NonEmptySortedMap.fromMap(inner(src.head), src.tail.map(inner))

      def transformWithPartialInner(
        src: NonEmptySortedMap[A, B],
        failFast: Boolean,
        inner: ((A, B)) => partial.Result[(C, D)]
      ): partial.Result[NonEmptySortedMap[C, D]] = partial
        .Result
        .traverse[Seq[(C, D)], (A, B), (C, D)](src.iterator, inner, failFast)
        .map(seq => NonEmptySortedMap(seq.head, seq.tail: _*))
    }

  implicit def nonEmptyListIsPartiallyBuildIterable[A]: PartiallyBuildIterable[NonEmptyList[A], A] =
    new PartiallyBuildIterable[NonEmptyList[A], A] {
      def partialFactory: Factory[A, partial.Result[NonEmptyList[A]]] =
        new FactoryCompat[A, partial.Result[NonEmptyList[A]]] {
          def newBuilder: mutable.Builder[A, partial.Result[NonEmptyList[A]]] =
            new FactoryCompat.Builder[A, partial.Result[NonEmptyList[A]]] {
              private val impl = List.newBuilder[A]
              def clear(): Unit = impl.clear()
              def result(): partial.Result[NonEmptyList[A]] = partial
                .Result
                .fromOption(NonEmptyList.fromIterableOption(impl.result()))
              def addOne(elem: A): this.type = {
                impl += elem
                this
              }
            }
        }
      def iterator(collection: NonEmptyList[A]): Iterator[A] = collection.iterator
    }

  implicit def nonEmptySetIsPartiallyBuildIterable[A]: PartiallyBuildIterable[NonEmptySet[A], A] =
    new PartiallyBuildIterable[NonEmptySet[A], A] {
      def partialFactory: Factory[A, partial.Result[NonEmptySet[A]]] =
        new FactoryCompat[A, partial.Result[NonEmptySet[A]]] {
          def newBuilder: mutable.Builder[A, partial.Result[NonEmptySet[A]]] =
            new FactoryCompat.Builder[A, partial.Result[NonEmptySet[A]]] {
              private val impl = scala.collection.immutable.Set.newBuilder[A]
              def clear(): Unit = impl.clear()
              def result(): partial.Result[NonEmptySet[A]] = partial
                .Result
                .fromOption(NonEmptySet.fromSetOption(impl.result()))
              def addOne(elem: A): this.type = {
                impl += elem;
                this
              }
            }
        }
      def iterator(collection: NonEmptySet[A]): Iterator[A] = collection.iterator
    }

  implicit def nonEmptyChunkIsPartiallyBuildIterable[A]: PartiallyBuildIterable[NonEmptyChunk[A], A] =
    new PartiallyBuildIterable[NonEmptyChunk[A], A] {
      def partialFactory: Factory[A, partial.Result[NonEmptyChunk[A]]] =
        new FactoryCompat[A, partial.Result[NonEmptyChunk[A]]] {
          def newBuilder: mutable.Builder[A, partial.Result[NonEmptyChunk[A]]] =
            new FactoryCompat.Builder[A, partial.Result[NonEmptyChunk[A]]] {
              private val impl = List.newBuilder[A]
              def clear(): Unit = impl.clear()
              def result(): partial.Result[NonEmptyChunk[A]] = partial
                .Result
                .fromOption(NonEmptyChunk.fromIterableOption(impl.result()))
              def addOne(elem: A): this.type = {
                impl += elem
                this
              }
            }
        }
      def iterator(collection: NonEmptyChunk[A]): Iterator[A] = collection.iterator
    }

  implicit def nonEmptySortedSetIsPartiallyBuildIterable[A: Ordering]: PartiallyBuildIterable[NonEmptySortedSet[A], A] =
    new PartiallyBuildIterable[NonEmptySortedSet[A], A] {
      def partialFactory: Factory[A, partial.Result[NonEmptySortedSet[A]]] =
        new FactoryCompat[A, partial.Result[NonEmptySortedSet[A]]] {
          def newBuilder: mutable.Builder[A, partial.Result[NonEmptySortedSet[A]]] =
            new FactoryCompat.Builder[A, partial.Result[NonEmptySortedSet[A]]] {
              private val impl = scala.collection.immutable.SortedSet.newBuilder[A]
              def clear(): Unit = impl.clear()
              def result(): partial.Result[NonEmptySortedSet[A]] = partial
                .Result
                .fromOption(NonEmptySortedSet.fromSetOption(impl.result()))
              def addOne(elem: A): this.type = {
                impl += elem;
                this
              }
            }
        }
      def iterator(collection: NonEmptySortedSet[A]): Iterator[A] = collection.iterator
    }

  implicit def nonEmptyMapIsPartiallyBuildMap[K, V]: PartiallyBuildMap[NonEmptyMap[K, V], K, V] =
    new PartiallyBuildMap[NonEmptyMap[K, V], K, V] {
      def partialFactory: Factory[(K, V), partial.Result[NonEmptyMap[K, V]]] =
        new FactoryCompat[(K, V), partial.Result[NonEmptyMap[K, V]]] {
          def newBuilder: mutable.Builder[(K, V), partial.Result[NonEmptyMap[K, V]]] =
            new FactoryCompat.Builder[(K, V), partial.Result[NonEmptyMap[K, V]]] {
              private val impl = scala.collection.immutable.Map.newBuilder[K, V]
              def clear(): Unit = impl.clear()
              def result(): partial.Result[NonEmptyMap[K, V]] = partial
                .Result
                .fromOption(NonEmptyMap.fromMapOption(impl.result()))
              def addOne(elem: (K, V)): this.type = {
                impl += elem;
                this
              }
            }
        }
      def iterator(collection: NonEmptyMap[K, V]): Iterator[(K, V)] = collection.iterator
    }

  implicit def nonEmptySortedMapIsPartiallyBuildMap[K: Ordering, V]: PartiallyBuildMap[NonEmptySortedMap[K, V], K, V] =
    new PartiallyBuildMap[NonEmptySortedMap[K, V], K, V] {
      def partialFactory: Factory[(K, V), partial.Result[NonEmptySortedMap[K, V]]] =
        new FactoryCompat[(K, V), partial.Result[NonEmptySortedMap[K, V]]] {
          def newBuilder: mutable.Builder[(K, V), partial.Result[NonEmptySortedMap[K, V]]] =
            new FactoryCompat.Builder[(K, V), partial.Result[NonEmptySortedMap[K, V]]] {
              private val impl = scala.collection.immutable.SortedMap.newBuilder[K, V]
              def clear(): Unit = impl.clear()
              def result(): partial.Result[NonEmptySortedMap[K, V]] = partial
                .Result
                .fromOption(NonEmptySortedMap.fromMapOption(impl.result()))
              def addOne(elem: (K, V)): this.type = {
                impl += elem;
                this
              }
            }
        }
      def iterator(collection: NonEmptySortedMap[K, V]): Iterator[(K, V)] = collection.iterator
    }

}

object ChimneyImplicitsPrelude extends ChimneyImplicitsPrelude
