package io.kinoplan.utils.redisson.codec

import io.circe.{Decoder, parser}

trait CirceRedisDecoders {

  implicit def circeToRedisDecoder[T: Decoder]: RedisDecoder[T] =
    parser.parse(_).flatMap(_.as[T]).toTry

}

object CirceRedisDecoders extends CirceRedisDecoders
