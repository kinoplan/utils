package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.RedissonClient
import org.redisson.api.redisnode.RedisNodes
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

import java.util.concurrent.TimeUnit

trait RedisBaseOperations {

  def pingCluster(): Task[Boolean]

  def pingCluster(timeout: Duration): Task[Boolean]

  def pingMasterSlave(): Task[Boolean]

  def pingMasterSlave(timeout: Duration): Task[Boolean]

  def pingSentinelMasterSlave(): Task[Boolean]

  def pingSentinelMasterSlave(timeout: Duration): Task[Boolean]

  def pingSingle(): Task[Boolean]

  def pingSingle(timeout: Duration): Task[Boolean]

}

trait RedisBaseOperationsImpl extends RedisBaseOperations {
  protected val redissonClient: RedissonClient

  private lazy val clusterNode = redissonClient.getRedisNodes(RedisNodes.CLUSTER)

  private lazy val masterSlaveNode = redissonClient.getRedisNodes(RedisNodes.MASTER_SLAVE)

  private lazy val sentinelMasterSlaveNodes =
    redissonClient.getRedisNodes(RedisNodes.SENTINEL_MASTER_SLAVE)

  private lazy val singleNode = redissonClient.getRedisNodes(RedisNodes.SINGLE)

  override def pingCluster(): Task[Boolean] = ZIO.attemptBlocking(clusterNode.pingAll())

  override def pingCluster(timeout: Duration): Task[Boolean] =
    ZIO.attemptBlocking(clusterNode.pingAll(timeout.getSeconds, TimeUnit.SECONDS))

  override def pingMasterSlave(): Task[Boolean] = ZIO.attemptBlocking(masterSlaveNode.pingAll())

  override def pingMasterSlave(timeout: Duration): Task[Boolean] =
    ZIO.attemptBlocking(masterSlaveNode.pingAll(timeout.getSeconds, TimeUnit.SECONDS))

  override def pingSentinelMasterSlave(): Task[Boolean] =
    ZIO.attemptBlocking(sentinelMasterSlaveNodes.pingAll())

  override def pingSentinelMasterSlave(timeout: Duration): Task[Boolean] =
    ZIO.attemptBlocking(sentinelMasterSlaveNodes.pingAll(timeout.getSeconds, TimeUnit.SECONDS))

  override def pingSingle(): Task[Boolean] = ZIO.attemptBlocking(singleNode.pingAll())

  override def pingSingle(timeout: Duration): Task[Boolean] =
    ZIO.attemptBlocking(singleNode.pingAll(timeout.getSeconds, TimeUnit.SECONDS))

}

case class RedisBaseOperationsLive(redissonClient: RedissonClient) extends RedisBaseOperationsImpl

object RedisBaseOperations {

  val live: URLayer[RedissonClient, RedisBaseOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisBaseOperationsLive))

}
