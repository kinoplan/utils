package io.kinoplan.utils.nullable

import org.scalatest.wordspec.AnyWordSpec
import zio.json._

class ZioJsonCodecNullableSpec extends AnyWordSpec {

  case class Foo(
    a: Nullable[Int],
    b: Nullable[Long],
    c: Nullable[Boolean],
    d: Nullable[String],
    e: Nullable[Set[Int]]
  )

  object Foo extends ZioJsonCodecNullable {
    implicit val fooDecoder: JsonDecoder[Foo] = DeriveJsonDecoder.gen[Foo]
    implicit val fooEncoder: JsonEncoder[Foo] = DeriveJsonEncoder.gen[Foo]
  }

  "ZioJsonCodecNullable#nullableEncoder" should {
    "return correct json for non null fields" in {
      val actual = Foo(
        a = Nullable.NonNull(1),
        b = Nullable.NonNull(2L),
        c = Nullable.NonNull(true),
        d = Nullable.NonNull(""),
        e = Nullable.NonNull(Set(1, 2, 3))
      )

      val json = actual.toJson
      val decoded = json.fromJson[Foo]

      assert(decoded.exists(_.a.exists(_ == 1)))
      assert(decoded.exists(_.b.exists(_ == 2L)))
      assert(decoded.exists(_.c.exists(_ == true)))
      assert(decoded.exists(_.d.exists(_ == "")))
      assert(decoded.exists(_.e.exists(_ == Set(1, 2, 3))))
    }
    "return correct json for some null fields" in {
      val actual = Foo(
        a = Nullable.NonNull(1),
        b = Nullable.NonNull(2L),
        c = Nullable.NonNull(true),
        d = Nullable.orNull(Option.empty[String]),
        e = Nullable.orNull(Option.empty[Set[Int]])
      )

      val json = actual.toJson

      assert(json.contains("\"d\":null"))
      assert(json.contains("\"e\":null"))

      val decoded = json.fromJson[Foo]

      assert(decoded.exists(_.d.isNull))
      assert(decoded.exists(_.e.isNull))
    }
    "return correct json for some null & absent fields" in {
      val actual = Foo(
        a = Nullable.NonNull(1),
        b = Nullable.orAbsent(Option.empty[Long]),
        c = Nullable.NonNull(true),
        d = Nullable.orNull(Option.empty[String]),
        e = Nullable.orNull(Option.empty[Set[Int]])
      )

      val json = actual.toJson

      assert(!json.contains("\"b\""))
      assert(json.contains("\"d\":null"))

      val decoded = json.fromJson[Foo]

      assert(decoded.exists(_.a.exists(_ == 1)))
      assert(decoded.exists(_.b.isAbsent))
      assert(decoded.exists(_.c.exists(_ == true)))
      assert(decoded.exists(_.d.isNull))
      assert(decoded.exists(_.e.isNull))
    }
  }

  "ZioJsonCodecNullable#nullableDecoder" should {
    "return incorrect value for non valid field" in {
      val json = """{"a":true,"b":2,"c":true,"d":"","e":[1,2,3]}"""

      assert(json.fromJson[Foo].isLeft)
    }
    "return correct value for non null fields" in {
      val json = """{"a":1,"b":2,"c":true,"d":"","e":[1,2,3]}"""

      val decoded = json.fromJson[Foo]

      assert(decoded.exists(_.a.exists(_ == 1)))
      assert(decoded.exists(_.b.exists(_ == 2L)))
      assert(decoded.exists(_.c.exists(_ == true)))
      assert(decoded.exists(_.d.exists(_ == "")))
      assert(decoded.exists(_.e.exists(_ == Set(1, 2, 3))))
    }
    "return correct value for some null fields" in {
      val json = """{"a":1,"b":2,"c":true,"d":null,"e":null}"""

      val decoded = json.fromJson[Foo]

      assert(decoded.exists(_.a.exists(_ == 1)))
      assert(decoded.exists(_.b.exists(_ == 2L)))
      assert(decoded.exists(_.c.exists(_ == true)))
      assert(decoded.exists(_.d.isNull))
      assert(decoded.exists(_.e.isNull))
    }
    "return correct value for some null & absent fields" in {
      val json = """{"a":1,"c":true,"d":null,"e":null}"""

      val decoded = json.fromJson[Foo]

      assert(decoded.exists(_.a.exists(_ == 1)))
      assert(decoded.exists(_.b.isAbsent))
      assert(decoded.exists(_.c.exists(_ == true)))
      assert(decoded.exists(_.d.isNull))
      assert(decoded.exists(_.e.isNull))
    }
  }

}
