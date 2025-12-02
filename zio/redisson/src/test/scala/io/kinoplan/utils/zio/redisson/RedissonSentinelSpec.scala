package io.kinoplan.utils.zio.redisson

import org.redisson.client.codec.{IntegerCodec, StringCodec}
import org.redisson.config.{Config => RedissonConfig}
import zio._
import zio.test._

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.{ZIntegration, ZIntegrationCheck}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.config.{RedisCommonConfig, RedisSentinelConfig}
import io.kinoplan.utils.zio.redisson.helpers.RedisSentinelContainers
import io.kinoplan.utils.zio.redisson.module.RedissonSentinel
import io.kinoplan.utils.zio.redisson.operations.generateKey

object RedissonSentinelSpec extends ZIOSpecDefault with DefaultRedisCodecs {

  private val testServiceName = "redisson.sentinel-test"

  private def sentinelConfig(redisSentinelsContainers: RedisSentinelContainers) = Map(
    "host" -> redisSentinelsContainers.host,
    "port" -> redisSentinelsContainers.port,
    "masterName" -> redisSentinelsContainers.masterName,
    "checkSentinelsList" -> "false",
    "protocol" -> "redis://",
    "readMode" -> "SLAVE",
    "subscriptionMode" -> "SLAVE",
    "masterConnectionMinimumIdleSize" -> "2",
    "slaveConnectionMinimumIdleSize" -> "2",
    "dnsMonitoringInterval" -> "6000",
    "checkSlaveStatusWithSyncing" -> "false",
    "loadBalancer" -> "RandomLoadBalancer",
    "subscriptionConnectionMinimumIdleSize" -> "2",
    "subscriptionConnectionPoolSize" -> "60",
    "masterConnectionPoolSize" -> "128",
    "slaveConnectionPoolSize" -> "128",
    "idleConnectionTimeout" -> "11000",
    "connectTimeout" -> "12000",
    "timeout" -> "5000",
    "retryAttempts" -> "5",
    "failedSlaveReconnectionInterval" -> "4000",
    "failedSlaveNodeDetector" -> "13000",
    "database" -> "1",
    "subscriptionTimeout" -> "8000",
    "clientName" -> "sentinel-test",
    "pingConnectionInterval" -> "20000",
    "keepAlive" -> "true",
    "tcpNoDelay" -> "false"
  ).map { case (key, value) =>
    (s"redis.sentinel.$key", value)
  }

  private def configLive = ZLayer
    .service[RedisSentinelContainers]
    .flatMap { redisSentinelsContainersEnv =>
      val redisSentinelsContainers = redisSentinelsContainersEnv.get
      val configMap = commonConfig ++ sentinelConfig(redisSentinelsContainers)

      Runtime.setConfigProvider(ConfigProvider.fromMap(configMap))
    }

  def redisCommonConfigLive: ZLayer[RedisSentinelContainers, Config.Error, RedisCommonConfig] =
    configLive >>> RedisCommonConfig.live

  def redisSentinelConfigLive: ZLayer[RedisSentinelContainers, Config.Error, RedisSentinelConfig] =
    configLive >>> RedisSentinelConfig.live

  def redisLive[K, V](
    configurator: RedissonConfig => RedissonConfig,
    serviceName: String,
    pingTimeout: Duration
  )(implicit
    codec: RCodec[K, V]
  ): ZLayer[RedisSentinelContainers, Throwable, ZIntegration[RedisClient]] = configLive >>>
    RedissonSentinel.live(configurator, serviceName, pingTimeout)

  def live[K, V](
    configurator: RedissonConfig => RedissonConfig = identity,
    serviceName: String = "redisson.sentinel",
    pingTimeout: Duration = 10.seconds
  )(implicit
    codec: RCodec[K, V]
  ): ZLayer[RedisSentinelContainers, Throwable, Set[IntegrationCheck[Task]] with RedisClient] =
    ZLayer
      .fromZIO(ZIO.service[RedisSentinelContainers])
      .flatMap { redisSentinelContainersEnv =>
        val redisSentinelContainers = redisSentinelContainersEnv.get
        val configuratorWithNat: RedissonConfig => RedissonConfig = config => {
          val configuredConfig = configurator(config)

          // For fix Sentinel, Docker, NAT, and possible issues
          // https://redis.io/docs/latest/operate/oss_and_stack/management/sentinel/#sentinel-docker-nat-and-possible-issues
          configuredConfig.useSentinelServers().setNatMapper(redisSentinelContainers.natMapper)

          configuredConfig
        }
        val redisIntegrationLive = redisLive(configuratorWithNat, serviceName, pingTimeout)
        val redisClientLive = redisIntegrationLive.map(_.get.module)
        val integrationLive = ZIntegrationCheck.live(redisIntegrationLive.map(_.get.moduleCheck))

        redisClientLive ++ integrationLive
      }

  override def spec: Spec[TestEnvironment with Scope, Throwable] = suite("RedissonSentinel")(
    test("check config")(
      for {
        redis <- redisClient
        codec = StringCodec.INSTANCE
        redissonConfig = redis.redissonClient.getConfig.setCodec(codec)
        redisCommonConfig <- ZIO.service[RedisCommonConfig]
        redisSentinelConfig <- ZIO.service[RedisSentinelConfig]
        combinedRedisSingleConfig =
          redisSentinelConfig.redissonConfig(redisCommonConfig.redissonConfig).setCodec(codec)
        _ = combinedRedisSingleConfig
          .useSentinelServers()
          .setNatMapper(redissonConfig.useSentinelServers().getNatMapper)
        redissonConfigYaml = redissonConfig.toYAML
        redisSingleConfigYaml = combinedRedisSingleConfig.toYAML
      } yield assertTrue(redissonConfigYaml == redisSingleConfigYaml)
    ).provideLayer(redisCommonConfigLive ++ redisSentinelConfigLive ++ live()),
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
  ).provideLayerShared(RedisSentinelContainers.live) @@
    testAspect(10.seconds) // @@ TestAspect.ignore

}
