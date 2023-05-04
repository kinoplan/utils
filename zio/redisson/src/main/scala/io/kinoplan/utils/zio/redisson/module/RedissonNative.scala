package io.kinoplan.utils.zio.redisson.module

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.ZIntegration
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.codec.RCodec
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import zio.{Duration, Task, ZIO, ZLayer, durationInt}

object RedissonNative {

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

  def redissonLive(
    config: Config,
    codec: RCodec = RCodec.dummyCodec
  ): ZLayer[Any, Throwable, RedissonClient] = ZLayer.fromZIO(
    for {
      redissonConfig <- ZIO.succeed(setupRedissonConfig(config, codec))
      client <- ZIO.attempt(Redisson.create(redissonConfig))
    } yield client
  )

  def live(
    config: Config,
    codec: RCodec = RCodec.dummyCodec,
    serviceName: Option[String] = None,
    pingTimeout: Duration = 10.seconds
  ): ZLayer[Any, Throwable, ZIntegration[RedisClient]] = redissonLive(config, codec) >>>
    clientLive(serviceName, pingTimeout)

}
