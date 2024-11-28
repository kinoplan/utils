package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{RSet, RedissonClient}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.JavaDecoders._
import io.kinoplan.utils.redisson.core.compat.crossFutureConverters.CompletionStageOps

trait RedisSetOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  protected lazy val set: String => RSet[String] =
    redissonClient.getSet[String](_, StringCodec.INSTANCE)

  protected def sAdd[T: RedisEncoder](key: String, value: T): Future[Boolean] = set(key)
    .addAsync(RedisEncoder[T].encode(value))
    .asScala
    .map(_.booleanValue())

  protected def sMembers[T: RedisDecoder](key: String): Future[Set[T]] = set(key)
    .readAllAsync()
    .asScala
    .flatMap(decodeSet[T])

  protected def sCard(key: String): Future[Int] = set(key).sizeAsync().asScala.map(_.intValue())

  @deprecated(message = "use sCard instead", since = "0.0.40")
  protected def sLen(key: String): Future[Int] = sCard(key)

  protected def sIsMember[T: RedisEncoder](key: String, value: T): Future[Boolean] = set(key)
    .containsAsync(RedisEncoder[T].encode(value))
    .asScala
    .map(_.booleanValue())

  protected def sRem[T: RedisEncoder](key: String, value: T): Future[Boolean] = set(key)
    .removeAsync(RedisEncoder[T].encode(value))
    .asScala
    .map(_.booleanValue())

}
