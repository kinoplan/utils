package io.kinoplan.utils.zio.json.prelude

import scala.collection.immutable.{SortedMap, SortedSet}

import zio.json.{JsonDecoder, JsonEncoder, JsonFieldDecoder, JsonFieldEncoder}
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

trait ZioJsonCodecPrelude {

  implicit final def encodeNonEmptySet[A](implicit
    encoder: JsonEncoder[A]
  ): JsonEncoder[NonEmptySet[A]] = JsonEncoder[Iterable[A]].contramap(_.toSet)

  implicit final def decodeNonEmptySet[A](implicit
    decoder: JsonDecoder[A]
  ): JsonDecoder[NonEmptySet[A]] = JsonDecoder[Iterable[A]].mapOrFail {
    case head :: tail => Right(NonEmptySet.fromIterable[A](head, tail))
    case Nil          => Left("Set was empty")
  }

  implicit final def encodeNonEmptySortedSet[A](implicit
    encoder: JsonEncoder[A]
  ): JsonEncoder[NonEmptySortedSet[A]] = JsonEncoder[Iterable[A]].contramap(_.toList)

  implicit final def decodeNonEmptySortedSet[A](implicit
    decoder: JsonDecoder[A],
    ordering: Ordering[A]
  ): JsonDecoder[NonEmptySortedSet[A]] = JsonDecoder[Iterable[A]].mapOrFail {
    case head :: tail => Right(NonEmptySortedSet.fromSet(head, SortedSet(tail: _*)))
    case Nil          => Left("SortedSet was empty")
  }

  implicit final def encodeNonEmptyList[A](implicit
    encoder: JsonEncoder[A]
  ): JsonEncoder[NonEmptyList[A]] = JsonEncoder[Iterable[A]].contramap(_.toList)

  implicit final def decodeNonEmptyList[A](implicit
    decoder: JsonDecoder[A]
  ): JsonDecoder[NonEmptyList[A]] = JsonDecoder[Iterable[A]].mapOrFail {
    case head :: tail => Right(NonEmptyList.fromIterable(head, tail))
    case Nil          => Left("List was empty")
  }

  implicit final def encodeNonEmptyMap[K, V](implicit
    keyEncoder: JsonFieldEncoder[K],
    encoder: JsonEncoder[V]
  ): JsonEncoder[NonEmptyMap[K, V]] = JsonEncoder[Map[K, V]].contramap(_.toMap)

  implicit final def decodeNonEmptyMap[K, V](implicit
    keyDecoder: JsonFieldDecoder[K],
    decoder: JsonDecoder[V]
  ): JsonDecoder[NonEmptyMap[K, V]] =
    JsonDecoder[Map[K, V]].mapOrFail(NonEmptyMap.fromMapOption(_).toRight("Map was empty"))

  implicit final def encodeNonEmptySortedMap[K, V](implicit
    keyEncoder: JsonFieldEncoder[K],
    encoder: JsonEncoder[V]
  ): JsonEncoder[NonEmptySortedMap[K, V]] = JsonEncoder[Map[K, V]].contramap(_.toMap)

  implicit final def decodeNonEmptySortedMap[K, V](implicit
    keyDecoder: JsonFieldDecoder[K],
    decoder: JsonDecoder[V],
    ordering: Ordering[K]
  ): JsonDecoder[NonEmptySortedMap[K, V]] = JsonDecoder[Map[K, V]].mapOrFail { map =>
    NonEmptySortedMap.fromMapOption(SortedMap.empty[K, V] ++ map).toRight("SortedMap was empty")
  }

}

object ZioJsonCodecPrelude extends ZioJsonCodecPrelude
