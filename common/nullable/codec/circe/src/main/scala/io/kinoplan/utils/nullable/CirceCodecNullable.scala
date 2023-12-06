package io.kinoplan.utils.nullable

import cats.data.Validated
import io.circe._
import io.circe.Decoder.{AccumulatingResult, Result}

trait CirceCodecNullable {

  implicit final def nullableEncoder[A](implicit
    e: Encoder[A]
  ): Encoder[Nullable[A]] = {
    case Nullable.NonNull(v) => e(v)
    case Nullable.Absent     => Json.Null
    case Nullable.Null       => Json.Null
  }

  implicit final def nullableDecoder[A](implicit
    e: Decoder[A]
  ): Decoder[Nullable[A]] = new Decoder[Nullable[A]] {
    final override def apply(c: HCursor): Result[Nullable[A]] = tryDecode(c)

    final override def tryDecode(c: ACursor): Decoder.Result[Nullable[A]] = c match {
      case c: HCursor =>
        if (c.value.isNull) Right(Nullable.Null)
        else e(c) match {
          case Right(a) => Right(Nullable.NonNull(a))
          case Left(df) => Left(df)
        }
      case _ => Right(Nullable.Absent)
    }

    final override def decodeAccumulating(c: HCursor): AccumulatingResult[Nullable[A]] =
      tryDecodeAccumulating(c)

    final override def tryDecodeAccumulating(c: ACursor): AccumulatingResult[Nullable[A]] =
      c match {
        case c: HCursor =>
          if (c.value.isNull) Validated.valid(Nullable.Null)
          else e.decodeAccumulating(c) match {
            case Validated.Valid(a)       => Validated.valid(Nullable.NonNull(a))
            case i @ Validated.Invalid(_) => i
          }
        case _ => Validated.valid(Nullable.Absent)
      }

  }

}
