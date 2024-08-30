package io.kinoplan.utils.nullable

import sttp.tapir.Schema

trait TapirCodecNullable {

  implicit def schemaForNullable[T](implicit
    s: Schema[T]
  ): Schema[Nullable[T]] = Schema[Nullable[T]](s.schemaType.as[Nullable[T]]).copy(
    isOptional = true,
    format = Some(Seq(Some("absent or nullable"), s.format).flatten.mkString(" "))
  )

}

object TapirCodecNullable extends TapirCodecNullable
