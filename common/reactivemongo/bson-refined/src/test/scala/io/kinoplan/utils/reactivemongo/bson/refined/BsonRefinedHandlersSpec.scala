package io.kinoplan.utils.reactivemongo.bson.refined

import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import org.scalatest.wordspec.AnyWordSpec
import reactivemongo.api.bson.document

import io.kinoplan.utils.reactivemongo.bson.refined.RefinedCustomTypes.Percent

class BsonRefinedHandlersSpec extends AnyWordSpec {

  val data: TestData = TestData(
    name = NonEmptyString.unsafeFrom("John"),
    level = PosInt.unsafeFrom(1),
    percent = Percent.unsafeFrom(100)
  )

  "BsonRefinedHandlers#writeTry" should {
    "return correct value" in
      assert(
        TestData
          .handler
          .writeTry(data)
          .toOption
          .contains(document("name" -> "John", "level" -> 1, "percent" -> 100))
      )
  }

  "BsonAnyHandlers#readTry" should {
    "return correct value" in
      assert(
        TestData
          .handler
          .readTry(document("name" -> "John", "level" -> 1, "percent" -> 100))
          .toOption
          .contains(data)
      )
    "return IllegalArgumentException" in
      assert(
        TestData
          .handler
          .readTry(document("name" -> "John", "level" -> 1, "percent" -> 101))
          .failed
          .toOption
          .map(_.getMessage)
          .nonEmpty
      )
  }

}
