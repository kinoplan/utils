package io.kinoplan.utils.redisson.codec

trait BaseRedisEncoder[T, V] {
  def encode(value: T): V
}
