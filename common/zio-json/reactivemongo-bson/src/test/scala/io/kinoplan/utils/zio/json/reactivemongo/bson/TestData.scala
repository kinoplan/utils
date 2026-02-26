package io.kinoplan.utils.zio.json.reactivemongo.bson

import reactivemongo.api.bson.BSONObjectID
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class TestData(id: Option[BSONObjectID])

trait TestDataJson extends ZioJsonCodecBson {
  implicit val encoder: JsonEncoder[TestData] = DeriveJsonEncoder.gen[TestData]
  implicit val decoder: JsonDecoder[TestData] = DeriveJsonDecoder.gen[TestData]
}

object TestData extends TestDataJson {

  private val id = "671fcd49f58ea8883b4763f3"
  private val invalidId = "671fcd49f58ea8883b4763f"

  val data: TestData = TestData(BSONObjectID.parse(id).toOption)

  val validJson: String = s"""{"id":"$id"}"""

  val invalidJson: String = s"""{"id":"$invalidId"}"""

}
