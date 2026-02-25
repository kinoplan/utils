package io.kinoplan.utils.redisson.codec

import zio.json.JsonDecoder

trait ZioJsonRedisDecoders {

  implicit def zioJsonToRedisDecoder[T: JsonDecoder]: RedisDecoder[T] =
    value => JsonDecoder[T].decodeJson(value).left.map(new IllegalArgumentException(_)).toTry

}

object ZioJsonRedisDecoders extends ZioJsonRedisDecoders
