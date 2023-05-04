package io.kinoplan.utils.zio.redisson.operations

import java.nio.ByteBuffer

import org.redisson.api._
import zio.{Duration, Task, URLayer, ZIO, ZLayer}
import zio.stream.{ZSink, ZStream}

import io.kinoplan.utils.cross.collection.MapSyntax.syntaxMapOps
import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.codec.base.{BaseRedisDecoder, BaseRedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.operations.base.ResultBuilder._
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders

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
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    */
  def append[T](key: String, value: T)(implicit
    encoder: BaseRedisEncoder[T, String]
  ): Task[Unit]

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
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the value, or None if the key does not exist.
    */
  def get[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Get the value of a key and delete the key.
    *
    * Similar to the GETDEL command.
    *
    * @param key
    *   The key to retrieve and delete.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the value, or None if the key does not exist.
    */
  def getDel[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Get the value of a key and set its expiration.
    *
    * Similar to the GETEX command with EX|PX|EXAT|PXAT option.
    *
    * @param key
    *   The key to retrieve and set expiration for.
    * @param duration
    *   Expire time.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the value, or None if the key does not exist.
    */
  def getEx[V](key: String, duration: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Get the value of a key and make it persistent.
    *
    * Similar to the GETEX command with PERSIST option.
    *
    * @param key
    *   The key to retrieve.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the value, or None if the key does not exist.
    */
  def getExPersist[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

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
    * @return
    *   An Option containing the substring, or None if the key does not exist.
    */
  def getRange(key: String, start: Int, end: Int): ResultBuilder2

  /** Set the string value of a key and return its old value.
    *
    * Similar to the GETSET command.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The new value.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @param decoder
    *   The decoder instance that converts `V` to `T`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the old value, or None if the key did not exist.
    */
  def getSet[T, V](key: String, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V],
    decoder: BaseRedisDecoder[V, T]
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
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A Map of keys to their corresponding values.
    */
  def mGet[V](keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder3[V]

  /** Set multiple keys to multiple values.
    *
    * Similar to the MSET command.
    *
    * @param params
    *   A map of key-value pairs to set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def mSet[T, V](params: Map[String, T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit]

  /** Set multiple keys to multiple values, only if none of the keys exist.
    *
    * Similar to the MSETNX command.
    *
    * @param params
    *   A map of key-value pairs to set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def mSetNx[T, V](params: Map[String, T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
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
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def pSetEx[T, V](key: String, duration: Duration, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit]

  /** Set the value of a key.
    *
    * Similar to the SET command.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The value to set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def set[T, V](key: String, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit]

  /** Set the value of a key and retain its current time-to-live (TTL).
    *
    * Similar to the SET command with KEEPTTL option.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The value to set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def setKeepTtl[T, V](key: String, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
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
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def setEx[T, V](key: String, duration: Duration, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit]

  /** Set the value of a key, only if the key does not exist.
    *
    * Similar to the SET command with NX option.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The value to set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def setNx[T, V](key: String, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
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
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def setNxEx[T, V](key: String, duration: Duration, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Set the value of a key only if the key exists.
    *
    * Similar to the SET command with XX option.
    *
    * @param key
    *   The key to set.
    * @param value
    *   The value to set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def setXx[T, V](key: String, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
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
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def setXxEx[T, V](key: String, duration: Duration, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
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
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @return
    *   The length of the string after modification.
    */
  def setRange[T](key: String, offset: Long, value: T)(implicit
    encoder: BaseRedisEncoder[T, String]
  ): Task[Long]

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
    codec: RCodec[_, _]
  ): Task[Long]

}

trait RedisStringOperationsImpl extends RedisStringOperations {
  protected val redissonClient: RedissonClient

  private def binaryStream(key: String): RBinaryStream = redissonClient.getBinaryStream(key)

  private def bucket[V](key: String)(implicit
    codec: RCodec[_, V]
  ): RBucket[V] = codec
    .underlying
    .map(redissonClient.getBucket[V](key, _))
    .getOrElse(redissonClient.getBucket[V](key))

  private def buckets(implicit
    codec: RCodec[_, _]
  ): RBuckets = codec.underlying.map(redissonClient.getBuckets).getOrElse(redissonClient.getBuckets)

  private def atomicLong(key: String): RAtomicLong = redissonClient.getAtomicLong(key)

  private def atomicDouble(key: String): RAtomicDouble = redissonClient.getAtomicDouble(key)

  override def append[T](key: String, value: T)(implicit
    encoder: BaseRedisEncoder[T, String]
  ): Task[Unit] = ZStream
    .fromIterable(encoder.encode(value).getBytes)
    .run(ZSink.fromOutputStream(binaryStream(key).getOutputStream))
    .unit

  override def decr(key: String): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).decrementAndGetAsync())
    .map(_.toLong)

  override def decrBy(key: String, decrement: Long): Task[Long] = ZIO
    .fromCompletionStage(atomicLong(key).addAndGetAsync(-decrement))
    .map(_.toLong)

  override def get[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(bucket(key).getAsync)
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def getDel[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(bucket(key).getAndDeleteAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def getEx[V](key: String, duration: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(bucket(key).getAndExpireAsync(duration))
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def getExPersist[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(bucket(key).getAndClearExpireAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def getRange(key: String, start: Int, end: Int): ResultBuilder2 = new ResultBuilder2 {
    override def as[T](implicit
      decoder: BaseRedisDecoder[String, T]
    ): Task[Option[T]] = for {
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
      result <- JavaDecoders.fromNullableString(value)
    } yield result
  }

  override def getSet[T, V](key: String, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(bucket(key).getAndSetAsync(codec.encode(value)))
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

  override def mGet[V](keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder3[V] = new ResultBuilder3[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, T]] = ZIO
      .fromCompletionStage(buckets.getAsync[V](keys: _*))
      .flatMap(JavaDecoders.fromMapValue(_))
  }

  override def mSet[T, V](params: Map[String, T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = ZIO
    .fromCompletionStage(buckets.setAsync(params.crossMapValues(codec.encode(_)).asJava))
    .unit

  override def mSetNx[T, V](params: Map[String, T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(buckets.trySetAsync(params.crossMapValues(codec.encode(_)).asJava))
    .map(Boolean.unbox)

  override def pSetEx[T, V](key: String, duration: Duration, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = ZIO.fromCompletionStage(bucket(key).setAsync(codec.encode(value), duration)).unit

  override def set[T, V](key: String, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = ZIO.fromCompletionStage(bucket(key).setAsync(codec.encode(value))).unit

  override def setKeepTtl[T, V](key: String, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = ZIO.fromCompletionStage(bucket(key).setAndKeepTTLAsync(codec.encode(value))).unit

  override def setEx[T, V](key: String, duration: Duration, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = ZIO.fromCompletionStage(bucket(key).setAsync(codec.encode(value), duration)).unit

  override def setNx[T, V](key: String, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).setIfAbsentAsync(codec.encode(value)))
    .map(Boolean.unbox)

  override def setNxEx[T, V](key: String, duration: Duration, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).setIfAbsentAsync(codec.encode(value), duration))
    .map(Boolean.unbox)

  override def setXx[T, V](key: String, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).setIfExistsAsync(codec.encode(value)))
    .map(Boolean.unbox)

  override def setXxEx[T, V](key: String, duration: Duration, value: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).setIfExistsAsync(codec.encode(value), duration))
    .map(Boolean.unbox)

  override def setRange[T](key: String, offset: Long, value: T)(implicit
    encoder: BaseRedisEncoder[T, String]
  ): Task[Long] = ZIO.scoped(
    for {
      data <- ZIO.attempt(encoder.encode(value).getBytes)
      byteBuffer <- ZIO.attempt(ByteBuffer.wrap(data))
      channel <- ZIO.fromAutoCloseable(ZIO.attempt(binaryStream(key).getChannel))
      _ <- ZIO.attempt(channel.position(offset).write(byteBuffer))
      result <- ZIO.attempt(channel.size())
    } yield result
  )

  override def strLen(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[Long] = ZIO.fromCompletionStage(bucket(key).sizeAsync()).map(_.toLong)

}

case class RedisStringOperationsLive(redissonClient: RedissonClient)
    extends RedisStringOperationsImpl

object RedisStringOperations {

  val live: URLayer[RedissonClient, RedisStringOperations] =
    ZLayer.fromFunction(RedisStringOperationsLive.apply _)

}
