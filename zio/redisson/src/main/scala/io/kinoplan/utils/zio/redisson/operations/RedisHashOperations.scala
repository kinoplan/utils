package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.RedisDecoder
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders
import org.redisson.api.{RMap, RedissonClient}
import org.redisson.client.codec.StringCodec
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

trait RedisHashOperations {

  def hDel(key: String, fields: Seq[String]): Task[Long]

  def hExists(key: String, field: Object): Task[Boolean]

  def hGet[T: RedisDecoder](key: String, ttl: Duration, field: String): Task[Option[T]]

}

trait RedisHashOperationsImpl extends RedisHashOperations {

  protected val redissonClient: RedissonClient

  private lazy val map: String => RMap[String, String] =
    redissonClient.getMap[String, String](_, StringCodec.INSTANCE)

  override def hDel(key: String, fields: Seq[String]): Task[Long] = ZIO
    .fromCompletionStage(map(key).fastRemoveAsync(fields: _*))
    .map(_.longValue())

  override def hExists(key: String, field: Object): Task[Boolean] = ZIO
    .fromCompletionStage(map(key).containsKeyAsync(field))
    .map(_.booleanValue())

  override def hGet[T: RedisDecoder](key: String, ttl: Duration, field: String): Task[Option[T]] =
    ZIO.fromCompletionStage(map(key).getAsync(field)).flatMap(JavaDecoders.decodeNullableValue(_))

}

case class RedisHashOperationsLive(redissonClient: RedissonClient) extends RedisHashOperationsImpl

object RedisHashOperations {

  val live: URLayer[RedissonClient, RedisHashOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisHashOperationsLive))

}
