package io.kinoplan.utils.zio.redisson.operations

import java.util.concurrent.TimeUnit

import org.redisson.api.RedissonClient
import org.redisson.api.redisnode.RedisNodes
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

/** Interface representing operations that can be performed on Redis connection management.
  */
trait RedisConnectionOperations {

  /** Ping the Redis cluster connection to check if it's alive.
    *
    * Performs a connectivity check for the cluster setup.
    *
    * @return
    *   true if the cluster responds within the default timeout; false otherwise.
    */
  def pingCluster(): Task[Boolean]

  /** Ping the Redis cluster connection with a specified timeout.
    *
    * Performs a connectivity check for the cluster setup within the given timeout.
    *
    * @param timeout
    *   The maximum duration to wait for a response.
    * @return
    *   true if the cluster responds within the specified timeout; false otherwise.
    */
  def pingCluster(timeout: Duration): Task[Boolean]

  /** Ping the master-slave Redis setup to ensure connectivity.
    *
    * Checks if the master-slave architecture is responding.
    *
    * @return
    *   true if the master-slave responds within the default timeout; false otherwise.
    */
  def pingMasterSlave(): Task[Boolean]

  /** Ping the master-slave Redis setup with a specified timeout.
    *
    * Checks if the master-slave architecture is responding within the given timeout.
    *
    * @param timeout
    *   The maximum duration to wait for a response.
    * @return
    *   true if the master-slave responds within the specified timeout; false otherwise.
    */
  def pingMasterSlave(timeout: Duration): Task[Boolean]

  /** Ping the Sentinel master-slave Redis setup to check connectivity.
    *
    * Ensures that the Sentinel-managed master-slave setup is reachable.
    *
    * @return
    *   true if the Sentinel master-slave responds within the default timeout; false otherwise.
    */
  def pingSentinelMasterSlave(): Task[Boolean]

  /** Ping the Sentinel master-slave Redis setup with a specified timeout.
    *
    * Ensures that the Sentinel-managed master-slave setup is reachable within the given timeout.
    *
    * @param timeout
    *   The maximum duration to wait for a response.
    * @return
    *   true if the Sentinel master-slave responds within the specified timeout; false otherwise.
    */
  def pingSentinelMasterSlave(timeout: Duration): Task[Boolean]

  /** Ping the single-node Redis setup to verify connectivity.
    *
    * Checks if a standalone Redis server is up and reachable.
    *
    * @return
    *   true if the single node responds within the default timeout; false otherwise.
    */
  def pingSingle(): Task[Boolean]

  /** Ping the single-node Redis setup with a specified timeout.
    *
    * Checks if a standalone Redis server is up and reachable within the given timeout.
    *
    * @param timeout
    *   The maximum duration to wait for a response.
    * @return
    *   true if the single node responds within the specified timeout; false otherwise.
    */
  def pingSingle(timeout: Duration): Task[Boolean]

}

trait RedisConnectionOperationsImpl extends RedisConnectionOperations {
  protected val redissonClient: RedissonClient

  private lazy val clusterNode = redissonClient.getRedisNodes(RedisNodes.CLUSTER)

  private lazy val masterSlaveNode = redissonClient.getRedisNodes(RedisNodes.MASTER_SLAVE)

  private lazy val sentinelMasterSlaveNodes =
    redissonClient.getRedisNodes(RedisNodes.SENTINEL_MASTER_SLAVE)

  private lazy val singleNode = redissonClient.getRedisNodes(RedisNodes.SINGLE)

  override def pingCluster(): Task[Boolean] = ZIO.attemptBlocking(clusterNode.pingAll())

  override def pingCluster(timeout: Duration): Task[Boolean] =
    ZIO.attemptBlocking(clusterNode.pingAll(timeout.toMillis, TimeUnit.MILLISECONDS))

  override def pingMasterSlave(): Task[Boolean] = ZIO.attemptBlocking(masterSlaveNode.pingAll())

  override def pingMasterSlave(timeout: Duration): Task[Boolean] =
    ZIO.attemptBlocking(masterSlaveNode.pingAll(timeout.toMillis, TimeUnit.MILLISECONDS))

  override def pingSentinelMasterSlave(): Task[Boolean] =
    ZIO.attemptBlocking(sentinelMasterSlaveNodes.pingAll())

  override def pingSentinelMasterSlave(timeout: Duration): Task[Boolean] =
    ZIO.attemptBlocking(sentinelMasterSlaveNodes.pingAll(timeout.toMillis, TimeUnit.MILLISECONDS))

  override def pingSingle(): Task[Boolean] = ZIO.attemptBlocking(singleNode.pingAll())

  override def pingSingle(timeout: Duration): Task[Boolean] =
    ZIO.attemptBlocking(singleNode.pingAll(timeout.toMillis, TimeUnit.MILLISECONDS))

}

case class RedisConnectionsOperationsLive(redissonClient: RedissonClient)
    extends RedisConnectionOperationsImpl

object RedisConnectionOperations {

  val live: URLayer[RedissonClient, RedisConnectionOperations] =
    ZLayer.fromFunction(RedisConnectionsOperationsLive.apply _)

}
