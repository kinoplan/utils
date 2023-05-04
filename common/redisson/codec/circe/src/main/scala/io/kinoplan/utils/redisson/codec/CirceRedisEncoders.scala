package io.kinoplan.utils.redisson.codec

import io.circe.Encoder
import io.circe.syntax.EncoderOps

import io.kinoplan.utils.redisson.base.codec.RedisEncoder

trait CirceRedisEncoders {
  implicit def circeToRedisEncoder[T: Encoder]: RedisEncoder[T] = _.asJson.noSpaces
}
