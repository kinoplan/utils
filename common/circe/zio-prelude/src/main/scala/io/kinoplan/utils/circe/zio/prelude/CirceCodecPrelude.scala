package io.kinoplan.utils.circe.zio.prelude

import scala.collection.immutable.{SortedMap, SortedSet}
import scala.collection.mutable

import io.circe.{CirceAccessors, Decoder, Encoder, KeyDecoder, KeyEncoder, NonEmptyMapDecoder}
import io.circe.Encoder.{AsArray, AsObject, encodeMap}
import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

trait CirceCodecPrelude {

  implicit final def encodeNonEmptySet[A](implicit
    encoder: Encoder[A]
  ): AsArray[NonEmptySet[A]] = (a: NonEmptySet[A]) => a.toVector.map(encoder(_))

  implicit final def decodeNonEmptySet[A](implicit
    decoder: Decoder[A]
  ): Decoder[NonEmptySet[A]] = CirceAccessors.nonEmptySeqDecoder[A, Set, NonEmptySet[A]](
    Set.newBuilder[A],
    (h, t) => NonEmptySet.fromSet(h, t)
  )

  implicit final def encodeNonEmptySortedSet[A](implicit
    encoder: Encoder[A]
  ): AsArray[NonEmptySortedSet[A]] = (a: NonEmptySortedSet[A]) => a.toVector.map(encoder(_))

  implicit final def decodeNonEmptySortedSet[A](implicit
    decoder: Decoder[A],
    ordering: Ordering[A]
  ): Decoder[NonEmptySortedSet[A]] = CirceAccessors
    .nonEmptySeqDecoder[A, SortedSet, NonEmptySortedSet[A]](
      SortedSet.newBuilder[A],
      (h, t) => NonEmptySortedSet.fromSet(h, t)
    )

  implicit final def encodeNonEmptyList[A](implicit
    encoder: Encoder[A]
  ): AsArray[NonEmptyList[A]] = (a: NonEmptyList[A]) => a.toVector.map(encoder(_))

  implicit final def decodeNonEmptyList[A](implicit
    decoder: Decoder[A]
  ): Decoder[NonEmptyList[A]] = CirceAccessors.nonEmptySeqDecoder[A, List, NonEmptyList[A]](
    List.newBuilder[A],
    (h, t) => NonEmptyList.fromIterable(h, t)
  )

  implicit final def encodeNonEmptyChunk[A](implicit
    encoder: Encoder[A]
  ): AsArray[NonEmptyChunk[A]] = (a: NonEmptyChunk[A]) => a.toVector.map(encoder(_))

  implicit final def decodeNonEmptyChunk[A](implicit
    decoder: Decoder[A]
  ): Decoder[NonEmptyChunk[A]] = CirceAccessors.nonEmptySeqDecoder[A, Iterable, NonEmptyChunk[A]](
    Iterable.newBuilder[A],
    (h, t) => NonEmptyChunk.fromIterable(h, t)
  )

  implicit final def encodeNonEmptyMap[K, V](implicit
    keyEncoder: KeyEncoder[K],
    encoder: Encoder[V]
  ): AsObject[NonEmptyMap[K, V]] = (a: NonEmptyMap[K, V]) => encodeMap[K, V].encodeObject(a.toMap)

  implicit final def decodeNonEmptyMap[K, V](implicit
    keyDecoder: KeyDecoder[K],
    decoder: Decoder[V]
  ): Decoder[NonEmptyMap[K, V]] = new NonEmptyMapDecoder[K, V, Map](keyDecoder, decoder) {
    final protected def createBuilder(): mutable.Builder[(K, V), Map[K, V]] = Map.newBuilder[K, V]
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
    final protected def createBuilder(): mutable.Builder[(K, V), SortedMap[K, V]] = SortedMap
      .newBuilder[K, V]
  }.emap(NonEmptySortedMap.fromMapOption(_).toRight("[K, V]NonEmptySortedMap[K, V]"))

}

object CirceCodecPrelude extends CirceCodecPrelude
