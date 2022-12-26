package io.kinoplan.utils.redisson.codec

import scala.util.Try

import play.api.libs.json.{Json, Reads}

import io.kinoplan.utils.redisson.core.codec.RedisDecoder

trait PlayJsonRedisDecoders {

  implicit def playJsonToRedisDecoder[T: Reads]: RedisDecoder[T] =
    value => Try(Json.parse(value).as[T])

}
