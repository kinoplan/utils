package io.kinoplan.utils.tapir.zio.prelude

import sttp.tapir.Validator
import zio.prelude.{ForEach, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

object ValidatorPrelude {

  def nonEmptyForEach[F[+_]: ForEach, T]: Validator[F[T]] = Validator
    .nonEmpty
    .contramap[F[T]](ForEach[F].toList)

  def nonEmptySet[T]: Validator[NonEmptySet[T]] = Validator
    .nonEmpty
    .contramap[NonEmptySet[T]](_.toSet)

  def nonEmptySortedSet[T]: Validator[NonEmptySortedSet[T]] = Validator
    .nonEmpty
    .contramap[NonEmptySortedSet[T]](_.toSet)

  def nonEmptyMap[K, V]: Validator[NonEmptyMap[K, V]] = Validator
    .nonEmpty
    .contramap[NonEmptyMap[K, V]](_.toMap)

  def nonEmptySortedMap[K, V]: Validator[NonEmptySortedMap[K, V]] = Validator
    .nonEmpty
    .contramap[NonEmptySortedMap[K, V]](_.toMap)

}
