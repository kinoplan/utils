package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.MapSyntax.syntaxMapOps
import io.kinoplan.utils.redisson.base.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders
import org.redisson.api._
import org.redisson.client.codec.StringCodec
import zio.macros.accessible
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

import scala.jdk.CollectionConverters.MapHasAsJava

@accessible
trait RedisStringOperations {
  def decr(key: String): Task[Long]

  def decrBy(key: String, decrement: Long): Task[Long]

  def get[T: RedisDecoder](key: String): Task[Option[T]]

  def getDel[T: RedisDecoder](key: String): Task[Option[T]]

  def getEx[T: RedisDecoder](key: String, duration: Duration): Task[Option[T]]

  def getExPersist[T: RedisDecoder](key: String, duration: Duration): Task[Option[T]]

  def getSet[T: RedisEncoder: RedisDecoder](key: String, value: T): Task[Option[T]]

  def incr(key: String): Task[Long]

  def incrBy(key: String, increment: Long): Task[Long]

  def incrByFloat(key: String, increment: Double): Task[Double]

  def mGet[T: RedisDecoder](keys: String*): Task[Map[String, T]]

  def mGet[T: RedisDecoder](keys: Set[String]): Task[Map[String, T]]

  def mSet[T: RedisEncoder](params: Map[String, T]): Task[Unit]

  def mSetNx[T: RedisEncoder](params: Map[String, T]): Task[Boolean]

  def pSetEx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Unit]

  def set[T: RedisEncoder](key: String, value: T): Task[Unit]

  def setKeepTtl[T: RedisEncoder](key: String, value: T): Task[Unit]

  def setEx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Unit]

  def setNx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Boolean]

  def setExNx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Boolean]

  def strLen[T: RedisDecoder](key: String): Task[Long]
}

case class RedisStringOperationsLive(redissonClient: RedissonClient) extends RedisStringOperations {

  private lazy val bucket: String => RBucket[String] =
    redissonClient.getBucket(_, StringCodec.INSTANCE)

  private lazy val buckets = redissonClient.getBuckets(StringCodec.INSTANCE)

  private lazy val atomicLong: String => RAtomicLong = redissonClient.getAtomicLong

  private lazy val atomicDouble: String => RAtomicDouble = redissonClient.getAtomicDouble

  override def decr(key: String): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).decrementAndGetAsync())
    .map(_.longValue())

  override def decrBy(key: String, decrement: Long): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).addAndGetAsync(decrement))
    .map(_.longValue())

  override def get[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAsync)
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def getDel[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAndDeleteAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def getEx[T: RedisDecoder](key: String, duration: Duration): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAndExpireAsync(duration))
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def getExPersist[T: RedisDecoder](key: String, duration: Duration): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAndClearExpireAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def getSet[T: RedisEncoder: RedisDecoder](key: String, value: T): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAndSetAsync(RedisEncoder[T].encode(value)))
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def incr(key: String): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).incrementAndGetAsync())
    .map(_.longValue())

  override def incrBy(key: String, increment: Long): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).addAndGetAsync(increment))
    .map(_.longValue())

  override def incrByFloat(key: String, increment: Double): Task[Double] = ZIO
    .fromCompletionStage(atomicDouble(key).addAndGetAsync(increment))
    .map(_.doubleValue())

  override def mGet[T: RedisDecoder](keys: String*): Task[Map[String, T]] = ZIO
    .fromCompletionStage(buckets.getAsync[String]())
    .flatMap(JavaDecoders.decodeMapValue(_))

  override def mGet[T: RedisDecoder](keys: Set[String]): Task[Map[String, T]] = mGet(keys.toSeq: _*)

  override def mSet[T: RedisEncoder](params: Map[String, T]): Task[Unit] = ZIO
    .fromCompletionStage(buckets.setAsync(params.crossMapValues(RedisEncoder[T].encode).asJava))
    .unit

  override def mSetNx[T: RedisEncoder](params: Map[String, T]): Task[Boolean] = ZIO
    .fromCompletionStage(buckets.trySetAsync(params.crossMapValues(RedisEncoder[T].encode).asJava))
    .map(_.booleanValue())

  override def pSetEx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Unit] = ZIO
    .fromCompletionStage(bucket(key).setAsync(RedisEncoder[T].encode(value), duration))
    .unit

  override def set[T: RedisEncoder](key: String, value: T): Task[Unit] = ZIO
    .fromCompletionStage(bucket(key).setAsync(RedisEncoder[T].encode(value)))
    .unit

  override def setKeepTtl[T: RedisEncoder](key: String, value: T): Task[Unit] = ZIO
    .fromCompletionStage(bucket(key).setAndKeepTTLAsync(RedisEncoder[T].encode(value)))
    .unit

  override def setEx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Unit] = ZIO
    .fromCompletionStage(bucket(key).setAsync(RedisEncoder[T].encode(value), duration))
    .unit

  override def setNx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Boolean] =
    ZIO
      .fromCompletionStage(bucket(key).setIfAbsentAsync(RedisEncoder[T].encode(value), duration))
      .map(_.booleanValue())

  override def setExNx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Boolean] =
    ZIO
      .fromCompletionStage(bucket(key).setIfExistsAsync(RedisEncoder[T].encode(value), duration))
      .map(_.booleanValue())

  override def strLen[T: RedisDecoder](key: String): Task[Long] = ZIO
    .fromCompletionStage(bucket(key).sizeAsync())
    .map(_.longValue())

}

object RedisStringOperations {

  val live: URLayer[RedissonClient, RedisStringOperations] = ZLayer
    .fromZIO(ZIO.service[RedissonClient].map(RedisStringOperationsLive))

}
