package io.kinoplan.utils.reactivemongo.bson.zio.prelude

import org.scalatest.wordspec.AnyWordSpec
import reactivemongo.api.bson.BSONDocument.pretty

import io.kinoplan.utils.reactivemongo.bson.zio.prelude.TestData._

class BsonZioPreludeHandlersSpec extends AnyWordSpec {

  "BsonZioPreludeHandlers#writeTry" should {
    "return correct value" in
      assert(handler.writeTry(data).toOption.map(pretty).contains(pretty(bson)))
  }

  "BsonZioPreludeHandlers#readTry" should {
    "return correct value" in assert(handler.readTry(bson).toOption.contains(data))
    "return IllegalArgumentException for NonEmptySet" in {
      assert(handler.readTry(emptyNesBson).failed.toOption.map(_.getMessage).nonEmpty)
      assert(handler.readTry(badNesBson).failed.toOption.map(_.getMessage).nonEmpty)
    }
    "return IllegalArgumentException for NonEmptySortedSet" in {
      assert(handler.readTry(emptyNessBson).failed.toOption.map(_.getMessage).nonEmpty)
      assert(handler.readTry(badNessBson).failed.toOption.map(_.getMessage).nonEmpty)
    }
    "return IllegalArgumentException for NonEmptyList" in {
      assert(handler.readTry(emptyNelBson).failed.toOption.map(_.getMessage).nonEmpty)
      assert(handler.readTry(badNelBson).failed.toOption.map(_.getMessage).nonEmpty)
    }
    "return IllegalArgumentException for NonEmptyChunk" in {
      assert(handler.readTry(emptyNecBson).failed.toOption.map(_.getMessage).nonEmpty)
      assert(handler.readTry(badNecBson).failed.toOption.map(_.getMessage).nonEmpty)
    }
    "return IllegalArgumentException for NonEmptyMap" in {
      assert(handler.readTry(emptyNemBson).failed.toOption.map(_.getMessage).nonEmpty)
      assert(handler.readTry(badNemBson).failed.toOption.map(_.getMessage).nonEmpty)
    }

    "return IllegalArgumentException for NonEmptySortedMap" in {
      assert(handler.readTry(emptyNesmBson).failed.toOption.map(_.getMessage).nonEmpty)
      assert(handler.readTry(badNesmBson).failed.toOption.map(_.getMessage).nonEmpty)
    }
  }

}
