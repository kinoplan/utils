package io.kinoplan.utils.zio.json.prelude

import org.scalatest.wordspec.AnyWordSpec
import zio.json._

import io.kinoplan.utils.zio.json.prelude.TestData._

class ZioJsonCodecPreludeSpec extends AnyWordSpec {

  "ZioJsonCodecPrelude#encoder" should {
    "return correct value" in assert(data.toJson.fromJson[TestData].contains(data))
  }

  "ZioJsonCodecPrelude#decoder" should {
    "return correct value" in assert(validJson.fromJson[TestData].contains(data))
    "return error for NonEmptySet" in {
      assert(emptyNesJson.fromJson[TestData].isLeft)
      assert(invalidNesJson.fromJson[TestData].isLeft)
    }
    "return error for NonEmptySortedSet" in {
      assert(emptyNessJson.fromJson[TestData].isLeft)
      assert(invalidNessJson.fromJson[TestData].isLeft)
    }
    "return error for NonEmptyList" in {
      assert(emptyNelJson.fromJson[TestData].isLeft)
      assert(invalidNelJson.fromJson[TestData].isLeft)
    }
    "return error for NonEmptyMap" in {
      assert(emptyNemJson.fromJson[TestData].isLeft)
      assert(invalidNemJson.fromJson[TestData].isLeft)
    }
    "return error for NonEmptySortedMap" in {
      assert(emptyNesmJson.fromJson[TestData].isLeft)
      assert(invalidNesmJson.fromJson[TestData].isLeft)
    }
  }

}
