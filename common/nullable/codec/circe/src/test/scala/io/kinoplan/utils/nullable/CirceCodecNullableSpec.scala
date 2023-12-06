package io.kinoplan.utils.nullable

import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax.EncoderOps
import org.scalatest.wordspec.AnyWordSpec

class CirceCodecNullableSpec extends AnyWordSpec {

  case class Foo(
    a: Nullable[Int],
    b: Nullable[Long],
    c: Nullable[Boolean],
    d: Nullable[String],
    e: Nullable[Set[Int]]
  )

  object Foo extends CirceCodecNullable {
    implicit val fooDecoder: Decoder[Foo] = deriveDecoder[Foo]
    implicit val fooEncoder: Encoder[Foo] = deriveEncoder[Foo]
  }

  "CirceCodecNullable#nullableEncoder" should {
    "return correct json for non null fields" in {
      val actual = Foo(
        a = Nullable.NonNull(1),
        b = Nullable.NonNull(2L),
        c = Nullable.NonNull(true),
        d = Nullable.NonNull(""),
        e = Nullable.NonNull(Set(1, 2, 3))
      )

      assert(
        actual.asJson ==
          Json.fromFields(
            List(
              "a" -> Json.fromInt(1),
              "b" -> Json.fromLong(2L),
              "c" -> Json.True,
              "d" -> Json.fromString(""),
              "e" -> Json.fromValues(List(Json.fromInt(1), Json.fromInt(2), Json.fromInt(3)))
            )
          )
      )
    }
    "return correct json for some null fields" in {
      val actual = Foo(
        a = Nullable.NonNull(1),
        b = Nullable.NonNull(2L),
        c = Nullable.NonNull(true),
        d = Nullable.orNull(Option.empty[String]),
        e = Nullable.orNull(Option.empty[Set[Int]])
      )

      assert(
        actual.asJson ==
          Json.fromFields(
            List(
              "a" -> Json.fromInt(1),
              "b" -> Json.fromLong(2L),
              "c" -> Json.True,
              "d" -> Json.Null,
              "e" -> Json.Null
            )
          )
      )
    }
    "return correct json for some null & absent fields" in {
      val actual = Foo(
        a = Nullable.NonNull(1),
        b = Nullable.orAbsent(Option.empty[Long]),
        c = Nullable.NonNull(true),
        d = Nullable.orNull(Option.empty[String]),
        e = Nullable.orNull(Option.empty[Set[Int]])
      )

      assert(
        actual.asJson ==
          Json.fromFields(
            List(
              "a" -> Json.fromInt(1),
              "b" -> Json.Null,
              "c" -> Json.True,
              "d" -> Json.Null,
              "e" -> Json.Null
            )
          )
      )
    }
  }

  "CirceCodecNullable#nullableDecoder" should {
    "return incorrect value for non valid field" in {
      val actual = Json
        .fromFields(
          List(
            "a" -> Json.True,
            "b" -> Json.fromLong(2L),
            "c" -> Json.True,
            "d" -> Json.fromString(""),
            "e" -> Json.fromValues(List(Json.fromInt(1), Json.fromInt(2), Json.fromInt(3)))
          )
        )
        .as[Foo]

      assert(actual.isInstanceOf[Left[DecodingFailure, Foo]])
    }
    "return correct value for non null fields" in {
      val actual = Json
        .fromFields(
          List(
            "a" -> Json.fromInt(1),
            "b" -> Json.fromLong(2L),
            "c" -> Json.True,
            "d" -> Json.fromString(""),
            "e" -> Json.fromValues(List(Json.fromInt(1), Json.fromInt(2), Json.fromInt(3)))
          )
        )
        .as[Foo]

      assert(actual.exists(_.a.exists(_ == 1)))
      assert(actual.exists(_.b.exists(_ == 2L)))
      assert(actual.exists(_.c.exists(_ == true)))
      assert(actual.exists(_.d.exists(_ == "")))
      assert(actual.exists(_.e.exists(_ == Set(1, 2, 3))))
    }
    "return correct value for some null fields" in {
      val actual = Json
        .fromFields(
          List(
            "a" -> Json.fromInt(1),
            "b" -> Json.fromLong(2L),
            "c" -> Json.True,
            "d" -> Json.Null,
            "e" -> Json.Null
          )
        )
        .as[Foo]

      assert(actual.exists(_.a.exists(_ == 1)))
      assert(actual.exists(_.b.exists(_ == 2L)))
      assert(actual.exists(_.c.exists(_ == true)))
      assert(actual.exists(_.d.isNull))
      assert(actual.exists(_.e.isNull))
    }
    "return correct value for some null & absent fields" in {
      val actual = Json
        .fromFields(
          List("a" -> Json.fromInt(1), "c" -> Json.True, "d" -> Json.Null, "e" -> Json.Null)
        )
        .as[Foo]

      assert(actual.exists(_.a.exists(_ == 1)))
      assert(actual.exists(_.b.isAbsent))
      assert(actual.exists(_.c.exists(_ == true)))
      assert(actual.exists(_.d.isNull))
      assert(actual.exists(_.e.isNull))
    }
  }

}
