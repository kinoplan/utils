package io.kinoplan.utils.redisson.codec

trait DefaultRedisEncoders {
  implicit val stringRedisEncoder: RedisEncoder[String] = identity
  implicit val intRedisEncoder: RedisEncoder[Int] = _.toString
  implicit val longRedisEncoder: RedisEncoder[Long] = _.toString
  implicit val doubleRedisEncoder: RedisEncoder[Double] = _.toString
  implicit val booleanRedisEncoder: RedisEncoder[Boolean] = _.toString
}

object DefaultRedisEncoders extends DefaultRedisEncoders
