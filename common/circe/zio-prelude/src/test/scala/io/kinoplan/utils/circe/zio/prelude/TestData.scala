package io.kinoplan.utils.circe.zio.prelude

import io.circe.{Decoder, Encoder, Json, ParsingFailure}
import io.circe.generic.semiauto._
import io.circe.parser.parse
import zio.NonEmptyChunk
import zio.prelude.{NonEmptyList, NonEmptyMap, NonEmptySet, NonEmptySortedMap, NonEmptySortedSet}

case class TestData(
  nes: NonEmptySet[Int],
  ness: NonEmptySortedSet[String],
  nel: NonEmptyList[Int],
  nec: NonEmptyChunk[Int],
  nem: NonEmptyMap[String, String],
  nesm: NonEmptySortedMap[String, Int]
)

trait TestDataJson extends CirceCodecPrelude {
  implicit val encoder: Encoder[TestData] = deriveEncoder[TestData]
  implicit val decoder: Decoder[TestData] = deriveDecoder[TestData]
}

object TestData extends TestDataJson {

  val data: TestData = TestData(
    nes = NonEmptySet(1, 1, 2, 3),
    ness = NonEmptySortedSet("a", "b", "c", "d", "a"),
    nel = NonEmptyList(1, 1, 2, 3, 4, 5, 6),
    nec = NonEmptyChunk(1, 2, 2, 3),
    nem = NonEmptyMap("a" -> "3", "c" -> "1", "b" -> "2"),
    nesm = NonEmptySortedMap("a" -> 3, "c" -> 1, "b" -> 2)
  )

  val validJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [1, 2, 2, 3],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val emptyNesJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [1, 2, 2, 3],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val invalidNesJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, true, "a"],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [1, 2, 2, 3],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val emptyNessJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": [],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [1, 2, 2, 3],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val invalidNessJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": [1, true, "a"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [1, 2, 2, 3],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val emptyNelJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [],
      "nec": [1, 2, 2, 3],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val invalidNelJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, true, "a"],
      "nec": [1, 2, 2, 3],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val emptyNecJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val invalidNecJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [1, true, "a"],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val emptyNemJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [1, 2, 2, 3],
      "nem": {},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val invalidNemJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [1, 2, 2, 3],
      "nem": {"a": 1, "b": true, "c": "a"},
      "nesm": {"a": 3, "b": 2, "c": 1}
    }""")

  val emptyNesmJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [1, 2, 2, 3],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {}
    }""")

  val invalidNesmJson: Either[ParsingFailure, Json] = parse("""{
      "nes": [1, 2, 3],
      "ness": ["a", "b", "c", "d"],
      "nel": [1, 1, 2, 3, 4, 5, 6],
      "nec": [1, 2, 2, 3],
      "nem": {"c": "1", "b": "2", "a": "3"},
      "nesm": {"a": 1, "b": true, "c": "a"}
    }""")

}
