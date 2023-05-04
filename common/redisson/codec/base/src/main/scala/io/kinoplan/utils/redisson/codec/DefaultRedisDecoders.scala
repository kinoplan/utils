package io.kinoplan.utils.redisson.codec

import scala.util.{Success, Try}

trait DefaultRedisDecoders {
  implicit val stringRedisDecoder: RedisDecoder[String] = Success(_)
  implicit val intRedisDecoder: RedisDecoder[Int] = value => Try(value.toInt)
  implicit val longRedisDecoder: RedisDecoder[Long] = value => Try(value.toLong)
  implicit val doubleRedisDecoder: RedisDecoder[Double] = value => Try(value.toDouble)
}

object DefaultRedisDecoders extends DefaultRedisDecoders
