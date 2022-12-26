package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{RSet, RedissonClient}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.redisson.core.JavaDecoders._
import io.kinoplan.utils.redisson.core.codec.{RedisDecoder, RedisEncoder}

trait RedisSetOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  private lazy val set: String => RSet[String] =
    redissonClient.getSet[String](_, StringCodec.INSTANCE)

  protected def sAdd[T: RedisEncoder](key: String, value: T): Future[Boolean] = Future {
    set(key).add(RedisEncoder[T].encode(value))
  }

  protected def sMembers[T: RedisDecoder](key: String): Future[Set[T]] = Future {
    set(key).readAll()
  }.flatMap(decodeSet[T])

  protected def sLen(key: String): Future[Int] = Future {
    set(key).size()
  }

  protected def sRem[T: RedisEncoder](key: String, value: T): Future[Boolean] = Future {
    set(key).remove(RedisEncoder[T].encode(value))
  }

}
