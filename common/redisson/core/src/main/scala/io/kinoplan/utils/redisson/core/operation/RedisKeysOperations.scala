package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.RedissonClient
import org.redisson.api.options.KeysScanOptions

import io.kinoplan.utils.cross.collection.converters._

trait RedisKeysOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  private lazy val keys = redissonClient.getKeys

  def scan(pattern: String): Future[Iterable[String]] = Future {
    keys.getKeys(KeysScanOptions.defaults().pattern(pattern)).asScala
  }

  protected def del(keySet: Set[String]): Future[Long] = Future {
    keys.delete(keySet.toList: _*)
  }

  protected def delByPattern(pattern: String): Future[Long] = Future {
    keys.deleteByPattern(pattern)
  }

}
