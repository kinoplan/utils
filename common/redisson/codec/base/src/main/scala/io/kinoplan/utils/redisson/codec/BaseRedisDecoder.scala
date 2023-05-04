package io.kinoplan.utils.redisson.codec

import scala.util.Try

trait BaseRedisDecoder[T, V] {
  type ValueType = V

  def decode(value: V): Try[T]
}
