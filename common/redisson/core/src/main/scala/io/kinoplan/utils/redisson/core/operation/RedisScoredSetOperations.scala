package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api._
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.JavaDecoders._

trait RedisScoredSetOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  private lazy val scoredSet: String => RScoredSortedSet[String] =
    redissonClient.getScoredSortedSet[String](_, StringCodec.INSTANCE)

  protected def zAdd[T: RedisEncoder](key: String, score: Double, value: T): Future[Boolean] =
    Future {
      scoredSet(key).add(score, RedisEncoder[T].encode(value))
    }

  protected def zRange[T: RedisDecoder](key: String, startIndex: Int, endIndex: Int): Future[Set[T]] =
    Future {
      scoredSet(key).valueRange(startIndex, endIndex)
    }.flatMap(decodeSet[T])

  protected def zRangeByScore[T: RedisDecoder](
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean
  ): Future[Set[T]] = Future {
    scoredSet(key).valueRange(fromScore, fromInc, toScore, toInc)
  }.flatMap(decodeSet[T])

  protected def zRevRange[T: RedisDecoder](
    key: String,
    startIndex: Int,
    endIndex: Int
  ): Future[Set[T]] = Future {
    scoredSet(key).valueRangeReversed(startIndex, endIndex)
  }.flatMap(decodeSet[T])

  protected def zRevRangeByScore[T: RedisDecoder](
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean
  ): Future[Set[T]] = Future {
    scoredSet(key).valueRangeReversed(fromScore, fromInc, toScore, toInc)
  }.flatMap(decodeSet[T])

  protected def zRem[T: RedisEncoder](key: String, value: T): Future[Boolean] = Future {
    scoredSet(key).remove(RedisEncoder[T].encode(value))
  }

  protected def zRemRangeByRank(key: String, startIndex: Int, endIndex: Int): Future[Int] = Future {
    scoredSet(key).removeRangeByRank(startIndex, endIndex)
  }

  protected def zRemRangeByScore(
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean
  ): Future[Int] = Future {
    scoredSet(key).removeRangeByScore(fromScore, fromInc, toScore, toInc)
  }

  protected def zCount(
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean
  ): Future[Int] = Future {
    scoredSet(key).count(fromScore, fromInc, toScore, toInc)
  }

}
