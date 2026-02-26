package io.kinoplan.utils.zio.json.reactivemongo.bson

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.json._

import io.kinoplan.utils.zio.json.reactivemongo.bson.TestData._

class ZioJsonCodecBsonSpec extends AnyFlatSpec with Matchers {

  it should "correctly encode BSONObjectID" in assert(data.toJson === validJson)

  it should "correctly decode BSONObjectID" in assert(validJson.fromJson[TestData].contains(data))

  it should "fail decode BSONObjectID on invalidJson" in
    assert(invalidJson.fromJson[TestData].isLeft)

}
