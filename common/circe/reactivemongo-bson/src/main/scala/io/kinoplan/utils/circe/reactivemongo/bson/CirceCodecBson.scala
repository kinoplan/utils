package io.kinoplan.utils.circe.reactivemongo.bson

import io.circe.{Decoder, Encoder}
import reactivemongo.api.bson._

trait CirceCodecBson {

  implicit val encodeBSONObjectID: Encoder[BSONObjectID] = Encoder
    .encodeString
    .contramap[BSONObjectID](_.stringify)

  implicit val decodeBSONObjectID: Decoder[BSONObjectID] = Decoder
    .decodeString
    .emapTry(BSONObjectID.parse)

}

object CirceCodecBson extends CirceCodecBson
