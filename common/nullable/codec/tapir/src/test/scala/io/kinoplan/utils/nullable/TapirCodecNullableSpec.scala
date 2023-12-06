package io.kinoplan.utils.nullable

import org.scalatest.wordspec.AnyWordSpec
import sttp.tapir.{Schema, SchemaType}
import sttp.tapir.SchemaType.SOption

class TapirCodecNullableSpec extends AnyWordSpec with TapirCodecNullable {

  "TapirCodecNullable#schemaForNullable" should {
    "return correct schemaType" in
      assert(
        implicitly[Schema[Nullable[String]]].schemaType ==
          SOption[Nullable[String], String](Schema(SchemaType.SString()))(_.toOption)
      )

    "return correct isOptional" in assert(implicitly[Schema[Nullable[String]]].isOptional)
  }

}
