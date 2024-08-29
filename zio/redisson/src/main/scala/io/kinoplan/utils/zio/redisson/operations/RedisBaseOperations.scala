package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.RedissonClient
import org.redisson.api.redisnode.RedisNodes
import zio.{Duration, Task, URLayer, ZIO, ZLayer}
import zio.macros.accessible

import java.util.concurrent.TimeUnit

@accessible
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

  private lazy val sentinelMasterSlaveNodes = redissonClient
    .getRedisNodes(RedisNodes.SENTINEL_MASTER_SLAVE)

  private lazy val singleNode = redissonClient.getRedisNodes(RedisNodes.SINGLE)

  override def pingCluster(): Task[Boolean] = ZIO.attempt(clusterNode.pingAll())

  override def pingCluster(timeout: Duration): Task[Boolean] = ZIO
    .attempt(clusterNode.pingAll(timeout.getSeconds, TimeUnit.SECONDS))

  override def pingMasterSlave(): Task[Boolean] = ZIO.attempt(masterSlaveNode.pingAll())

  override def pingMasterSlave(timeout: Duration): Task[Boolean] = ZIO
    .attempt(masterSlaveNode.pingAll(timeout.getSeconds, TimeUnit.SECONDS))

  override def pingSentinelMasterSlave(): Task[Boolean] = ZIO
    .attempt(sentinelMasterSlaveNodes.pingAll())

  override def pingSentinelMasterSlave(timeout: Duration): Task[Boolean] = ZIO
    .attempt(sentinelMasterSlaveNodes.pingAll(timeout.getSeconds, TimeUnit.SECONDS))

  override def pingSingle(): Task[Boolean] = ZIO.attempt(singleNode.pingAll())

  override def pingSingle(timeout: Duration): Task[Boolean] = ZIO
    .attempt(singleNode.pingAll(timeout.getSeconds, TimeUnit.SECONDS))

}

case class RedisBaseOperationsLive(redissonClient: RedissonClient) extends RedisBaseOperationsImpl

object RedisBaseOperations {

  val live: URLayer[RedissonClient, RedisBaseOperations] = ZLayer
    .fromZIO(ZIO.service[RedissonClient].map(RedisBaseOperationsLive))

}
