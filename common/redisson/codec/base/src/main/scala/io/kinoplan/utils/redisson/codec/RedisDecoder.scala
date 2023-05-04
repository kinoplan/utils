package io.kinoplan.utils.redisson.codec

import io.kinoplan.utils.redisson.codec.base.BaseRedisDecoder

trait RedisDecoder[T] extends BaseRedisDecoder[String, T]

object RedisDecoder {

  def apply[T](implicit
    decoder: RedisDecoder[T]
  ): RedisDecoder[T] = decoder

}
