package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.base.codec.RedisDecoder
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders
import org.redisson.api.{RMap, RMapCacheNative, RedissonClient}
import org.redisson.client.codec.StringCodec
import zio.macros.accessible
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

import scala.jdk.CollectionConverters.SetHasAsJava

@accessible
trait RedisHashOperations {

  def hDel(key: String, fields: String*): Task[Long]

  def hDel(key: String, fields: Iterable[String]): Task[Long]

  def hExists(key: String, field: Object): Task[Boolean]

  def hExpireXx(key: String, ttl: Duration, field: String): Task[Boolean]

  def hExpireXx(key: String, ttl: Duration, fields: Set[String]): Task[Int]

  def hExpireXx(key: String, ttl: Duration, fields: String*): Task[Int]

  def hExpireNx(key: String, ttl: Duration, field: String): Task[Boolean]

  def hExpireNx(key: String, ttl: Duration, fields: Set[String]): Task[Int]

  def hExpireNx(key: String, ttl: Duration, fields: String*): Task[Int]

  def hExpireGt(key: String, ttl: Duration, field: String): Task[Boolean]

  def hExpireGt(key: String, ttl: Duration, fields: Set[String]): Task[Int]

  def hExpireGt(key: String, ttl: Duration, fields: String*): Task[Int]

  def hExpireLt(key: String, ttl: Duration, field: String): Task[Boolean]

  def hExpireLt(key: String, ttl: Duration, fields: Set[String]): Task[Int]

  def hExpireLt(key: String, ttl: Duration, fields: String*): Task[Int]

}

trait RedisHashOperationsImpl extends RedisHashOperations {

  protected val redissonClient: RedissonClient

  private lazy val map: String => RMap[String, String] =
    redissonClient.getMap[String, String](_, StringCodec.INSTANCE)

  private lazy val mapCacheNative: String => RMapCacheNative[String, String] =
    redissonClient.getMapCacheNative[String, String](_, StringCodec.INSTANCE)

  override def hDel(key: String, fields: String*): Task[Long] = ZIO
    .fromCompletionStage(map(key).fastRemoveAsync(fields: _*))
    .map(_.longValue())

  override def hDel(key: String, fields: Iterable[String]): Task[Long] = ZIO
    .fromCompletionStage(map(key).fastRemoveAsync(fields.toSeq: _*))
    .map(_.longValue())

  override def hExists(key: String, field: Object): Task[Boolean] = ZIO
    .fromCompletionStage(map(key).containsKeyAsync(field))
    .map(_.booleanValue())

  override def hExpireXx(key: String, ttl: Duration, field: String): Task[Boolean] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntryAsync(field, ttl))
    .map(_.booleanValue())

  override def hExpireXx(key: String, ttl: Duration, fields: Set[String]): Task[Int] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntriesAsync(fields.asJava, ttl))
    .map(_.intValue())

  override def hExpireXx(key: String, ttl: Duration, fields: String*): Task[Int] =
    hExpireXx(key, ttl, fields.toSet)

  override def hExpireNx(key: String, ttl: Duration, field: String): Task[Boolean] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntryIfNotSetAsync(field, ttl))
    .map(_.booleanValue())

  override def hExpireNx(key: String, ttl: Duration, fields: Set[String]): Task[Int] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntriesIfNotSetAsync(fields.asJava, ttl))
    .map(_.intValue())

  override def hExpireNx(key: String, ttl: Duration, fields: String*): Task[Int] =
    hExpireNx(key, ttl, fields.toSet)

  override def hExpireGt(key: String, ttl: Duration, field: String): Task[Boolean] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntryIfGreaterAsync(field, ttl))
    .map(_.booleanValue())

  override def hExpireGt(key: String, ttl: Duration, fields: Set[String]): Task[Int] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntriesIfGreaterAsync(fields.asJava, ttl))
    .map(_.intValue())

  override def hExpireGt(key: String, ttl: Duration, fields: String*): Task[Int] =
    hExpireGt(key, ttl, fields.toSet)

  override def hExpireLt(key: String, ttl: Duration, field: String): Task[Boolean] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntryIfLessAsync(field, ttl))
    .map(_.booleanValue())

  override def hExpireLt(key: String, ttl: Duration, fields: Set[String]): Task[Int] = ZIO
    .fromCompletionStage(mapCacheNative(key).expireEntriesIfLessAsync(fields.asJava, ttl))
    .map(_.intValue())

  override def hExpireLt(key: String, ttl: Duration, fields: String*): Task[Int] =
    hExpireLt(key, ttl, fields.toSet)

  override def hGet[T: RedisDecoder](key: String, ttl: Duration, field: String): Task[Option[T]] =
    ZIO.fromCompletionStage(map(key).getAsync(field)).flatMap(JavaDecoders.decodeNullableValue(_))

}

case class RedisHashOperationsLive(redissonClient: RedissonClient) extends RedisHashOperationsImpl

object RedisHashOperations {

  val live: URLayer[RedissonClient, RedisHashOperations] = ZLayer
    .fromZIO(ZIO.service[RedissonClient].map(RedisHashOperationsLive))

}
