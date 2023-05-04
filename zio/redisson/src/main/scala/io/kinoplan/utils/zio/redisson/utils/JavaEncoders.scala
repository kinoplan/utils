package io.kinoplan.utils.zio.redisson.utils

import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.codec.base.BaseRedisEncoder
import io.kinoplan.utils.zio.redisson.codec.RCodec

private[redisson] object JavaEncoders {

  def fromMap[T, K, V](entity: Map[K, T])(implicit
    codec: RCodec[K, V],
    encoder: BaseRedisEncoder[T, V]
  ): java.util.Map[K, V] = entity
    .view
    .map { case (k, v) =>
      k -> codec.encode(v)
    }
    .toMap
    .asJava

  def fromMapWithWeight[T, V](entity: Map[T, Double])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): java.util.Map[V, java.lang.Double] = entity
    .view
    .map { case (k, v) =>
      codec.encode(k) -> Double.box(v)
    }
    .toMap
    .asJava

}
