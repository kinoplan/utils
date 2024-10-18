package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.RedissonClient

import io.kinoplan.utils.redisson.crossCollectionConverters._

trait RedisKeysOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  private lazy val keys = redissonClient.getKeys

  def scan(pattern: String): Future[Iterable[String]] = Future {
    keys.getKeysByPattern(pattern).asScala
  }

  protected def del(keySet: Set[String]): Future[Long] = Future {
    keys.delete(keySet.toList: _*)
  }

  protected def delByPattern(pattern: String): Future[Long] = Future {
    keys.deleteByPattern(pattern)
  }

}
