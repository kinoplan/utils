package io.kinoplan.utils.circe.zio.prelude

import scala.collection.immutable.{SortedMap, SortedSet}
import scala.collection.mutable

import io.circe.{
  Decoder,
  Encoder,
  KeyDecoder,
  KeyEncoder,
  NonEmptyMapDecoder,
  NonEmptySequenceDecoder
}
import io.circe.Encoder.{AsArray, AsObject, encodeMap}
import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

trait CirceCodecPrelude {

  implicit final def encodeNonEmptySet[A](implicit
    encoder: Encoder[A]
  ): AsArray[NonEmptySet[A]] = (a: NonEmptySet[A]) => a.toVector.map(encoder(_))

  implicit final def decodeNonEmptySet[A](implicit
    decoder: Decoder[A]
  ): Decoder[NonEmptySet[A]] = new NonEmptySequenceDecoder[A, Set, NonEmptySet[A]] {
    override protected def createBuilder(): mutable.Builder[A, Set[A]] = Set.newBuilder[A]
    override protected def create: (A, Set[A]) => NonEmptySet[A] =
      (h, t) => NonEmptySet.fromSet(h, t)
  }

  implicit final def encodeNonEmptySortedSet[A](implicit
    encoder: Encoder[A]
  ): AsArray[NonEmptySortedSet[A]] = (a: NonEmptySortedSet[A]) => a.toVector.map(encoder(_))

  implicit final def decodeNonEmptySortedSet[A](implicit
    decoder: Decoder[A],
    ordering: Ordering[A]
  ): Decoder[NonEmptySortedSet[A]] =
    new NonEmptySequenceDecoder[A, SortedSet, NonEmptySortedSet[A]] {
      override protected def createBuilder(): mutable.Builder[A, SortedSet[A]] = SortedSet
        .newBuilder[A]
      override protected def create: (A, SortedSet[A]) => NonEmptySortedSet[A] =
        (h, t) => NonEmptySortedSet.fromSet(h, t)
    }

  implicit final def encodeNonEmptyList[A](implicit
    encoder: Encoder[A]
  ): AsArray[NonEmptyList[A]] = (a: NonEmptyList[A]) => a.toVector.map(encoder(_))

  implicit final def decodeNonEmptyList[A](implicit
    decoder: Decoder[A]
  ): Decoder[NonEmptyList[A]] = new NonEmptySequenceDecoder[A, List, NonEmptyList[A]] {
    override protected def createBuilder(): mutable.Builder[A, List[A]] = List.newBuilder[A]
    override protected def create: (A, List[A]) => NonEmptyList[A] =
      (h, t) => NonEmptyList.fromIterable(h, t)
  }

  implicit final def encodeNonEmptyChunk[A](implicit
    encoder: Encoder[A]
  ): AsArray[NonEmptyChunk[A]] = (a: NonEmptyChunk[A]) => a.toVector.map(encoder(_))

  implicit final def decodeNonEmptyChunk[A](implicit
    decoder: Decoder[A]
  ): Decoder[NonEmptyChunk[A]] = new NonEmptySequenceDecoder[A, List, NonEmptyChunk[A]] {
    override protected def createBuilder(): mutable.Builder[A, List[A]] = List.newBuilder[A]
    override protected def create: (A, List[A]) => NonEmptyChunk[A] =
      (h, t) => NonEmptyChunk.fromIterable(h, t)
  }

  implicit final def encodeNonEmptyMap[K, V](implicit
    keyEncoder: KeyEncoder[K],
    encoder: Encoder[V]
  ): AsObject[NonEmptyMap[K, V]] = (a: NonEmptyMap[K, V]) => encodeMap[K, V].encodeObject(a.toMap)

  implicit final def decodeNonEmptyMap[K, V](implicit
    keyDecoder: KeyDecoder[K],
    decoder: Decoder[V]
  ): Decoder[NonEmptyMap[K, V]] = new NonEmptyMapDecoder[K, V, Map](keyDecoder, decoder) {
    override protected def createBuilder(): mutable.Builder[(K, V), Map[K, V]] = Map.newBuilder[K, V]
  }.emap(NonEmptyMap.fromMapOption(_).toRight("[K, V]NonEmptyMap[K, V]"))

  implicit final def encodeNonEmptySortedMap[K, V](implicit
    keyEncoder: KeyEncoder[K],
    encoder: Encoder[V]
  ): AsObject[NonEmptySortedMap[K, V]] =
    (a: NonEmptySortedMap[K, V]) => encodeMap[K, V].encodeObject(a.toMap)

  implicit final def decodeNonEmptySortedMap[K, V](implicit
    keyDecoder: KeyDecoder[K],
    decoder: Decoder[V],
    ordering: Ordering[K]
  ): Decoder[NonEmptySortedMap[K, V]] = new NonEmptyMapDecoder[K, V, SortedMap](keyDecoder, decoder) {
    override protected def createBuilder(): mutable.Builder[(K, V), SortedMap[K, V]] = SortedMap
      .newBuilder[K, V]
  }.emap(NonEmptySortedMap.fromMapOption(_).toRight("[K, V]NonEmptySortedMap[K, V]"))

}

object CirceCodecPrelude extends CirceCodecPrelude
