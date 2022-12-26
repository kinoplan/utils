package io.kinoplan.utils.redisson.codec

import io.circe.{Decoder, parser}

import io.kinoplan.utils.redisson.core.codec.RedisDecoder

trait CirceRedisDecoders {

  implicit def circeToRedisDecoder[T: Decoder]: RedisDecoder[T] =
    parser.parse(_).flatMap(_.as[T]).toTry

}
