package io.kinoplan.utils.zio.redisson.module

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.ZIntegration
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.config.{RedisCommonConfig, RedisSingleConfig}
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import zio.{Duration, Task, ZIO, ZLayer, durationInt}

object RedissonSingle {

  private def clientLive(serviceName: String, pingTimeout: Duration) = RedisClient
    .live
    .map { redisClient =>
      val client = redisClient.get

      val integrationCheck = new IntegrationCheck[Task] {
        override val checkServiceName: String = serviceName

        override def checkAvailability: Task[Boolean] = client.pingSingle(pingTimeout)
      }

      ZIntegration.environment(client, Set(integrationCheck))
    }

  def redissonLive(
    codec: RCodec = RCodec.dummyCodec,
    configurator: Config => Config = identity
  ): ZLayer[Any, Throwable, RedissonClient] = RedisCommonConfig.live ++ RedisSingleConfig.live >>>
    ZLayer.fromZIO(
      for {
        commonConfig <- ZIO.service[RedisCommonConfig]
        singleConfig <- ZIO.service[RedisSingleConfig]
        combinedConfig = singleConfig.redissonConfig(commonConfig.redissonConfig)
        redissonConfig = setupRedissonConfig(combinedConfig, codec, configurator)
        client <- ZIO.attempt(Redisson.create(redissonConfig))
      } yield client
    )

  def live(
    codec: RCodec = RCodec.dummyCodec,
    configurator: Config => Config = identity,
    serviceName: String = "redisson.single",
    pingTimeout: Duration = 10.seconds
  ): ZLayer[Any, Throwable, ZIntegration[RedisClient]] = redissonLive(codec, configurator) >>>
    clientLive(serviceName, pingTimeout)

}
