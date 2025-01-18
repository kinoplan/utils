package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api._
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.JavaDecoders._
import io.kinoplan.utils.redisson.core.compat.crossFutureConverters.CompletionStageOps

trait RedisScoredSetOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  protected lazy val scoredSet: String => RScoredSortedSet[String] =
    redissonClient.getScoredSortedSet[String](_, StringCodec.INSTANCE)

  protected def zAdd[T: RedisEncoder](key: String, score: Double, value: T): Future[Boolean] =
    scoredSet(key).addAsync(score, RedisEncoder[T].encode(value)).asScala.map(_.booleanValue())

  protected def zRange[T: RedisDecoder](
    key: String,
    startIndex: Int,
    endIndex: Int
  ): Future[Set[T]] = scoredSet(key)
    .valueRangeAsync(startIndex, endIndex)
    .asScala
    .flatMap(decodeSet[T])

  protected def zRangeByScore[T: RedisDecoder](
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean
  ): Future[Set[T]] = scoredSet(key)
    .valueRangeAsync(fromScore, fromInc, toScore, toInc)
    .asScala
    .flatMap(decodeSet[T])

  protected def zRevRange[T: RedisDecoder](
    key: String,
    startIndex: Int,
    endIndex: Int
  ): Future[Set[T]] = scoredSet(key)
    .valueRangeReversedAsync(startIndex, endIndex)
    .asScala
    .flatMap(decodeSet[T])

  protected def zRevRangeByScore[T: RedisDecoder](
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean
  ): Future[Set[T]] = scoredSet(key)
    .valueRangeReversedAsync(fromScore, fromInc, toScore, toInc)
    .asScala
    .flatMap(decodeSet[T])

  protected def zRem[T: RedisEncoder](key: String, value: T): Future[Boolean] = scoredSet(key)
    .removeAsync(RedisEncoder[T].encode(value))
    .asScala
    .map(_.booleanValue())

  protected def zRemRangeByRank(key: String, startIndex: Int, endIndex: Int): Future[Int] =
    scoredSet(key).removeRangeByRankAsync(startIndex, endIndex).asScala.map(_.intValue())

  protected def zRemRangeByScore(
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean
  ): Future[Int] = scoredSet(key)
    .removeRangeByScoreAsync(fromScore, fromInc, toScore, toInc)
    .asScala
    .map(_.intValue())

  protected def zCount(
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean
  ): Future[Int] = scoredSet(key)
    .countAsync(fromScore, fromInc, toScore, toInc)
    .asScala
    .map(_.intValue())

}
