package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.cross.collection.MapSyntax.syntaxMapOps
import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders
import org.redisson.api._
import org.redisson.client.codec.StringCodec
import zio.stream.{ZSink, ZStream}
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

import java.nio.ByteBuffer
import scala.jdk.CollectionConverters.MapHasAsJava

trait RedisStringOperations {

  def append[T: RedisEncoder](key: String, value: T): Task[Unit]

  def decr(key: String): Task[Long]

  def decrBy(key: String, decrement: Long): Task[Long]

  def get[T: RedisDecoder](key: String): Task[Option[T]]

  def getDel[T: RedisDecoder](key: String): Task[Option[T]]

  def getEx[T: RedisDecoder](key: String, duration: Duration): Task[Option[T]]

  def getExPersist[T: RedisDecoder](key: String, duration: Duration): Task[Option[T]]

  def getRange[T: RedisDecoder](key: String, start: Int, end: Int): Task[Option[T]]

  def getSet[T: RedisEncoder: RedisDecoder](key: String, value: T): Task[Option[T]]

  def incr(key: String): Task[Long]

  def incrBy(key: String, increment: Long): Task[Long]

  def incrByFloat(key: String, increment: Double): Task[Double]

  def mGet[T: RedisDecoder](keys: Seq[String]): Task[Map[String, T]]

  def mSet[T: RedisEncoder](params: Map[String, T]): Task[Unit]

  def mSetNx[T: RedisEncoder](params: Map[String, T]): Task[Boolean]

  def pSetEx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Unit]

  def set[T: RedisEncoder](key: String, value: T): Task[Unit]

  def setKeepTtl[T: RedisEncoder](key: String, value: T): Task[Unit]

  def setEx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Unit]

  def setNx[T: RedisEncoder](key: String, value: T): Task[Boolean]

  def setNxEx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Boolean]

  def setXx[T: RedisEncoder](key: String, value: T): Task[Boolean]

  def setXxEx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Boolean]

  def setRange[T: RedisEncoder](key: String, offset: Long, value: T): Task[Long]

  def strLen(key: String): Task[Long]

}

trait RedisStringOperationsImpl extends RedisStringOperations {
  protected val redissonClient: RedissonClient

  private lazy val binaryStream: String => RBinaryStream = redissonClient.getBinaryStream

  private lazy val bucket: String => RBucket[String] =
    redissonClient.getBucket(_, StringCodec.INSTANCE)

  private lazy val buckets = redissonClient.getBuckets(StringCodec.INSTANCE)

  private lazy val atomicLong: String => RAtomicLong = redissonClient.getAtomicLong

  private lazy val atomicDouble: String => RAtomicDouble = redissonClient.getAtomicDouble

  override def append[T: RedisEncoder](key: String, value: T): Task[Unit] = ZStream
    .fromIterable(RedisEncoder[T].encode(value).getBytes)
    .run(ZSink.fromOutputStream(binaryStream(key).getOutputStream))
    .unit

  override def decr(key: String): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).decrementAndGetAsync())
    .map(_.longValue())

  override def decrBy(key: String, decrement: Long): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).addAndGetAsync(-decrement))
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

  override def getRange[T: RedisDecoder](key: String, start: Int, end: Int): Task[Option[T]] = for {
    is <- ZIO.attempt(binaryStream(key).getInputStream)
    size <- ZIO.attempt(is.available())
    buf <- ZIO.attempt(new Array[Byte](size))
    offset =
      if (start < -size) 0
      else if (size < start) size
      else if (start < 0) (size + start) % size
      else start
    len =
      if (end < -size) 0
      else if (end > size) size - offset
      else if (end < 0) size + end - offset + 1
      else end - offset + 1
    _ <- ZIO.attempt(is.skip(offset.toLong))
    _ <- ZIO.attempt(is.read(buf, offset, len))
    value <- ZIO.attempt(new String(buf, offset, len))
    result <- JavaDecoders.decodeNullableValue(value)
  } yield result

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

  override def mGet[T: RedisDecoder](keys: Seq[String]): Task[Map[String, T]] = ZIO
    .fromCompletionStage(buckets.getAsync[String](keys: _*))
    .flatMap(JavaDecoders.decodeMapValue(_))

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

  override def setNx[T: RedisEncoder](key: String, value: T): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).setIfAbsentAsync(RedisEncoder[T].encode(value)))
    .map(_.booleanValue())

  override def setNxEx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Boolean] =
    ZIO
      .fromCompletionStage(bucket(key).setIfAbsentAsync(RedisEncoder[T].encode(value), duration))
      .map(_.booleanValue())

  override def setXx[T: RedisEncoder](key: String, value: T): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).setIfExistsAsync(RedisEncoder[T].encode(value)))
    .map(_.booleanValue())

  override def setXxEx[T: RedisEncoder](key: String, duration: Duration, value: T): Task[Boolean] =
    ZIO
      .fromCompletionStage(bucket(key).setIfExistsAsync(RedisEncoder[T].encode(value), duration))
      .map(_.booleanValue())

  override def setRange[T: RedisEncoder](key: String, offset: Long, value: T): Task[Long] = for {
    data <- ZIO.attempt(RedisEncoder[T].encode(value).getBytes)
    byteBuffer <- ZIO.attempt(ByteBuffer.wrap(data))
    channel = binaryStream(key).getChannel
    _ <- ZIO.attempt(channel.position(offset).write(byteBuffer))
    result <- ZIO.attempt(channel.size())
  } yield result

  override def strLen(key: String): Task[Long] = ZIO
    .fromCompletionStage(bucket(key).sizeAsync())
    .map(_.longValue())

}

case class RedisStringOperationsLive(redissonClient: RedissonClient)
    extends RedisStringOperationsImpl

object RedisStringOperations {

  val live: URLayer[RedissonClient, RedisStringOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisStringOperationsLive))

}
