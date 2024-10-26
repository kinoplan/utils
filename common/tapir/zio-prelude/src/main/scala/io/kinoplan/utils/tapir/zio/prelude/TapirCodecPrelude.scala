package io.kinoplan.utils.tapir.zio.prelude

import scala.collection.immutable.{SortedMap, SortedSet}

import sttp.tapir.{Codec, CodecFormat, DecodeResult, Schema, SchemaType, Validator}
import sttp.tapir.generic.auto.SchemaDerivation
import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

trait TapirCodecPrelude extends SchemaDerivation {

  implicit def schemaForNel[T: Schema]: Schema[NonEmptyList[T]] =
    Schema[NonEmptyList[T]](SchemaType.SArray(implicitly[Schema[T]])(_.toList))
      .validate(ValidatorPrelude.nonEmptyForEach[NonEmptyList, T])

  implicit def schemaForNec[T: Schema]: Schema[NonEmptyChunk[T]] =
    Schema[NonEmptyChunk[T]](SchemaType.SArray(implicitly[Schema[T]])(_.toList))
      .validate(ValidatorPrelude.nonEmptyForEach[NonEmptyChunk, T])

  implicit def schemaForNes[T: Schema]: Schema[NonEmptySet[T]] =
    Schema[NonEmptySet[T]](SchemaType.SArray(implicitly[Schema[T]])(_.toSet))
      .validate(ValidatorPrelude.nonEmptySet[T])

  implicit def schemaForSortedNes[T: Schema]: Schema[NonEmptySortedSet[T]] =
    Schema[NonEmptySortedSet[T]](SchemaType.SArray(implicitly[Schema[T]])(_.toSet))
      .validate(ValidatorPrelude.nonEmptySortedSet[T])

  implicit def schemaForStringNem[V: Schema]: Schema[NonEmptyMap[String, V]] = Schema
    .schemaForMap[V]
    .map(map => NonEmptyMap.fromMapOption(map))(_.toMap)
    .validate(ValidatorPrelude.nonEmptyMap[String, V])

  def schemaForNem[K: Schema, V: Schema](keyToString: K => String): Schema[NonEmptyMap[K, V]] =
    Schema
      .schemaForMap[K, V](keyToString)
      .map(map => NonEmptyMap.fromMapOption(map))(_.toMap)
      .validate(ValidatorPrelude.nonEmptyMap[K, V])

  implicit def schemaForStringSortedNem[V: Schema]: Schema[NonEmptySortedMap[String, V]] = Schema
    .schemaForMap[V]
    .map(map => NonEmptySortedMap.fromMapOption(SortedMap(map.toSeq: _*)))(_.toMap)
    .validate(ValidatorPrelude.nonEmptySortedMap[String, V])

  def schemaForSortedNem[K: Schema: Ordering, V: Schema](
    keyToString: K => String
  ): Schema[NonEmptySortedMap[K, V]] = Schema
    .schemaForMap[K, V](keyToString)
    .map(map => NonEmptySortedMap.fromMapOption(SortedMap(map.toSeq: _*)))(_.toMap)
    .validate(ValidatorPrelude.nonEmptySortedMap[K, V])

  implicit def codecForNonEmptyList[L, H, CF <: CodecFormat](implicit
    c: Codec[L, List[H], CF]
  ): Codec[L, NonEmptyList[H], CF] = c
    .schema(_.copy(isOptional = false))
    .validate(Validator.nonEmpty)
    .mapDecode { l =>
      DecodeResult.fromOption(NonEmptyList.fromIterableOption(l))
    }(_.toList)

  implicit def codecForNonEmptyChunk[L, H, CF <: CodecFormat](implicit
    c: Codec[L, List[H], CF]
  ): Codec[L, NonEmptyChunk[H], CF] = c
    .schema(_.copy(isOptional = false))
    .validate(Validator.nonEmpty)
    .mapDecode { l =>
      DecodeResult.fromOption(NonEmptyChunk.fromIterableOption(l))
    }(_.toList)

  implicit def codecForNonEmptySet[L, H, CF <: CodecFormat](implicit
    c: Codec[L, Set[H], CF]
  ): Codec[L, NonEmptySet[H], CF] = c
    .schema(_.copy(isOptional = false))
    .validate(Validator.nonEmpty)
    .mapDecode { set =>
      DecodeResult.fromOption(NonEmptySet.fromSetOption(set))
    }(_.toSet)

  implicit def codecForNonEmptySortedSet[L, H: Ordering, CF <: CodecFormat](implicit
    c: Codec[L, Set[H], CF]
  ): Codec[L, NonEmptySortedSet[H], CF] = c
    .schema(_.copy(isOptional = false))
    .validate(Validator.nonEmpty)
    .mapDecode { set =>
      DecodeResult.fromOption(NonEmptySortedSet.fromSetOption(SortedSet(set.toSeq: _*)))
    }(_.toSet)

}

object TapirCodecPrelude extends TapirCodecPrelude
