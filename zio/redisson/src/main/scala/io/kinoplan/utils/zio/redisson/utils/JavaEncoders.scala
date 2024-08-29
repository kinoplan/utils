package io.kinoplan.utils.zio.redisson.utils

import io.kinoplan.utils.redisson.base.codec.RedisEncoder
import io.kinoplan.utils.redisson.crossCollectionConverters._

private[redisson] object JavaEncoders {

  def encodeMapWithWeight[T: RedisEncoder](
    entity: Map[T, Double]
  ): java.util.Map[String, java.lang.Double] = entity
    .view
    .map { case (k, v) =>
      RedisEncoder[T].encode(k) -> Double.box(v)
    }
    .toMap
    .asJava

}
