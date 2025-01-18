package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future, blocking}

import org.redisson.api.RedissonClient
import org.redisson.api.redisnode.RedisNodes

trait RedisServiceOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  private lazy val sentinelMasterSlaveNodes =
    redissonClient.getRedisNodes(RedisNodes.SENTINEL_MASTER_SLAVE)

  private lazy val singleNode = redissonClient.getRedisNodes(RedisNodes.SINGLE)

  protected def pingSentinelMasterSlave(): Future[Boolean] = Future {
    blocking {
      sentinelMasterSlaveNodes.pingAll()
    }
  }

  protected def pingSingleMaster(): Future[Boolean] = Future {
    blocking {
      singleNode.pingAll()
    }
  }

}
