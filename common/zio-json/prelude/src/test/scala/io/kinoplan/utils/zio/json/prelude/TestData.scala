package io.kinoplan.utils.zio.json.prelude

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

case class TestData(
  nes: NonEmptySet[Int],
  ness: NonEmptySortedSet[String],
  nel: NonEmptyList[Int],
  nem: NonEmptyMap[String, String],
  nesm: NonEmptySortedMap[String, Int]
)

trait TestDataJson extends ZioJsonCodecPrelude {
  implicit val encoder: JsonEncoder[TestData] = DeriveJsonEncoder.gen[TestData]
  implicit val decoder: JsonDecoder[TestData] = DeriveJsonDecoder.gen[TestData]
}

object TestData extends TestDataJson {

  val data: TestData = TestData(
    nes = NonEmptySet(1, 1, 2, 3),
    ness = NonEmptySortedSet("a", "b", "c", "d", "a"),
    nel = NonEmptyList(1, 1, 2, 3, 4, 5, 6),
    nem = NonEmptyMap("a" -> "3", "c" -> "1", "b" -> "2"),
    nesm = NonEmptySortedMap("a" -> 3, "c" -> 1, "b" -> 2)
  )

  val validJson: String = """{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }"""

  val emptyNesJson: String = """{
      "nes": [],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }"""

  val invalidNesJson: String = """{
      "nes": [1, true, "a"],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }"""

  val emptyNessJson: String = """{
      "nes": [1, 2, 3],
      "ness": [],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }"""

  val invalidNessJson: String = """{
      "nes": [1, 2, 3],
      "ness": [1, true, "a"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }"""

  val emptyNelJson: String = """{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }"""

  val invalidNelJson: String = """{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, true, "a"],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }"""

  val emptyNemJson: String = """{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nem": {},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }"""

  val invalidNemJson: String = """{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nem": {"a": 1, "b": true, "c": "a"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }"""

  val emptyNesmJson: String = """{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {}
    }"""

  val invalidNesmJson: String = """{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 1, "b": true, "c": "a"}
    }"""

}
