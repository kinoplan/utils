package io.kinoplan.utils.zio.redisson.utils

import io.kinoplan.utils.redisson.codec.RedisEncoder

import scala.jdk.CollectionConverters.MapHasAsJava

private[redisson] object JavaEncoders {

  def fromMap[T: RedisEncoder](entity: Map[String, T]): java.util.Map[String, String] = entity
    .view
    .map { case (k, v) =>
      k -> RedisEncoder[T].encode(v)
    }
    .toMap
    .asJava

  def fromMapWithWeight[T: RedisEncoder](
    entity: Map[T, Double]
  ): java.util.Map[String, java.lang.Double] = entity
    .view
    .map { case (k, v) =>
      RedisEncoder[T].encode(k) -> Double.box(v)
    }
    .toMap
    .asJava

}
