package io.kinoplan.utils.redisson.core.client

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.redisson.core.operation.RedisOperations

abstract class RedisMasterClientBase extends RedisOperations with DefaultRedisCodecs {

  protected val host: String
  protected val port: Int

  private lazy val redissonConfiguration: Config = {
    val config = new Config()

    config.useSingleServer().setAddress(s"redis://$host:$port").setConnectionMinimumIdleSize(1)

    config
  }

  protected lazy val redissonClient: RedissonClient = Redisson.create(redissonConfiguration)
}
