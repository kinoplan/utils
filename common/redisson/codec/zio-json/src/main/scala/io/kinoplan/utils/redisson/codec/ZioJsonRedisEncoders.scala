package io.kinoplan.utils.redisson.codec

import zio.json._

trait ZioJsonRedisEncoders {
  implicit def zioJsonToRedisEncoder[T: JsonEncoder]: RedisEncoder[T] = _.toJson
}

object ZioJsonRedisEncoders extends ZioJsonRedisEncoders
