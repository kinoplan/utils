package io.kinoplan.utils.nullable

import zio.json.{JsonDecoder, JsonEncoder, JsonError}
import zio.json.internal.{RetractReader, Write}

trait ZioJsonCodecNullable {

  implicit final def nullableEncoder[A](implicit
    e: JsonEncoder[A]
  ): JsonEncoder[Nullable[A]] = new JsonEncoder[Nullable[A]] {

    def unsafeEncode(a: Nullable[A], indent: Option[Int], out: Write): Unit = a match {
      case Nullable.NonNull(v) => e.unsafeEncode(v, indent, out)
      case _                   => out.write("null")
    }

    override def isNothing(a: Nullable[A]): Boolean = a.isAbsent

  }

  implicit final def nullableDecoder[A](implicit
    d: JsonDecoder[A]
  ): JsonDecoder[Nullable[A]] = new JsonDecoder[Nullable[A]] {
    private val optionDecoder: JsonDecoder[Option[A]] = JsonDecoder.option[A]

    def unsafeDecode(trace: List[JsonError], in: RetractReader): Nullable[A] =
      optionDecoder.unsafeDecode(trace, in) match {
        case Some(a) => Nullable.NonNull(a)
        case None    => Nullable.Null
      }

    override def unsafeDecodeMissing(trace: List[JsonError]): Nullable[A] = Nullable.Absent

  }

}

object ZioJsonCodecNullable extends ZioJsonCodecNullable
