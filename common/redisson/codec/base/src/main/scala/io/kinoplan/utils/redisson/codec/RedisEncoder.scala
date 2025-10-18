package io.kinoplan.utils.redisson.codec

import io.kinoplan.utils.redisson.codec.base.BaseRedisEncoder

trait RedisEncoder[T] extends BaseRedisEncoder[T, String]

object RedisEncoder {

  def apply[T](implicit
    encoder: RedisEncoder[T]
  ): RedisEncoder[T] = encoder

}
