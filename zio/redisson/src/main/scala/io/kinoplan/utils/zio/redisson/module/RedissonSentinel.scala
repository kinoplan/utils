package io.kinoplan.utils.zio.redisson.module

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import zio.{Duration, Task, ZIO, ZLayer, durationInt}

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.ZIntegration
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.config.{RedisCommonConfig, RedisSentinelConfig}

/** Provides layers for creating and managing a Redis Sentinel client within a ZIO environment.
  */
object RedissonSentinel {

  /** Constructs a live Redis client with integration checks for Redis Sentinel.
    *
    * @param serviceName
    *   The name used for service health checking.
    * @param pingTimeout
    *   Timeout duration for Redis availability checks.
    * @return
    *   A ZLayer containing the Redis client and integration checks for Sentinel.
    */
  private def clientLive(serviceName: String, pingTimeout: Duration) = RedisClient
    .live
    .map { redisClient =>
      val client = redisClient.get

      val integrationCheck = new IntegrationCheck[Task] {
        override val checkServiceName: String = serviceName

        override def checkAvailability: Task[Boolean] = client.pingSentinelMasterSlave(pingTimeout)
      }

      ZIntegration.environment(client, Set(integrationCheck))
    }

  /** Provides a layer for a live Redisson client configured for Sentinel operation.
    *
    * @param configurator
    *   Custom configuration function for Redisson.
    * @param codec
    *   Implicit codec for key and value serialization.
    * @tparam K
    *   Type of keys used in Redis.
    * @tparam V
    *   Type of values stored in Redis.
    * @return
    *   A ZLayer that outputs a configured RedissonClient.
    */
  def redissonLive[K, V](configurator: Config => Config = identity)(implicit
    codec: RCodec[K, V]
  ): ZLayer[Any, Throwable, RedissonClient] = RedisCommonConfig.live ++ RedisSentinelConfig.live >>>
    ZLayer.fromZIO(
      for {
        commonConfig <- ZIO.service[RedisCommonConfig]
        sentinelConfig <- ZIO.service[RedisSentinelConfig]
        combinedConfig = sentinelConfig.redissonConfig(commonConfig.redissonConfig)
        redissonConfig = setupRedissonConfig(combinedConfig, configurator)
        client <- ZIO.attempt(Redisson.create(redissonConfig))
      } yield client
    )

  /** Constructs a live ZLayer providing Redisson and integration functionality for Sentinel.
    *
    * @param configurator
    *   Custom configuration function for Redisson.
    * @param serviceName
    *   The name used for service health checking.
    * @param pingTimeout
    *   Timeout duration for Redis availability checks.
    * @param codec
    *   Implicit codec for key and value serialization.
    * @tparam K
    *   Type of keys used in Redis.
    * @tparam V
    *   Type of values stored in Redis.
    * @return
    *   A ZLayer providing a ZIntegration with a Redis client configured for Sentinel.
    */
  def live[K, V](
    configurator: Config => Config = identity,
    serviceName: String = "redisson.sentinel",
    pingTimeout: Duration = 10.seconds
  )(implicit
    codec: RCodec[K, V]
  ): ZLayer[Any, Throwable, ZIntegration[RedisClient]] = redissonLive(configurator) >>>
    clientLive(serviceName, pingTimeout)

}
