package io.kinoplan.utils.zio.redisson.helpers

import scala.jdk.CollectionConverters._

import org.redisson.api.NatMapper
import org.testcontainers.containers.{GenericContainer, Network}
import org.testcontainers.utility.DockerImageName
import zio.{Scope, UIO, ZIO, ZLayer}

case class RedisSentinelContainers(
  masterName: String,
  masterContainer: GenericContainer[Nothing],
  slaveContainer: GenericContainer[Nothing],
  sentinelContainer: GenericContainer[Nothing]
) {

  val host: String = sentinelContainer.getHost
  val port: String = sentinelContainer.getFirstMappedPort.toString

  val nodes: Seq[GenericContainer[Nothing]] = Seq(masterContainer, slaveContainer, sentinelContainer)

  val natMapper: NatMapper = SentinelNatMapper.make(
    RedisSentinelContainers.REDIS_MASTER_PORT,
    nodes.map(_.asInstanceOf[GenericContainer[_]]).asJava
  )

  def stop(): UIO[Unit] = for {
    _ <- ZIO.succeed(masterContainer.stop())
    _ <- ZIO.succeed(slaveContainer.stop())
    _ <- ZIO.succeed(sentinelContainer.stop())
  } yield ()

}

object RedisSentinelContainers {
  private val redisName = "bitnami/redis:latest"
  private val redisSentinelName = "bitnami/redis-sentinel:latest"

  private val REDIS_MASTER_PORT = 6379
  private val REDIS_SENTINEL_PORT = 26379

  val REDIS_MASTER_HOST = "redis-master"
  val REDIS_SLAVE_HOST = "redis-slave"
  val REDIS_SENTINEL_HOST = "redis-sentinel"
  val REDIS_SENTINEL_MASTER_HOST: String = REDIS_MASTER_HOST
  val REDIS_SENTINEL_MASTER_PORT: String = REDIS_MASTER_PORT.toString

  val live: ZLayer[Any with Scope, Throwable, RedisSentinelContainers] = ZLayer.fromZIO(
    ZIO.acquireRelease(
      ZIO.attempt {
        val network = Network.newNetwork()
        val masterName = "test-master"

        val masterContainer = new GenericContainer(DockerImageName.parse(redisName))
        masterContainer.withExposedPorts(REDIS_MASTER_PORT)
        masterContainer.withNetwork(network)
        masterContainer.withNetworkAliases(REDIS_MASTER_HOST)
        masterContainer.withEnv("REDIS_REPLICATION_MODE", "master")
        masterContainer.withEnv("ALLOW_EMPTY_PASSWORD", "yes")

        masterContainer.start()

        val slaveContainer = new GenericContainer(DockerImageName.parse(redisName))
        slaveContainer.withExposedPorts(REDIS_MASTER_PORT)
        slaveContainer.withNetwork(network)
        slaveContainer.withNetworkAliases(REDIS_SLAVE_HOST)
        slaveContainer.withEnv("REDIS_REPLICATION_MODE", "slave")
        slaveContainer.withEnv("REDIS_MASTER_HOST", REDIS_MASTER_HOST)
        slaveContainer.withEnv("ALLOW_EMPTY_PASSWORD", "yes")

        slaveContainer.start()

        Thread.sleep(5000) // hack to fix slave is down problem

        val sentinelContainer = new GenericContainer(DockerImageName.parse(redisSentinelName))
        sentinelContainer.withExposedPorts(REDIS_SENTINEL_PORT)
        sentinelContainer.withNetwork(network)
        sentinelContainer.withNetworkAliases(REDIS_SENTINEL_HOST)
        sentinelContainer.withEnv("REDIS_SENTINEL_DOWN_AFTER_MILLISECONDS", "5000")
        sentinelContainer.withEnv("REDIS_SENTINEL_FAILOVER_TIMEOUT", "10000")
        sentinelContainer.withEnv("REDIS_MASTER_HOST", REDIS_SENTINEL_MASTER_HOST)
        sentinelContainer.withEnv("REDIS_MASTER_PORT_NUMBER", REDIS_SENTINEL_MASTER_PORT)
        sentinelContainer.withEnv("REDIS_MASTER_SET", masterName)
        sentinelContainer.withEnv("REDIS_SENTINEL_QUORUM", "2")

        sentinelContainer.start()

        RedisSentinelContainers(masterName, masterContainer, slaveContainer, sentinelContainer)
      }
    )(_.stop())
  )

}
