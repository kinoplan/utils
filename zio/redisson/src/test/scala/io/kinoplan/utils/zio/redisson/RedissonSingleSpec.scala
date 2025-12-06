package io.kinoplan.utils.zio.redisson

import com.redis.testcontainers.RedisContainer
import org.redisson.client.codec.{IntegerCodec, StringCodec}
import org.redisson.config.{Config => RedissonConfig}
import zio._
import zio.test._

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.{ZIntegration, ZIntegrationCheck}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.config.{RedisCommonConfig, RedisSingleConfig}
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import io.kinoplan.utils.zio.redisson.module.RedissonSingle
import io.kinoplan.utils.zio.redisson.operations.generateKey

object RedissonSingleSpec extends ZIOSpecDefault with DefaultRedisCodecs {

  private val testServiceName = "redisson.single-test"

  private def singleConfig(redisContainer: RedisContainer) = Map(
    "host" -> redisContainer.getHost,
    "port" -> redisContainer.getFirstMappedPort.toString,
    "protocol" -> "redis://",
    "subscriptionConnectionMinimumIdleSize" -> "2",
    "subscriptionConnectionPoolSize" -> "100",
    "connectionPoolSize" -> "64",
    "dnsMonitoringInterval" -> "10000",
    "idleConnectionTimeout" -> "15000",
    "connectTimeout" -> "12000",
    "timeout" -> "5000",
    "retryAttempts" -> "5",
    "database" -> "1",
    "subscriptionsPerConnection" -> "10",
    "subscriptionTimeout" -> "8500",
    "clientName" -> "single-test",
    "pingConnectionInterval" -> "40000",
    "keepAlive" -> "true",
    "tcpNoDelay" -> "false"
  ).map { case (key, value) =>
    (s"redis.single.$key", value)
  }

  private def configLive = ZLayer
    .service[RedisContainer]
    .flatMap { redisContainerEnv =>
      val redisContainer = redisContainerEnv.get
      val configMap = commonConfig ++ singleConfig(redisContainer)

      Runtime.setConfigProvider(ConfigProvider.fromMap(configMap))
    }

  def redisCommonConfigLive: ZLayer[RedisContainer, Config.Error, RedisCommonConfig] =
    configLive >>> RedisCommonConfig.live

  def redisSingleConfigLive: ZLayer[RedisContainer, Config.Error, RedisSingleConfig] =
    configLive >>> RedisSingleConfig.live

  def redisLive[K, V](
    configurator: RedissonConfig => RedissonConfig,
    serviceName: String,
    pingTimeout: Duration
  )(implicit
    codec: RCodec[K, V]
  ): ZLayer[RedisContainer, Throwable, ZIntegration[RedisClient]] = configLive >>>
    RedissonSingle.live(configurator, serviceName, pingTimeout)

  def live[K, V](
    configurator: RedissonConfig => RedissonConfig = identity,
    serviceName: String = "redisson.single",
    pingTimeout: Duration = 10.seconds
  )(implicit
    codec: RCodec[K, V]
  ): ZLayer[RedisContainer, Throwable, Set[IntegrationCheck[Task]] with RedisClient] = {
    val redisIntegrationLive = redisLive(configurator, serviceName, pingTimeout)
    val redisClientLive = redisIntegrationLive.map(_.get.module)
    val integrationLive = ZIntegrationCheck.live(redisIntegrationLive.map(_.get.moduleCheck))

    redisClientLive ++ integrationLive
  }

  def toSpec(spec: TestSpec[RedisClient, Throwable, TestResult]): Spec[RedisClient, Throwable] =
    test(spec.label)(spec.result)

  override def spec: Spec[TestEnvironment with Scope, Throwable] = suite("RedissonSingle")(
    test("check config")(
      for {
        redis <- redisClient
        codec = StringCodec.INSTANCE
        redissonConfig = redis.redissonClient.getConfig.setCodec(codec)
        redisCommonConfig <- ZIO.service[RedisCommonConfig]
        redisSingleConfig <- ZIO.service[RedisSingleConfig]
        combinedRedisSingleConfig =
          redisSingleConfig.redissonConfig(redisCommonConfig.redissonConfig).setCodec(codec)
        redissonConfigYaml = redissonConfig.toYAML
        redisSingleConfigYaml = combinedRedisSingleConfig.toYAML
      } yield assertTrue(redissonConfigYaml == redisSingleConfigYaml)
    ).provideLayer(redisCommonConfigLive ++ redisSingleConfigLive ++ live()),
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
    test("check configurator")(
      for {
        redis <- redisClient
        redissonConfig = redis.redissonClient.getConfig
        case1 = redissonConfig.getNettyThreads == testConfigurator
      } yield assertTrue(case1)
    ).provideLayer(live(configurator = _.setNettyThreads(testConfigurator))),
    test("check RCodec with configurator")(
      for {
        redis <- redisClient
        redissonConfig = redis.redissonClient.getConfig
        case1 = testCodec.underlying.contains(redissonConfig.getCodec)
      } yield assertTrue(case1)
    ).provideLayer(live(_.setCodec(IntegerCodec.INSTANCE))(testCodec)),
    test("check serviceName")(
      for {
        check <- checkClient
        redisCheckO = check.headOption
        case1 = redisCheckO.exists(_.checkServiceName == testServiceName)
      } yield assertTrue(case1)
    ).provideLayer(live(serviceName = testServiceName)),
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
