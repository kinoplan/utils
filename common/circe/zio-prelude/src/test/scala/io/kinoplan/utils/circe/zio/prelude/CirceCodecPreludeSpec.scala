package io.kinoplan.utils.circe.zio.prelude

import io.circe.syntax.EncoderOps
import org.scalatest.wordspec.AnyWordSpec

import io.kinoplan.utils.circe.zio.prelude.TestData._

class CirceCodecPreludeSpec extends AnyWordSpec {

  "CirceCodecPrelude#encoder" should {
    "return correct value" in assert(validJson.contains(data.asJson))
  }

  "CirceCodecPrelude#decoder" should {
    "return correct value" in assert(validJson.flatMap(_.as[TestData]).contains(data))
    "return error for NonEmptySet" in {
      assert(emptyNesJson.flatMap(_.as[TestData]).isLeft)
      assert(invalidNesJson.flatMap(_.as[TestData]).isLeft)
    }
    "return error for NonEmptySortedSet" in {
      assert(emptyNessJson.flatMap(_.as[TestData]).isLeft)
      assert(invalidNessJson.flatMap(_.as[TestData]).isLeft)
    }
    "return error for NonEmptyList" in {
      assert(emptyNelJson.flatMap(_.as[TestData]).isLeft)
      assert(invalidNelJson.flatMap(_.as[TestData]).isLeft)
    }
    "return error for NonEmptyChunk" in {
      assert(emptyNecJson.flatMap(_.as[TestData]).isLeft)
      assert(invalidNecJson.flatMap(_.as[TestData]).isLeft)
    }
    "return error for NonEmptyMap" in {
      assert(emptyNemJson.flatMap(_.as[TestData]).isLeft)
      assert(invalidNemJson.flatMap(_.as[TestData]).isLeft)
    }
    "return error for NonEmptySortedMap" in {
      assert(emptyNesmJson.flatMap(_.as[TestData]).isLeft)
      assert(invalidNesmJson.flatMap(_.as[TestData]).isLeft)
    }
  }

}
