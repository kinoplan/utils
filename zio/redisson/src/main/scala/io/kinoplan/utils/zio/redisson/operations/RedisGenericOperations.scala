package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{RExpirableAsync, RType, RedissonClient}
import zio.stream.ZStream
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.IterableHasAsScala

trait RedisGenericOperations {
  def copy(key: String, host: String, port: Int, db: Int, timeout: Duration): Task[Unit]

  def del(key: String): Task[Boolean]

  def del(keys: Seq[String]): Task[Long]

  def exists(key: String): Task[Boolean]

  def dump(key: String): Task[Array[Byte]]

  def expire(key: String, duration: Duration): Task[Boolean]

  def expireTime(key: String, duration: Duration): Task[Option[Duration]]

  def keys(key: String, pattern: String): Task[Iterable[String]]

  def keys(key: String, pattern: String, count: Int): Task[Iterable[String]]

  def migrate(key: String, host: String, port: Int, db: Int, timeout: Duration): Task[Unit]

  def move(key: String, db: Int): Task[Boolean]

  def persist(key: String): Task[Boolean]

  def pExpire(key: String, duration: Duration): Task[Boolean]

  def pExpireTime(key: String, duration: Duration): Task[Option[Duration]]

  def pTtl(key: String): Task[Option[Duration]]

  def randomKey(key: String, newKey: String): Task[String]

  def rename(key: String, newKey: String): Task[Unit]

  def renameNx(key: String, newKey: String): Task[Boolean]

  def restore(key: String, state: Array[Byte]): Task[Unit]

  def restore(key: String, timeout: Duration, state: Array[Byte]): Task[Unit]

  def scan(): ZStream[Any, Throwable, String]

  def scan(count: Int): ZStream[Any, Throwable, String]

  def scan(pattern: String): ZStream[Any, Throwable, String]

  def scan(pattern: String, count: Int): ZStream[Any, Throwable, String]

  def touch(key: String): Task[Boolean]

  def ttl(key: String): Task[Option[Duration]]

  def getType(key: String): Task[RType]

  def unlink(key: String): Task[Boolean]
}

trait RedisGenericOperationsImpl extends RedisGenericOperations {
  protected val redissonClient: RedissonClient

  private def expirable(key: String): RExpirableAsync = redissonClient.getBucket(key)

  override def copy(key: String, host: String, port: Int, db: Int, timeout: Duration): Task[Unit] =
    ZIO.fromCompletionStage(expirable(key).copyAsync(host, port, db, timeout.toMillis)).unit

  override def del(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).deleteAsync())
    .map(Boolean.unbox)

  override def del(keys: Seq[String]): Task[Long] = ZIO
    .fromCompletionStage(redissonClient.getKeys.deleteAsync(keys: _*))
    .map(_.toLong)

  override def dump(key: String): Task[Array[Byte]] =
    ZIO.fromCompletionStage(expirable(key).dumpAsync())

  override def exists(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).isExistsAsync)
    .map(Boolean.unbox)

  override def expire(key: String, duration: Duration): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).expireAsync(duration))
    .map(Boolean.unbox)

  override def expireTime(key: String, duration: Duration): Task[Option[Duration]] =
    pExpireTime(key, duration)

  override def keys(key: String, pattern: String): Task[Iterable[String]] = ZIO
    .attempt(redissonClient.getKeys.getKeysByPattern(pattern))
    .map(_.asScala)

  override def keys(key: String, pattern: String, count: Int): Task[Iterable[String]] = ZIO
    .attempt(redissonClient.getKeys.getKeysByPattern(pattern, count))
    .map(_.asScala)

  override def migrate(
    key: String,
    host: String,
    port: Int,
    db: Int,
    timeout: Duration
  ): Task[Unit] = ZIO
    .fromCompletionStage(expirable(key).migrateAsync(host, port, db, timeout.toMillis))
    .unit

  override def move(key: String, db: Int): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).moveAsync(db))
    .map(Boolean.unbox)

  override def persist(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).clearExpireAsync())
    .map(Boolean.unbox)

  override def pExpire(key: String, duration: Duration): Task[Boolean] = expire(key, duration)

  override def pExpireTime(key: String, duration: Duration): Task[Option[Duration]] = ZIO
    .fromCompletionStage(expirable(key).getExpireTimeAsync)
    .map(_.toLong)
    .map(result =>
      if (result < 0) None
      else Some(Duration.fromMillis(result))
    )

  override def pTtl(key: String): Task[Option[Duration]] = ttl(key)

  override def randomKey(key: String, newKey: String): Task[String] =
    ZIO.fromCompletionStage(redissonClient.getKeys.randomKeyAsync())

  override def rename(key: String, newKey: String): Task[Unit] = ZIO
    .fromCompletionStage(expirable(key).renameAsync(newKey))
    .unit

  override def renameNx(key: String, newKey: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).renamenxAsync(newKey))
    .map(Boolean.unbox)

  override def restore(key: String, state: Array[Byte]): Task[Unit] = ZIO
    .fromCompletionStage(expirable(key).restoreAsync(state))
    .unit

  override def restore(key: String, timeout: Duration, state: Array[Byte]): Task[Unit] = ZIO
    .fromCompletionStage(expirable(key).restoreAsync(state, timeout.toMillis, TimeUnit.MILLISECONDS))
    .unit

  override def scan(): ZStream[Any, Throwable, String] =
    ZStream.fromJavaStream(redissonClient.getKeys.getKeysStream)

  override def scan(count: Int): ZStream[Any, Throwable, String] =
    ZStream.fromJavaStream(redissonClient.getKeys.getKeysStream(count))

  override def scan(pattern: String): ZStream[Any, Throwable, String] =
    ZStream.fromJavaStream(redissonClient.getKeys.getKeysStreamByPattern(pattern))

  override def scan(pattern: String, count: Int): ZStream[Any, Throwable, String] = ZStream
    .fromJavaStream(redissonClient.getKeys.getKeysStreamByPattern(pattern, count))

  override def touch(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).touchAsync())
    .map(Boolean.unbox)

  override def ttl(key: String): Task[Option[Duration]] = ZIO
    .fromCompletionStage(expirable(key).remainTimeToLiveAsync())
    .map(_.toLong)
    .map(result =>
      if (result < 0) None
      else Some(Duration.fromMillis(result))
    )

  override def getType(key: String): Task[RType] =
    ZIO.fromCompletionStage(redissonClient.getKeys.getTypeAsync(key))

  override def unlink(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(expirable(key).unlinkAsync())
    .map(Boolean.unbox)

}

case class RedisGenericOperationsLive(redissonClient: RedissonClient)
    extends RedisGenericOperationsImpl

object RedisGenericOperations {

  val live: URLayer[RedissonClient, RedisGenericOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisGenericOperationsLive))

}
