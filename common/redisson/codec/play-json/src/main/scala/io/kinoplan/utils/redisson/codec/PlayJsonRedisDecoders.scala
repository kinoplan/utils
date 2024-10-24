package io.kinoplan.utils.redisson.codec

import scala.util.Try

import play.api.libs.json.{Json, Reads}

trait PlayJsonRedisDecoders {

  implicit def playJsonToRedisDecoder[T: Reads]: RedisDecoder[T] =
    value => Try(Json.parse(value).as[T])

}
