package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{RMap, RedissonClient}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.JavaDecoders._

trait RedisHashmapOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  private lazy val map: String => RMap[String, String] =
    redissonClient.getMap[String, String](_, StringCodec.INSTANCE)

  protected def hSet[T: RedisEncoder](key: String, kv: (String, T)): Future[Boolean] = Future {
    import kv.{_1 => objKey, _2 => objValue}
    map(key).fastPut(objKey, RedisEncoder[T].encode(objValue))
  }

  protected def hGet[T: RedisDecoder](key: String, field: String): Future[Option[T]] = Future {
    map(key).get(field)
  }.flatMap(decodeNullableValue[T])

  protected def hGetAll[T: RedisDecoder](key: String): Future[Map[String, T]] = Future {
    map(key).readAllMap()
  }.flatMap(decodeMap[T])

}
