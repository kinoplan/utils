package io.kinoplan.utils.redisson.core.client

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.{Config, ReadMode, SubscriptionMode}

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.redisson.core.operation.RedisOperations

abstract class RedisSentinelClientBase extends RedisOperations with DefaultRedisCodecs {

  protected val sentinelHost: String
  protected val sentinelPort: Int
  protected val sentinelMaster: String

  private lazy val redissonConfiguration: Config = {
    val config = new Config()

    config
      .useSentinelServers()
      .setMasterName(sentinelMaster)
      .addSentinelAddress(s"redis://$sentinelHost:$sentinelPort")
      .setCheckSentinelsList(false) // unsafe
      .setReadMode(ReadMode.MASTER)
      .setSubscriptionMode(SubscriptionMode.MASTER)
      .setMasterConnectionMinimumIdleSize(1)
      .setSlaveConnectionMinimumIdleSize(1)

    config
  }

  protected lazy val redissonClient: RedissonClient = Redisson.create(redissonConfiguration)
}
