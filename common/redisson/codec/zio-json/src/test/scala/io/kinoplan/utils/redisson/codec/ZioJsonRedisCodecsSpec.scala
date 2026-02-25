package io.kinoplan.utils.redisson.codec

import scala.util.Success

import org.scalatest.wordspec.AnyWordSpec
import zio.json._

class ZioJsonRedisCodecsSpec extends AnyWordSpec with ZioJsonRedisCodecs {

  case class User(name: String, age: Int)

  object User {
    implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
    implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
  }

  "ZioJsonRedisEncoders" should {
    "encode a case class to JSON string" in {
      val result = RedisEncoder[User].encode(User("Alice", 30))

      assert(result === """{"name":"Alice","age":30}""")
    }

    "encode an Int to JSON string" in {
      val result = RedisEncoder[Int].encode(42)

      assert(result === "42")
    }

    "encode a String to JSON string" in {
      val result = RedisEncoder[String].encode("hello")

      assert(result === "\"hello\"")
    }

    "encode a List to JSON string" in {
      val result = RedisEncoder[List[Int]].encode(List(1, 2, 3))

      assert(result === "[1,2,3]")
    }

    "encode an Option to JSON string" in {
      assert(RedisEncoder[Option[Int]].encode(Some(1)) === "1")
      assert(RedisEncoder[Option[Int]].encode(None) === "null")
    }
  }

  "ZioJsonRedisDecoders" should {
    "decode a valid JSON string to case class" in {
      val result = RedisDecoder[User].decode("""{"name":"Alice","age":30}""")

      assert(result === Success(User("Alice", 30)))
    }

    "decode a valid JSON string to Int" in {
      val result = RedisDecoder[Int].decode("42")

      assert(result === Success(42))
    }

    "decode a valid JSON string to String" in {
      val result = RedisDecoder[String].decode("\"hello\"")

      assert(result === Success("hello"))
    }

    "decode a valid JSON string to List" in {
      val result = RedisDecoder[List[Int]].decode("[1,2,3]")

      assert(result === Success(List(1, 2, 3)))
    }

    "fail with IllegalArgumentException on invalid JSON" in {
      val result = RedisDecoder[User].decode("not a json")

      assert(result.failed.map(_.isInstanceOf[IllegalArgumentException]) === Success(true))
    }

    "fail with IllegalArgumentException on type mismatch" in {
      val result = RedisDecoder[User].decode("""{"name":123,"age":"wrong"}""")

      assert(result.failed.map(_.isInstanceOf[IllegalArgumentException]) === Success(true))
    }
  }

  "ZioJsonRedisCodecs round trip" should {
    "encode and decode a case class" in {
      val user = User("Bob", 25)

      val encoded = RedisEncoder[User].encode(user)
      val decoded = RedisDecoder[User].decode(encoded)

      assert(decoded === Success(user))
    }
  }

}
