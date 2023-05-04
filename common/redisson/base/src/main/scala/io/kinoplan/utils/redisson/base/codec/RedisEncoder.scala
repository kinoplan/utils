package io.kinoplan.utils.redisson.base.codec

trait RedisEncoder[T] {
  def encode(value: T): String
}

object RedisEncoder {

  def apply[T](implicit
    encoder: RedisEncoder[T]
  ): RedisEncoder[T] = encoder

}
