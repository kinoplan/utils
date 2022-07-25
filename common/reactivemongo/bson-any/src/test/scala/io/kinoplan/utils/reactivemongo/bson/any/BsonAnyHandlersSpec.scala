package io.kinoplan.utils.reactivemongo.bson.any

import org.scalatest.wordspec.AnyWordSpec
import reactivemongo.api.bson.{BSONArray, document}
import reactivemongo.api.bson.exceptions.HandlerException

class BsonAnyHandlersSpec extends AnyWordSpec {

  "BsonAnyHandlers#writeTry" should {
    "return correct value Int" in {
      assert(
        TestData.handler.writeTry(TestData(1)).toOption.contains(document("value" -> 1))
      )
    }
    "return correct value Long" in {
      assert(
        TestData.handler.writeTry(TestData(1L)).toOption.contains(document("value" -> 1L))
      )
    }
    "return correct value Double" in {
      assert(
        TestData.handler.writeTry(TestData(1.1)).toOption.contains(document("value" -> 1.1))
      )
    }
    "return correct value String" in {
      assert(
        TestData.handler.writeTry(TestData("test")).toOption.contains(document("value" -> "test"))
      )
    }
    "return IllegalArgumentException" in {
      assert(
        TestData.handler.writeTry(TestData(Nil)).failed.toOption.map(_.getMessage).contains(
          HandlerException(
            "value",
            new IllegalArgumentException(s"unexpected value $Nil")
          ).getMessage
        )
      )
    }
  }

  "BsonAnyHandlers#readTry" should {
    "return correct value Int" in {
      assert(
        TestData.handler.readTry(document("value" -> 1)).toOption.contains(TestData(1))
      )
    }
    "return correct value Long" in {
      assert(
        TestData.handler.readTry(document("value" -> 1L)).toOption.contains(TestData(1L))
      )
    }
    "return correct value Double" in {
      assert(
        TestData.handler.readTry(document("value" -> 1.1)).toOption.contains(TestData(1.1))
      )
    }
    "return correct value String" in {
      assert(
        TestData.handler.readTry(document("value" -> "test")).toOption.contains(TestData("test"))
      )
    }
    "return IllegalArgumentException" in {
      assert(
        TestData.handler.readTry(document("value" -> BSONArray.empty)).failed.toOption.map(
          _.getMessage
        ).contains(
          HandlerException(
            "value",
            new IllegalArgumentException(s"unexpected value ${BSONArray.empty}")
          ).getMessage
        )
      )
    }
  }

}
