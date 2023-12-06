package io.kinoplan.utils.nullable

import sttp.tapir.{Schema, SchemaType}

trait TapirCodecNullable {

  implicit def schemaForNullable[T: Schema]: Schema[Nullable[T]] =
    Schema[Nullable[T]](SchemaType.SOption(implicitly[Schema[T]])(_.toOption))
      .copy(isOptional = true, format = Some("absent or nullable"))

}
