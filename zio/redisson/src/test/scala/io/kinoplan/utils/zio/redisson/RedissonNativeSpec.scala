package io.kinoplan.utils.zio.redisson

import scala.io.Source

import com.redis.testcontainers.RedisContainer
import org.redisson.config.Config
import zio.{Duration, Scope, Task, ZIO, ZLayer, durationInt}
import zio.test._

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.{ZIntegration, ZIntegrationCheck}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.module.RedissonNative
import io.kinoplan.utils.zio.redisson.operations.generateKey

object RedissonNativeSpec extends ZIOSpecDefault with DefaultRedisCodecs {

  private val testServiceName = "redisson.native-single-test"

  private def resourceYaml = Source.fromResource("single-config.yaml")

  def redisLive[K, V](serviceName: Option[String] = None, pingTimeout: Duration = 10.seconds)(
    implicit
    codec: RCodec[K, V]
  ): ZLayer[RedisContainer, Throwable, ZIntegration[RedisClient]] = ZLayer
    .fromZIO(ZIO.service[RedisContainer])
    .flatMap { redisContainerEnvironment =>
      val redisContainer = redisContainerEnvironment.get

      val config = Config.fromYAML(resourceYaml.reader())

      val host = redisContainer.getHost
      val port = redisContainer.getFirstMappedPort.toString
      val address = s"redis://$host:$port"

      config.useSingleServer().setAddress(address)

      RedissonNative.live(config, serviceName, pingTimeout)
    }

  def live[K, V](serviceName: Option[String] = None, pingTimeout: Duration = 10.seconds)(implicit
    codec: RCodec[K, V]
  ): ZLayer[RedisContainer, Throwable, Set[IntegrationCheck[Task]] with RedisClient] = {
    val redisIntegrationLive = redisLive(serviceName, pingTimeout)
    val redisClientLive = redisIntegrationLive.map(_.get.module)
    val integrationLive = ZIntegrationCheck.live(redisIntegrationLive.map(_.get.moduleCheck))

    redisClientLive ++ integrationLive
  }

  override def spec: Spec[TestEnvironment with Scope, Throwable] = suite("RedissonNative")(
    test("check config")(
      for {
        redis <- redisClient
        configYaml = resourceYaml.getLines().toList
        redissonConfigYaml = redis.redissonClient.getConfig.toYAML.split("\n").toList
        case1 = configYaml.diff(redissonConfigYaml)
        case2 = configYaml.diff(redissonConfigYaml).filterNot(_.startsWith("  address:"))
      } yield assertTrue(case1.size == 1, case1.exists(_.contains("address")), case2.isEmpty)
    ).provideLayer(live()),
    test("check RCodec")(
      for {
        redis <- redisClient
        redissonConfig = redis.redissonClient.getConfig
        key = generateKey
        case1 = testCodec.underlying.contains(redissonConfig.getCodec)
        _ <- redis.set(key, "Hello")
        case2 <- redis.get(key).as[String]
      } yield assertTrue(case1, case2.contains("Hello"))
    ).provideLayer(live()(testCodec)),
    test("check serviceName")(
      for {
        check <- checkClient
        redisCheckO = check.headOption
        case1 = redisCheckO.exists(_.checkServiceName == testServiceName)
      } yield assertTrue(case1)
    ).provideLayer(live(serviceName = Some(testServiceName))),
    test("check pingTimeout")(
      for {
        check <- checkClient
        redisCheckO = check.headOption
        case1 <- ZIO.fromOption(redisCheckO).forEachZIO(_.checkAvailability).map(_.getOrElse(false))
      } yield assertTrue(case1)
    ).provideLayer(live()),
    test("check pingTimeout zero")(
      for {
        check <- checkClient
        redisCheckO = check.headOption
        case1 <- ZIO.fromOption(redisCheckO).forEachZIO(_.checkAvailability).map(_.getOrElse(false))
      } yield assertTrue(!case1)
    ).provideLayer(live(pingTimeout = testPingTimeout))
  ).provideLayerShared(redisSingleContainerLive) @@ testAspect(10.seconds) // @@ TestAspect.ignore

}
