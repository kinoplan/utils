package io.kinoplan.utils.zio

import com.redis.testcontainers.RedisContainer
import org.testcontainers.utility.DockerImageName
import zio.{Duration, Scope, Task, URIO, ZIO, ZLayer}
import zio.test.{Spec, TestResult}

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.redisson.RedissonSentinelSpec.test
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.helpers.TestSpec

package object redisson {
  private val redisImageName = "redis:latest"

  val testCodec: RCodec[String, String] = RCodec.stringCodec
  val testConfigurator: Int = 128
  val testPingTimeout: Duration = Duration.Zero

  val commonConfig: Map[String, String] = Map(
    "lazyInitialization" -> "true",
    "nettyThreads" -> "64",
    "transportMode" -> "NIO",
    "threads" -> "32",
    "protocol" -> "RESP2",
    "lockWatchdogTimeout" -> "35000",
    "lockWatchdogBatchSize" -> "101",
    "checkLockSyncedSlaves" -> "false",
    "slavesSyncTimeout" -> "1001",
    "reliableTopicWatchdogTimeout" -> "600001",
    "useScriptCache" -> "false",
    "keepPubSubOrder" -> "false",
    "minCleanUpDelay" -> "7",
    "maxCleanUpDelay" -> "1801",
    "cleanUpKeysAmount" -> "102",
    "useThreadClassLoader" -> "false"
  ).map { case (key, value) =>
    (s"redis.$key", value)
  }

  val redisSingleContainerLive: ZLayer[Any with Scope, Throwable, RedisContainer] = ZLayer.fromZIO(
    ZIO.acquireRelease(
      ZIO.attempt {
        val container = new RedisContainer(DockerImageName.parse(redisImageName))

        container.start()

        container
      }
    )(container => ZIO.succeed(container.stop()))
  )

  def redisClient: URIO[RedisClient, RedisClient] = ZIO.service[RedisClient]

  def checkClient: URIO[Set[IntegrationCheck[Task]], Set[IntegrationCheck[Task]]] =
    ZIO.service[Set[IntegrationCheck[Task]]]

  def toSpec(spec: TestSpec[RedisClient, Throwable, TestResult]): Spec[RedisClient, Throwable] =
    test(spec.label)(spec.result)

}
