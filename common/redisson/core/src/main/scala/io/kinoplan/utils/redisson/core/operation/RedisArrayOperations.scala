package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{RDeque, RList, RedissonClient}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.JavaDecoders.{decodeArray, decodeNullableValue}
import io.kinoplan.utils.redisson.core.compat.crossFutureConverters.CompletionStageOps

trait RedisArrayOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  protected lazy val deque: String => RDeque[String] =
    redissonClient.getDeque[String](_, StringCodec.INSTANCE)

  protected lazy val list: String => RList[String] =
    redissonClient.getList[String](_, StringCodec.INSTANCE)

  protected def lPush[T: RedisEncoder](key: String, value: T): Future[Int] = deque(key)
    .addFirstAsync(List(RedisEncoder[T].encode(value)): _*)
    .asScala
    .map(_.intValue())

  protected def rPush[T: RedisEncoder](key: String, value: T): Future[Int] = deque(key)
    .addLastAsync(List(RedisEncoder[T].encode(value)): _*)
    .asScala
    .map(_.intValue())

  protected def lPop[T: RedisDecoder](key: String): Future[Option[T]] = deque(key)
    .pollFirstAsync()
    .asScala
    .flatMap(decodeNullableValue[T])

  protected def rPop[T: RedisDecoder](key: String): Future[Option[T]] = deque(key)
    .pollLastAsync()
    .asScala
    .flatMap(decodeNullableValue[T])

  protected def rPopLPush[T: RedisDecoder](key: String, destination: String): Future[Option[T]] =
    deque(key).pollLastAndOfferFirstToAsync(destination).asScala.flatMap(decodeNullableValue[T])

  protected def lRange[T: RedisDecoder](key: String, start: Int, end: Int): Future[List[T]] =
    list(key).rangeAsync(start, end).asScala.flatMap(decodeArray[T])

  protected def lTrim(key: String, start: Int, end: Int): Future[Unit] = list(key)
    .trimAsync(start, end)
    .asScala
    .map(_ => ())

  protected def lLen(key: String): Future[Int] = list(key).sizeAsync().asScala.map(_.intValue())

  protected def lRem(key: String, count: Int, element: String): Future[Boolean] = list(key)
    .removeAsync(element, count)
    .asScala
    .map(_.booleanValue())

}
