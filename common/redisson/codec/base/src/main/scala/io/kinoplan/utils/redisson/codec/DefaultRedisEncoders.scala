package io.kinoplan.utils.redisson.codec

trait DefaultRedisEncoders {
  implicit val stringRedisEncoder: RedisEncoder[String] = identity[String]
  implicit val intRedisEncoder: RedisEncoder[Int] = _.toString
  implicit val longRedisEncoder: RedisEncoder[Long] = _.toString
}

object DefaultRedisEncoders extends DefaultRedisEncoders
