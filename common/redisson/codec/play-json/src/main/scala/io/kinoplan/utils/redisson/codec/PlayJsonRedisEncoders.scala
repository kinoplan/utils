package io.kinoplan.utils.redisson.codec

import play.api.libs.json.{Json, Writes}

import io.kinoplan.utils.redisson.base.codec.RedisEncoder

trait PlayJsonRedisEncoders {
  implicit def playJsonToRedisEncoder[T: Writes]: RedisEncoder[T] = Json.toJson[T](_).toString
}
