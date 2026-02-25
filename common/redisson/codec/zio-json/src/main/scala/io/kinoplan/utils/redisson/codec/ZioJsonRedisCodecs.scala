package io.kinoplan.utils.redisson.codec

trait ZioJsonRedisCodecs extends ZioJsonRedisEncoders with ZioJsonRedisDecoders

object ZioJsonRedisCodecs extends ZioJsonRedisCodecs
