package io.kinoplan.utils.circe.reactivemongo.bson

import io.circe.{Decoder, Encoder, Json, ParsingFailure}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.parse
import reactivemongo.api.bson.BSONObjectID

case class TestData(id: Option[BSONObjectID])

trait TestDataJson extends CirceCodecBson {
  implicit val encoder: Encoder[TestData] = deriveEncoder[TestData]
  implicit val decoder: Decoder[TestData] = deriveDecoder[TestData]
}

object TestData extends TestDataJson {

  private val id = "671fcd49f58ea8883b4763f3"
  private val invalidId = "671fcd49f58ea8883b4763f"

  val data: TestData = TestData(BSONObjectID.parse(id).toOption)

  val validJson: Either[ParsingFailure, Json] = parse(s"""{
      "id": "$id"
    }""")

  val invalidJson: Either[ParsingFailure, Json] = parse(s"""{
      "id": "$invalidId"
    }""")

}
