package io.kinoplan.utils.reactivemongo.bson.zio.prelude

import scala.util.{Failure, Success, Try}

import reactivemongo.api.bson.{BSONValue, _}
import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

trait BsonPreludeHandlers {

  implicit private class SeqTrySyntax[A](seq: Seq[Try[A]]) {

    @inline
    def sequence: Try[List[A]] = seq.foldLeft(Try(List.empty[A])) { case (acc, valueTry) =>
      valueTry.flatMap(value => acc.map(value :: _))
    }

  }

  private def failure(expected: String, bson: BSONValue) =
    Failure(new IllegalArgumentException(s"expected $expected, but found ${BSONValue.pretty(bson)}"))

  implicit final def nesHandler[A](implicit
    r: BSONReader[A],
    w: BSONWriter[A]
  ): BSONHandler[NonEmptySet[A]] = BSONHandler.from[NonEmptySet[A]](
    bson =>
      bson
        .asTry[BSONArray]
        .flatMap(value => value.values.map(r.readTry).sequence)
        .flatMap {
          case head :: tail => Success(NonEmptySet(head, tail: _*))
          case _            => failure("NonEmptySet", bson)
        },
    _.map(w.writeTry).toSeq.sequence.map(BSONArray(_))
  )

  implicit final def sortedNesHandler[A](implicit
    r: BSONReader[A],
    w: BSONWriter[A],
    ordering: Ordering[A]
  ): BSONHandler[NonEmptySortedSet[A]] = BSONHandler.from[NonEmptySortedSet[A]](
    bson =>
      bson
        .asTry[BSONArray]
        .flatMap(value => value.values.map(r.readTry).sequence)
        .flatMap {
          case head :: tail => Success(NonEmptySortedSet(head, tail: _*))
          case _            => failure("NonEmptySortedSet", bson)
        },
    _.toSeq.map(w.writeTry).sequence.map(BSONArray(_))
  )

  implicit final def nelHandler[A](implicit
    r: BSONReader[A],
    w: BSONWriter[A]
  ): BSONHandler[NonEmptyList[A]] = BSONHandler.from[NonEmptyList[A]](
    bson =>
      bson
        .asTry[BSONArray]
        .flatMap(value => value.values.map(r.readTry).sequence)
        .flatMap {
          case head :: tail => Success(NonEmptyList(head, tail: _*))
          case _            => failure("NonEmptyList", bson)
        },
    _.map(w.writeTry).toSeq.sequence.map(BSONArray(_))
  )

  implicit final def necHandler[A](implicit
    r: BSONReader[A],
    w: BSONWriter[A]
  ): BSONHandler[NonEmptyChunk[A]] = BSONHandler.from[NonEmptyChunk[A]](
    bson =>
      bson
        .asTry[BSONArray]
        .flatMap(value => value.values.map(r.readTry).sequence)
        .flatMap {
          case head :: tail => Success(NonEmptyChunk(head, tail: _*))
          case _            => failure("NonEmptyChunk", bson)
        },
    _.map(w.writeTry).toSeq.sequence.map(BSONArray(_))
  )

  implicit final def nemHandler[V](implicit
    r: BSONReader[V],
    w: BSONWriter[V]
  ): BSONDocumentHandler[NonEmptyMap[String, V]] = BSONDocumentHandler.from[NonEmptyMap[String, V]](
    bson =>
      bson
        .elements
        .map(element => element.value.asTry[V].map(element.name -> _))
        .sequence
        .flatMap {
          case head :: tail => Success(NonEmptyMap(head, tail: _*))
          case _            => failure("NonEmptyMap", bson)
        },
    _.map { case (key, value) =>
        w.writeTry(value).map(key -> _)
      }
      .toSeq
      .sequence
      .map(BSONDocument(_))
  )

  implicit final def sortedNemHandler[V](implicit
    r: BSONReader[V],
    w: BSONWriter[V]
  ): BSONDocumentHandler[NonEmptySortedMap[String, V]] =
    BSONDocumentHandler.from[NonEmptySortedMap[String, V]](
      bson =>
        bson
          .elements
          .map(element => element.value.asTry[V].map(element.name -> _))
          .sequence
          .flatMap {
            case head :: tail => Success(NonEmptySortedMap(head, tail: _*))
            case _            => failure("NonEmptySortedMap", bson)
          },
      _.map { case (key, value) =>
          w.writeTry(value).map(key -> _)
        }
        .toSeq
        .sequence
        .map(BSONDocument(_))
    )

}

object BsonPreludeHandlers extends BsonPreludeHandlers
