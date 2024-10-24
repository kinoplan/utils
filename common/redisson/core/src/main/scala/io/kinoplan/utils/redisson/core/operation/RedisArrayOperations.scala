package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{RDeque, RList, RedissonClient}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.JavaDecoders._

trait RedisArrayOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  private lazy val deque: String => RDeque[String] =
    redissonClient.getDeque[String](_, StringCodec.INSTANCE)

  private lazy val list: String => RList[String] =
    redissonClient.getList[String](_, StringCodec.INSTANCE)

  protected def lPush[T: RedisEncoder](key: String, value: T): Future[Int] = Future {
    deque(key).addFirst(RedisEncoder[T].encode(value))
  }

  protected def rPush[T: RedisEncoder](key: String, value: T): Future[Int] = Future {
    deque(key).addLast(RedisEncoder[T].encode(value))
  }

  protected def lPop[T: RedisDecoder](key: String): Future[Option[T]] = Future {
    deque(key).pollFirst()
  }.flatMap(decodeNullableValue[T])

  protected def rPop[T: RedisDecoder](key: String): Future[Option[T]] = Future {
    deque(key).pollLast()
  }.flatMap(decodeNullableValue[T])

  protected def rPopLPush[T: RedisDecoder](key: String, destination: String): Future[Option[T]] =
    Future {
      deque(key).pollLastAndOfferFirstTo(destination)
    }.flatMap(decodeNullableValue[T])

  protected def lRange[T: RedisDecoder](key: String, start: Int, end: Int): Future[List[T]] =
    Future {
      list(key).range(start, end)
    }.flatMap(decodeArray[T])

  protected def lTrim(key: String, start: Int, end: Int): Future[Unit] = Future {
    list(key).trim(start, end)
  }

  protected def lLen(key: String): Future[Int] = Future {
    list(key).size()
  }

  protected def lRem(key: String, count: Int, element: String): Future[Boolean] = Future {
    list(key).remove(element, count)
  }

}
