package io.kinoplan.utils.redisson.codec

import io.circe.Encoder
import io.circe.syntax.EncoderOps

trait CirceRedisEncoders {
  implicit def circeToRedisEncoder[T: Encoder]: RedisEncoder[T] = _.asJson.noSpaces
}

object CirceRedisEncoders extends CirceRedisEncoders
