package io.kinoplan.utils.zio.redisson

import com.redis.testcontainers.RedisContainer
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import io.kinoplan.utils.zio.redisson.module.RedissonSingle
import io.kinoplan.utils.zio.redisson.operations._
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
    configLayer >>> RedissonSingle.live().map(_.get.module)

  def redisClient: URIO[RedisClient, RedisClient] = ZIO.service[RedisClient]

  def toSpec(spec: TestSpec[RedisClient, Throwable, TestResult]): Spec[RedisClient, Throwable] =
    test(spec.label)(spec.result)

  override def spec: Spec[TestEnvironment with Scope, Throwable] = suite("RedisClient")(
    suite("RedisBitmapOperations")(RedisBitmapOperationsSpec.specs.map(toSpec)),
    suite("RedisGenericOperations")(RedisGenericOperationsSpec.specs.map(toSpec)),
    suite("RedisGeoOperations")(RedisGeoOperationsSpec.specs.map(toSpec)),
    suite("RedisHashOperations")(RedisHashOperationsSpec.specs.map(toSpec)),
    suite("RedisHyperLogLogOperations")(RedisHyperLogLogOperationsSpec.specs.map(toSpec)),
    suite("RedisListOperations")(RedisListOperationsSpec.specs.map(toSpec)),
    suite("RedisSortedSetOperations")(RedisSortedSetOperationsSpec.specs.map(toSpec)),
    suite("RedisSetOperations")(RedisSetOperationsSpec.specs.map(toSpec)),
    suite("RedisStringOperations")(RedisStringOperationsSpec.specs.map(toSpec)),
    suite("RedisTopicOperations")(RedisTopicOperationsSpec.specs.map(toSpec)) @@
      TestAspect.withLiveClock
  ).provideLayerShared(redisLayer) @@ TestAspect.timeout(30.seconds)

}
