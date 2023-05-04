package io.kinoplan.utils.zio.redisson.module

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.ZIntegration
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.config.RedisSentinelConfig
import org.redisson.Redisson
import zio.config.{ReadError, getConfig}
import zio.{Layer, Task, ZLayer, durationInt}

object RedissonSentinel {

  private val redissonLive = RedisSentinelConfig.live >>>
    ZLayer.fromZIO(
      for {
        config <- getConfig[RedisSentinelConfig]
        client = Redisson.create(config.redissonConfig)
      } yield client
    )

  val live: Layer[ReadError[String], ZIntegration[RedisClient]] = redissonLive >>>
    RedisClient
      .live
      .map { redisClient =>
        val integrationCheck = new IntegrationCheck[Task] {
          override val checkServiceName: String = "redisson.sentinel"

          override def checkAvailability: Task[Boolean] = redisClient
            .get
            .base
            .pingSentinelMasterSlave(10.seconds)
        }

        ZIntegration.environment(redisClient.get, Set(integrationCheck))
      }

}
