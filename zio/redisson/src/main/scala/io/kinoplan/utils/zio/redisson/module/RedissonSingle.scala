package io.kinoplan.utils.zio.redisson.module

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.ZIntegration
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.config.RedisSingleConfig
import org.redisson.Redisson
import zio.{Task, ZIO, ZLayer, durationInt}

object RedissonSingle {

  private val redissonLive = RedisSingleConfig.live >>>
    ZLayer.fromZIO(
      for {
        config <- ZIO.service[RedisSingleConfig]
        client <- ZIO.attempt(Redisson.create(config.redissonConfig))
      } yield client
    )

  val live: ZLayer[Any, Throwable, ZIntegration[RedisClient]] = redissonLive >>>
    RedisClient
      .live
      .map { redisClient =>
        val integrationCheck = new IntegrationCheck[Task] {
          override val checkServiceName: String = "redisson.single"

          override def checkAvailability: Task[Boolean] = redisClient.get.pingSingle(10.seconds)
        }

        ZIntegration.environment(redisClient.get, Set(integrationCheck))
      }

}
