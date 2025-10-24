package io.kinoplan.utils.zio.redisson.module

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import zio.{Duration, Task, ZIO, ZLayer, durationInt}

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.ZIntegration
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.codec.RCodec

/** Provides layers for creating and managing Redisson clients with diverse Redis configurations.
  */
object RedissonNative {

  /** Constructs a live Redis client with integration checks for Redis Native.
    *
    * @param serviceName
    *   The name used for service health checking. If not provided, defaults based on the Redis
    *   configuration type.
    * @param pingTimeout
    *   Timeout duration for Redis availability checks.
    * @return
    *   A ZLayer containing the Redis client and integration checks.
    */
  private def clientLive(serviceName: Option[String], pingTimeout: Duration) = RedisClient
    .live
    .map { redisClient =>
      val client = redisClient.get
      val config = client.redissonClient.getConfig

      lazy val configYaml = config.toYAML

      val integrationCheck = new IntegrationCheck[Task] {
        override val checkServiceName: String = serviceName.getOrElse {
          val prefix =
            if (config.isSingleConfig) "single"
            else if (config.isSentinelConfig) "sentinel"
            else if (config.isClusterConfig) "cluster"
            else if (configYaml.contains("masterSlaveServersConfig")) "master-slave"
            else "unknown"

          s"redisson.$prefix"
        }

        override def checkAvailability: Task[Boolean] =
          if (config.isSingleConfig) client.pingSingle(pingTimeout)
          else if (config.isSentinelConfig) client.pingSentinelMasterSlave(pingTimeout)
          else if (config.isClusterConfig) client.pingCluster(pingTimeout)
          else if (configYaml.contains("masterSlaveServersConfig"))
            client.pingMasterSlave(pingTimeout)
          else ZIO.succeed(false)
      }

      ZIntegration.environment(client, Set(integrationCheck))
    }

  /** Provides a layer for a live Redisson client with given configuration.
    *
    * @param config
    *   Redisson configuration for setup.
    * @param codec
    *   Implicit codec for key and value serialization.
    * @tparam K
    *   Type of the keys used in Redis.
    * @tparam V
    *   Type of the values stored in Redis.
    * @return
    *   A ZLayer that outputs a configured RedissonClient.
    */
  def redissonLive[K, V](config: Config)(implicit
    codec: RCodec[K, V]
  ): ZLayer[Any, Throwable, RedissonClient] = ZLayer.fromZIO(
    for {
      redissonConfig <- ZIO.succeed(setupRedissonConfig(config))
      client <- ZIO.attempt(Redisson.create(redissonConfig))
    } yield client
  )

  /** Constructs a live ZLayer providing Redisson and integration functionality for Native.
    *
    * @param config
    *   Redisson configuration for setup.
    * @param serviceName
    *   The name used for service health checking. If not provided, defaults based on the Redis
    *   configuration type.
    * @param pingTimeout
    *   Timeout duration for Redis availability checks.
    * @param codec
    *   Implicit codec for key and value serialization.
    * @tparam K
    *   Type of keys used in Redis.
    * @tparam V
    *   Type of values stored in Redis.
    * @return
    *   A ZLayer providing a ZIntegration with a Redis client.
    */
  def live[K, V](
    config: Config,
    serviceName: Option[String] = None,
    pingTimeout: Duration = 10.seconds
  )(implicit
    codec: RCodec[K, V]
  ): ZLayer[Any, Throwable, ZIntegration[RedisClient]] = redissonLive(config) >>>
    clientLive(serviceName, pingTimeout)

}
