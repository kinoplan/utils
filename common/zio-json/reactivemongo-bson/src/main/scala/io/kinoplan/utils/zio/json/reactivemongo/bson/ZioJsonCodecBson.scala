package io.kinoplan.utils.zio.json.reactivemongo.bson

import reactivemongo.api.bson._
import zio.json.{JsonDecoder, JsonEncoder}

trait ZioJsonCodecBson {

  implicit val encodeBSONObjectID: JsonEncoder[BSONObjectID] =
    JsonEncoder[String].contramap[BSONObjectID](_.stringify)

  implicit val decodeBSONObjectID: JsonDecoder[BSONObjectID] =
    JsonDecoder[String].mapOrFail(str => BSONObjectID.parse(str).toEither.left.map(_.getMessage))

}

object ZioJsonCodecBson extends ZioJsonCodecBson
