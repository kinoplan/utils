package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{RMap, RedissonClient}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.JavaDecoders._
import io.kinoplan.utils.redisson.core.compat.crossFutureConverters.CompletionStageOps

trait RedisHashmapOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  protected lazy val map: String => RMap[String, String] =
    redissonClient.getMap[String, String](_, StringCodec.INSTANCE)

  protected def hSet[T: RedisEncoder](key: String, kv: (String, T)): Future[Boolean] = {
    import kv.{_1 => objKey, _2 => objValue}
    map(key).fastPutAsync(objKey, RedisEncoder[T].encode(objValue)).asScala.map(_.booleanValue())
  }

  protected def hGet[T: RedisDecoder](key: String, field: String): Future[Option[T]] = map(key)
    .getAsync(field)
    .asScala
    .flatMap(decodeNullableValue[T])

  protected def hGetAll[T: RedisDecoder](key: String): Future[Map[String, T]] = map(key)
    .readAllMapAsync()
    .asScala
    .flatMap(decodeMap[T])

}
