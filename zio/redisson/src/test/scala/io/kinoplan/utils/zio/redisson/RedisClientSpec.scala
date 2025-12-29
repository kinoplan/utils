package io.kinoplan.utils.zio.redisson

import com.redis.testcontainers.RedisContainer
import zio._
import zio.test._

import io.kinoplan.utils.zio.redisson.module.RedissonSingle
import io.kinoplan.utils.zio.redisson.operations._

object RedisClientSpec extends ZIOSpecDefault {

  private def configProvider(redisContainer: RedisContainer) = ConfigProvider.fromMap(
    Map(
      "redis.single.host" -> redisContainer.getHost,
      "redis.single.port" -> redisContainer.getFirstMappedPort.toString
    )
  )

  private def configLive = ZLayer
    .service[RedisContainer]
    .flatMap(c => Runtime.setConfigProvider(configProvider(c.get)))

  def redisLive: ZLayer[Any with Scope, Throwable, RedisClient] = redisSingleContainerLive >>>
    configLive >>> RedissonSingle.live().map(_.get.module)

  override def spec: Spec[TestEnvironment with Scope, Throwable] = suite("RedisClient")(
    suite("RedisBitmapOperations")(RedisBitmapOperationsSpec.specs.map(toSpec)),
    suite("RedisConnectionOperations")(RedisConnectionOperationsSpec.singleSpecs.map(toSpec)),
    suite("RedisGenericOperations")(RedisGenericOperationsSpec.specs.map(toSpec)),
    suite("RedisGeoOperations")(RedisGeoOperationsSpec.specs.map(toSpec)),
    suite("RedisHashOperations")(RedisHashOperationsSpec.specs.map(toSpec)),
    suite("RedisHyperLogLogOperations")(RedisHyperLogLogOperationsSpec.specs.map(toSpec)),
    suite("RedisListOperations")(RedisListOperationsSpec.specs.map(toSpec)),
    suite("RedisSetOperations")(RedisSetOperationsSpec.specs.map(toSpec)),
    suite("RedisSortedSetOperations")(RedisSortedSetOperationsSpec.specs.map(toSpec)),
    suite("RedisStreamOperations")(RedisStreamOperationsSpec.specs.map(toSpec)),
    suite("RedisStringOperations")(RedisStringOperationsSpec.specs.map(toSpec)),
    suite("RedisTopicOperations")(RedisTopicOperationsSpec.specs.map(toSpec)) @@
      TestAspect.withLiveClock
  ).provideLayerShared(redisLive) @@ redissonTestAspect(30.seconds) // @@ TestAspect.ignore

}
