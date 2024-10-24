package io.kinoplan.utils.redisson.codec

import scala.util.Try

trait RedisDecoder[T] {
  def decode(value: String): Try[T]
}

object RedisDecoder {

  def apply[T](implicit
    decoder: RedisDecoder[T]
  ): RedisDecoder[T] = decoder

}
