package io.kinoplan.utils.redisson.base.codec

import scala.util.{Success, Try}

trait DefaultRedisDecoders {
  implicit val stringRedisDecoder: RedisDecoder[String] = Success(_)
  implicit val intRedisDecoder: RedisDecoder[Int] = value => Try(value.toInt)
  implicit val longRedisDecoder: RedisDecoder[Long] = value => Try(value.toLong)
}
