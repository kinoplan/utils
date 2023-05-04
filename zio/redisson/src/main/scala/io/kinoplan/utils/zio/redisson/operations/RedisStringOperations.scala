package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.cross.collection.MapSyntax.syntaxMapOps
import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders
import org.redisson.api._
import zio.stream.{ZSink, ZStream}
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

import java.nio.ByteBuffer
import scala.jdk.CollectionConverters.MapHasAsJava

/** Interface representing operations that can be performed on Redis string data.
  */
trait RedisStringOperations {

  /** Append a value to a key.
    *
    * Similar to the APPEND command.
    *
    * @param key
    *   The key to which the value will be appended.
    * @param value
    *   The value to append.
    * @tparam T
    *   Type of the value to append, requires RedisEncoder[T].
    */
  def append[T: RedisEncoder](key: String, value: T): Task[Unit]

  /** Decrement the number stored at key by one.
    *
    * Similar to the DECR command.
    *
    * @param key
    *   The key of the number to decrement.
    * @return
    *   The new value after decrementing by one.
    */
  def decr(key: String): Task[Long]

  /** Decrement the number stored at key by a specified amount.
    *
    * Similar to the DECRBY command.
    *
    * @param key
    *   The key of the number to decrement.
    * @param decrement
    *   The amount to decrement by.
    * @return
    *   The new value after the decrement.
    */
  def decrBy(key: String, decrement: Long): Task[Long]

  /** Get the value of a key.
    *
    * Similar to the GET command.
    *
    * @param key
    *   The key to retrieve the value from.
    * @tparam T
    *   Type of the value, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the value, or None if the key does not exist.
    */
  def get[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Get the value of a key and delete the key.
    *
    * Similar to the GETDEL command.
    *
    * @param key
    *   The key to retrieve and delete.
    * @tparam T
    *   Type of the value, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the value, or None if the key does not exist.
    */
  def getDel[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Get the value of a key and set its expiration.
    *
    * Similar to the GETEX command with EX|PX|EXAT|PXAT option.
    *
    * @param key
    *   The key to retrieve and set expiration for.
    * @param duration
    *   Expire time.
    * @tparam T
    *   Type of the value, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the value, or None if the key does not exist.
    */
  def getEx[T: RedisDecoder](key: String, duration: Duration)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Get the value of a key and make it persistent.
    *
    * Similar to the GETEX command with PERSIST option.
    *
    * @param key
    *   The key to retrieve.
    * @param duration
    *   Currently used in your description but typically should persist the key.
    * @tparam T
    *   Type of the value, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the value, or None if the key does not exist.
    */
  def getExPersist[T: RedisDecoder](key: String, duration: Duration)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Get a substring of the value stored at a key.
    *
    * Similar to the GETRANGE command.
    *
    * @param key
    *   The key of the string.
    * @param start
    *   The starting index of the substring.
    * @param end
    *   The ending index of the substring.
    * @tparam T
    *   Type of the value, requires RedisDecoder[T].
    * @return
    *   An Option containing the substring, or None if the key does not exist.
    */
  def getRange[T: RedisDecoder](key: String, start: Int, end: Int): Task[Option[T]]

  /** Set the string value of a key and return its old value.
    *
    * Similar to the GETSET command.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The new value.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T] and RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the old value, or None if the key did not exist.
    */
  def getSet[T: RedisEncoder: RedisDecoder](key: String, value: T)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Increment the number stored at key by one.
    *
    * Similar to the INCR command.
    *
    * @param key
    *   The key of the number to increment.
    * @return
    *   The new value after incrementing by one.
    */
  def incr(key: String): Task[Long]

  /** Increment the number stored at key by a specified amount.
    *
    * Similar to the INCRBY command.
    *
    * @param key
    *   The key of the number to increment.
    * @param increment
    *   The amount to increment by.
    * @return
    *   The new value after the increment.
    */
  def incrBy(key: String, increment: Long): Task[Long]

  /** Increment the float value stored at key by a specified amount.
    *
    * Similar to the INCRBYFLOAT command.
    *
    * @param key
    *   The key of the float to increment.
    * @param increment
    *   The amount to increment by.
    * @return
    *   The new value after the increment.
    */
  def incrByFloat(key: String, increment: Double): Task[Double]

  /** Get the values of multiple keys.
    *
    * Similar to the MGET command.
    *
    * @param keys
    *   The sequence of keys to retrieve values for.
    * @tparam T
    *   Type of the values, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A Map of keys to their corresponding values.
    */
  def mGet[T: RedisDecoder](keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Map[String, T]]

  /** Set multiple keys to multiple values.
    *
    * Similar to the MSET command.
    *
    * @param params
    *   A map of key-value pairs to set.
    * @tparam T
    *   Type of the values, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def mSet[T: RedisEncoder](params: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit]

  /** Set multiple keys to multiple values, only if none of the keys exist.
    *
    * Similar to the MSETNX command.
    *
    * @param params
    *   A map of key-value pairs to set.
    * @tparam T
    *   Type of the values, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def mSetNx[T: RedisEncoder](params: Map[String, T])(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Set the value of a key with expire time.
    *
    * Similar to the PSETEX command.
    *
    * @param key
    *   The key to set.
    * @param duration
    *   Expire time.
    * @param value
    *   The value to set.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def pSetEx[T: RedisEncoder](key: String, duration: Duration, value: T)(implicit
    codec: RCodec
  ): Task[Unit]

  /** Set the value of a key.
    *
    * Similar to the SET command.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The value to set.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def set[T: RedisEncoder](key: String, value: T)(implicit
    codec: RCodec
  ): Task[Unit]

  /** Set the value of a key and retain its current time-to-live (TTL).
    *
    * Similar to the SET command with KEEPTTL option.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The value to set.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def setKeepTtl[T: RedisEncoder](key: String, value: T)(implicit
    codec: RCodec
  ): Task[Unit]

  /** Set the value of a key with expire time.
    *
    * Similar to the SET command with EX option.
    *
    * @param key
    *   The key to set.
    * @param duration
    *   Expire time.
    * @param value
    *   The value to set.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def setEx[T: RedisEncoder](key: String, duration: Duration, value: T)(implicit
    codec: RCodec
  ): Task[Unit]

  /** Set the value of a key, only if the key does not exist.
    *
    * Similar to the SET command with NX option.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The value to set.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def setNx[T: RedisEncoder](key: String, value: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Set the value of a key with expire time, only if the key does not exist.
    *
    * Combines SET command with NX and EX options.
    *
    * @param key
    *   The key to set.
    * @param duration
    *   Expire time.
    * @param value
    *   The value to set.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def setNxEx[T: RedisEncoder](key: String, duration: Duration, value: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Set the value of a key only if the key exists.
    *
    * Similar to the SET command with XX option.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The value to set.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def setXx[T: RedisEncoder](key: String, value: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Set the value of a key with expire time, only if the key exists.
    *
    * Combines SET command with XX and EX options.
    *
    * @param key
    *   The key to set.
    * @param duration
    *   Expire time.
    * @param value
    *   The value to set.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def setXxEx[T: RedisEncoder](key: String, duration: Duration, value: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** OOverwrites part of the string stored at key, starting at the specified offset, for the entire
    * length of value.
    *
    * Similar to the SETRANGE command.
    *
    * @param key
    *   The key of the string to modify.
    * @param offset
    *   The offset to start overwriting from.
    * @param value
    *   The value to write.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @return
    *   The length of the string after modification.
    */
  def setRange[T: RedisEncoder](key: String, offset: Long, value: T): Task[Long]

  /** Get the length of the string value stored at a key.
    *
    * Similar to the STRLEN command.
    *
    * @param key
    *   The key of the string.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the string.
    */
  def strLen(key: String)(implicit
    codec: RCodec
  ): Task[Long]

}

trait RedisStringOperationsImpl extends RedisStringOperations {
  protected val redissonClient: RedissonClient

  private def binaryStream(key: String): RBinaryStream = redissonClient.getBinaryStream(key)

  private def bucket(key: String)(implicit
    codec: RCodec
  ): RBucket[String] = codec
    .underlying
    .map(redissonClient.getBucket[String](key, _))
    .getOrElse(redissonClient.getBucket[String](key))

  private def buckets(implicit
    codec: RCodec
  ): RBuckets = codec.underlying.map(redissonClient.getBuckets).getOrElse(redissonClient.getBuckets)

  private def atomicLong(key: String): RAtomicLong = redissonClient.getAtomicLong(key)

  private def atomicDouble(key: String): RAtomicDouble = redissonClient.getAtomicDouble(key)

  override def append[T: RedisEncoder](key: String, value: T): Task[Unit] = ZStream
    .fromIterable(RedisEncoder[T].encode(value).getBytes)
    .run(ZSink.fromOutputStream(binaryStream(key).getOutputStream))
    .unit

  override def decr(key: String): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).decrementAndGetAsync())
    .map(_.toLong)

  override def decrBy(key: String, decrement: Long): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).addAndGetAsync(-decrement))
    .map(_.toLong)

  override def get[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAsync)
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def getDel[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAndDeleteAsync())
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def getEx[T: RedisDecoder](key: String, duration: Duration)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAndExpireAsync(duration))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def getExPersist[T: RedisDecoder](key: String, duration: Duration)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAndClearExpireAsync())
    .flatMap(JavaDecoders.fromNullableValue(_))

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
    result <- JavaDecoders.fromNullableValue(value)
  } yield result

  override def getSet[T: RedisEncoder: RedisDecoder](key: String, value: T)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAndSetAsync(RedisEncoder[T].encode(value)))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def incr(key: String): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).incrementAndGetAsync())
    .map(_.toLong)

  override def incrBy(key: String, increment: Long): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).addAndGetAsync(increment))
    .map(_.toLong)

  override def incrByFloat(key: String, increment: Double): Task[Double] = ZIO
    .fromCompletionStage(atomicDouble(key).addAndGetAsync(increment))
    .map(_.doubleValue())

  override def mGet[T: RedisDecoder](keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Map[String, T]] = ZIO
    .fromCompletionStage(buckets.getAsync[String](keys: _*))
    .flatMap(JavaDecoders.fromMap(_))

  override def mSet[T: RedisEncoder](params: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(buckets.setAsync(params.crossMapValues(RedisEncoder[T].encode).asJava))
    .unit

  override def mSetNx[T: RedisEncoder](params: Map[String, T])(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(buckets.trySetAsync(params.crossMapValues(RedisEncoder[T].encode).asJava))
    .map(Boolean.unbox)

  override def pSetEx[T: RedisEncoder](key: String, duration: Duration, value: T)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(bucket(key).setAsync(RedisEncoder[T].encode(value), duration))
    .unit

  override def set[T: RedisEncoder](key: String, value: T)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO.fromCompletionStage(bucket(key).setAsync(RedisEncoder[T].encode(value))).unit

  override def setKeepTtl[T: RedisEncoder](key: String, value: T)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(bucket(key).setAndKeepTTLAsync(RedisEncoder[T].encode(value)))
    .unit

  override def setEx[T: RedisEncoder](key: String, duration: Duration, value: T)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(bucket(key).setAsync(RedisEncoder[T].encode(value), duration))
    .unit

  override def setNx[T: RedisEncoder](key: String, value: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).setIfAbsentAsync(RedisEncoder[T].encode(value)))
    .map(Boolean.unbox)

  override def setNxEx[T: RedisEncoder](key: String, duration: Duration, value: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).setIfAbsentAsync(RedisEncoder[T].encode(value), duration))
    .map(Boolean.unbox)

  override def setXx[T: RedisEncoder](key: String, value: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).setIfExistsAsync(RedisEncoder[T].encode(value)))
    .map(Boolean.unbox)

  override def setXxEx[T: RedisEncoder](key: String, duration: Duration, value: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).setIfExistsAsync(RedisEncoder[T].encode(value), duration))
    .map(Boolean.unbox)

  override def setRange[T: RedisEncoder](key: String, offset: Long, value: T): Task[Long] = for {
    data <- ZIO.attempt(RedisEncoder[T].encode(value).getBytes)
    byteBuffer <- ZIO.attempt(ByteBuffer.wrap(data))
    channel = binaryStream(key).getChannel
    _ <- ZIO.attempt(channel.position(offset).write(byteBuffer))
    result <- ZIO.attempt(channel.size())
  } yield result

  override def strLen(key: String)(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(bucket(key).sizeAsync()).map(_.toLong)

}

case class RedisStringOperationsLive(redissonClient: RedissonClient)
    extends RedisStringOperationsImpl

object RedisStringOperations {

  val live: URLayer[RedissonClient, RedisStringOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisStringOperationsLive))

}
