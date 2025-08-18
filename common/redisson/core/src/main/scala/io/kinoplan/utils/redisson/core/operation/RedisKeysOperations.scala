package io.kinoplan.utils.redisson.core.operation

import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.core.compat.crossFutureConverters.CompletionStageOps
import org.redisson.api.RedissonClient
import org.redisson.api.options.KeysScanOptions

import scala.concurrent.{ExecutionContext, Future, blocking}

trait RedisKeysOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  protected lazy val keys = redissonClient.getKeys

  def scan(pattern: String): Future[Iterable[String]] = Future {
    blocking {
      keys.getKeys(KeysScanOptions.defaults().pattern(pattern)).asScala
    }
  }

  protected def del(keySet: Set[String]): Future[Long] = keys
    .deleteAsync(keySet.toList: _*)
    .asScala
    .map(_.longValue())

  protected def delByPattern(pattern: String): Future[Long] = keys
    .deleteByPatternAsync(pattern)
    .asScala
    .map(_.longValue())

}
