package io.kinoplan.utils.redisson.codec

import play.api.libs.json.{Json, Writes}

trait PlayJsonRedisEncoders {
  implicit def playJsonToRedisEncoder[T: Writes]: RedisEncoder[T] = Json.toJson[T](_).toString
}
