package io.kinoplan.utils.redisson.core.codec

trait DefaultRedisEncoders {
  implicit val stringRedisEncoder: RedisEncoder[String] = identity[String]
  implicit val intRedisEncoder: RedisEncoder[Int] = _.toString
  implicit val longRedisEncoder: RedisEncoder[Long] = _.toString
}
