package io.kinoplan.utils.zio.redisson

import com.redis.testcontainers.RedisContainer
import io.kinoplan.utils.zio.redisson.module.RedissonSingle
import io.kinoplan.utils.zio.redisson.operations.RedisStringOperationsSpec
import org.testcontainers.utility.DockerImageName
import zio._
import zio.test._

object RedisClientSpec extends ZIOSpecDefault {

  private def redisContainerLayer = ZLayer.fromZIO(
    ZIO.acquireRelease(
      ZIO.attempt {
        val container = new RedisContainer(DockerImageName.parse("redis:7"))

        container.start()

        container
      }
    )(container => ZIO.succeed(container.stop()))
  )

  private def configProvider(redisContainer: RedisContainer) = ConfigProvider.fromMap(
    Map(
      "redis.single.host" -> redisContainer.getHost,
      "redis.single.port" -> redisContainer.getFirstMappedPort.toString
    )
  )

  private def configLayer = ZLayer
    .service[RedisContainer]
    .flatMap(c => Runtime.setConfigProvider(configProvider(c.get)))

  def redisLayer: ZLayer[Any with Scope, Throwable, RedisClient] = redisContainerLayer >>>
    configLayer >>> RedissonSingle.live.map(_.get.module)

  def redisClient: URIO[RedisClient, RedisClient] = ZIO.service[RedisClient]

  override def spec: Spec[TestEnvironment with Scope, Throwable] = suite("RedisClient")(
    suite("RedisStringOperations")(
      RedisStringOperationsSpec.tests.map(spec => test(spec.label)(spec.result))
    )
  ).provideLayerShared(redisLayer)

}
