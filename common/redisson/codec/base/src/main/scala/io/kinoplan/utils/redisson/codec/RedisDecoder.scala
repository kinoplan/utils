package io.kinoplan.utils.redisson.codec

trait RedisDecoder[T] extends BaseRedisDecoder[T, String]

object RedisDecoder {

  def apply[T](implicit
    decoder: RedisDecoder[T]
  ): RedisDecoder[T] = decoder

}
