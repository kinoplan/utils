package io.kinoplan.utils.nullable

import org.scalatest.wordspec.AnyWordSpec
import sttp.tapir.{Schema, SchemaType}

class TapirCodecNullableSpec extends AnyWordSpec with TapirCodecNullable {

  "TapirCodecNullable#schemaForNullable" should {
    "return correct schemaType" in
      assert(
        implicitly[Schema[Nullable[String]]].schemaType == Schema(SchemaType.SString()).schemaType
      )

    "return correct isOptional" in assert(implicitly[Schema[Nullable[String]]].isOptional)

    "return correct format" in {
      assert(implicitly[Schema[Nullable[String]]].format.contains("absent or nullable"))
      assert(
        implicitly[Schema[Nullable[SchemaDate]]].format.contains("absent or nullable dd.MM.yyyy")
      )
    }
  }

}
