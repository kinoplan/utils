package io.kinoplan.utils.zio.redisson.module

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.ZIntegration
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.config.RedisSingleConfig
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import zio.{Duration, Task, ZIO, ZLayer, durationInt}

object RedissonSingle {

  def redissonLive(
    codec: RCodec = RCodec.dummyCodec,
    configurator: Config => Config = identity
  ): ZLayer[Any, Throwable, RedissonClient] = RedisSingleConfig.live >>>
    ZLayer.fromZIO(
      for {
        config <- ZIO.service[RedisSingleConfig]
        redissonConfig = setupRedissonConfig(config.redissonConfig, codec, configurator)
        client <- ZIO.attempt(Redisson.create(redissonConfig))
      } yield client
    )

  def live(
    codec: RCodec = RCodec.dummyCodec,
    configurator: Config => Config = identity,
    serviceName: String = "redisson.single",
    pingTimeout: Duration = 10.seconds
  ): ZLayer[Any, Throwable, ZIntegration[RedisClient]] = redissonLive(codec, configurator) >>>
    RedisClient
      .live
      .map { redisClient =>
        val integrationCheck = new IntegrationCheck[Task] {
          override val checkServiceName: String = serviceName

          override def checkAvailability: Task[Boolean] = redisClient.get.pingSingle(pingTimeout)
        }

        ZIntegration.environment(redisClient.get, Set(integrationCheck))
      }

}
