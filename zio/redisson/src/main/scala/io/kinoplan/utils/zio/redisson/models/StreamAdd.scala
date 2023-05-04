package io.kinoplan.utils.zio.redisson.models

import org.redisson.api.stream.{StreamAddArgs => RStreamAddArgs}

import io.kinoplan.utils.redisson.codec.base.BaseRedisEncoder
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.JavaEncoders

object StreamAdd {

  def create[T, K, V](field: K, value: T)(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
  ): RStreamAddArgs[K, V] = RStreamAddArgs.entry(field, codec.encode(value))

  def create[T, K, V](fieldsValue: Map[K, T])(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
  ): RStreamAddArgs[K, V] = RStreamAddArgs.entries(JavaEncoders.fromMap(fieldsValue))

}
