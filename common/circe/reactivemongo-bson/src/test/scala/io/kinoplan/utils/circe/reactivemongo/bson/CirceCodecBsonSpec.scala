package io.kinoplan.utils.circe.reactivemongo.bson

import io.circe.syntax.EncoderOps
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import io.kinoplan.utils.circe.reactivemongo.bson.TestData._

class CirceCodecBsonSpec extends AnyFlatSpec with Matchers {

  it should "correctly encode BSONObjectID" in assert(validJson.contains(data.asJson))

  it should "correctly decode BSONObjectID" in
    assert(validJson.flatMap(_.as[TestData]).contains(data))

  it should "fail decode BSONObjectID on invalidJson" in
    assert(invalidJson.flatMap(_.as[TestData]).isLeft)

}
