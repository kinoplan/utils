package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.{JavaDecoders, JavaEncoders}
import org.redisson.api.{RMap, RMapCache, RedissonClient}
import zio.stream.ZStream
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

import scala.jdk.CollectionConverters.SetHasAsJava

trait RedisHashOperations {

  def hDel(key: String, fields: Seq[String])(implicit
    codec: RCodec
  ): Task[Long]

  def hDel[T: RedisEncoder: RedisDecoder](key: String, field: Object, value: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  def hExists(key: String, field: Object)(implicit
    codec: RCodec
  ): Task[Boolean]

  def hExpire(key: String, timeout: Duration, field: String)(implicit
    codec: RCodec
  ): Task[Boolean]

  def hExpire(key: String, timeout: Duration, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Int]

  def hExpireNx(key: String, timeout: Duration, field: String)(implicit
    codec: RCodec
  ): Task[Boolean]

  def hExpireNx(key: String, timeout: Duration, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Int]

  def hGet[T: RedisDecoder](key: String, ttl: Duration, field: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  def hGetAll[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Map[String, T]]

  def hGetDel[T: RedisDecoder](key: String, field: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  def hIncrBy[T: RedisDecoder](key: String, field: String, increment: Number)(implicit
    codec: RCodec
  ): Task[Option[T]]

  def hIncrByFloat[T: RedisDecoder](key: String, field: String, increment: Number)(implicit
    codec: RCodec
  ): Task[Option[T]]

  def hKeys(key: String)(implicit
    codec: RCodec
  ): Task[Set[String]]

  def hLen(key: String)(implicit
    codec: RCodec
  ): Task[Int]

  def hmGet[T: RedisDecoder](key: String, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Map[String, T]]

  def hmSet[T: RedisEncoder](key: String, fieldValues: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit]

  def hPersist(key: String)(implicit
    codec: RCodec
  ): Task[Boolean]

  def hpExpire(key: String, timeout: Duration, field: String)(implicit
    codec: RCodec
  ): Task[Boolean]

  def hpExpire(key: String, timeout: Duration, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Int]

  def hpExpireNx(key: String, timeout: Duration, field: String)(implicit
    codec: RCodec
  ): Task[Boolean]

  def hpExpireNx(key: String, timeout: Duration, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Int]

  def hpTtl(key: String, field: String)(implicit
    codec: RCodec
  ): Task[Option[Duration]]

  def hRandField(key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Set[String]]

  def hRandFieldWithValues[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Map[String, T]]

  def hScan[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)]

  def hScan[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)]

  def hScan[T: RedisDecoder](key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)]

  def hScan[T: RedisDecoder](key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)]

  def hScanNoValues(key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String]

  def hScanNoValues(key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String]

  def hScanNoValues(key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String]

  def hScanNoValues(key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String]

  def hScanNoKeys[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  def hScanNoKeys[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  def hScanNoKeys[T: RedisDecoder](key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  def hScanNoKeys[T: RedisDecoder](key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  def hSet[T: RedisEncoder](key: String, fieldValues: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit]

  def hSetNx[T: RedisEncoder](key: String, field: String, value: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  def hStrLen(key: String, field: String)(implicit
    codec: RCodec
  ): Task[Int]

  def hTtl(key: String, field: String)(implicit
    codec: RCodec
  ): Task[Option[Duration]]

  def hVals[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Iterable[T]]

}

trait RedisHashOperationsImpl extends RedisHashOperations {
  protected val redissonClient: RedissonClient

  private def map(key: String)(implicit
    codec: RCodec
  ): RMap[String, String] = codec
    .underlying
    .map(redissonClient.getMap[String, String](key, _))
    .getOrElse(redissonClient.getMap[String, String](key))

  private def mapCache(key: String)(implicit
    codec: RCodec
  ): RMapCache[String, String] = codec
    .underlying
    .map(redissonClient.getMapCache[String, String](key, _))
    .getOrElse(redissonClient.getMapCache[String, String](key))

  override def hDel(key: String, fields: Seq[String])(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(map(key).fastRemoveAsync(fields: _*)).map(_.toLong)

  override def hDel[T: RedisEncoder: RedisDecoder](key: String, field: Object, value: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(map(key).removeAsync(field, RedisEncoder[T].encode(value)))
    .map(Boolean.unbox)

  override def hExists(key: String, field: Object)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO.fromCompletionStage(map(key).containsKeyAsync(field)).map(Boolean.unbox)

  override def hExpire(key: String, timeout: Duration, field: String)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(mapCache(key).expireEntryAsync(field, timeout, Duration.Zero))
    .map(Boolean.unbox)

  override def hExpire(key: String, timeout: Duration, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Int] = ZIO
    .fromCompletionStage(mapCache(key).expireEntriesAsync(fields.asJava, timeout, Duration.Zero))
    .map(_.toInt)

  override def hExpireNx(key: String, timeout: Duration, field: String)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO.attempt(mapCache(key).expireEntryIfNotSet(field, timeout, Duration.Zero))

  override def hExpireNx(key: String, timeout: Duration, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Int] =
    ZIO.attempt(mapCache(key).expireEntriesIfNotSet(fields.asJava, timeout, Duration.Zero))

  override def hGet[T: RedisDecoder](key: String, ttl: Duration, field: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(map(key).getAsync(field))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def hGetAll[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Map[String, T]] = ZIO
    .fromCompletionStage(map(key).readAllMapAsync())
    .flatMap(JavaDecoders.fromMap(_))

  override def hGetDel[T: RedisDecoder](key: String, field: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(map(key).removeAsync(field))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def hIncrBy[T: RedisDecoder](key: String, field: String, increment: Number)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(map(key).addAndGetAsync(field, increment))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def hIncrByFloat[T: RedisDecoder](key: String, field: String, increment: Number)(implicit
    codec: RCodec
  ): Task[Option[T]] = hIncrBy(key, field, increment)

  override def hKeys(key: String)(implicit
    codec: RCodec
  ): Task[Set[String]] = ZIO
    .fromCompletionStage(map(key).readAllKeySetAsync())
    .map(JavaDecoders.fromSetKeys)

  override def hLen(key: String)(implicit
    codec: RCodec
  ): Task[Int] = ZIO.fromCompletionStage(map(key).sizeAsync()).map(_.toInt)

  override def hmGet[T: RedisDecoder](key: String, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Map[String, T]] = ZIO
    .fromCompletionStage(map(key).getAllAsync(fields.asJava))
    .flatMap(JavaDecoders.fromMap(_))

  override def hmSet[T: RedisEncoder](key: String, fieldValues: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit] = hSet(key, fieldValues)

  override def hPersist(key: String)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO.fromCompletionStage(mapCache(key).clearExpireAsync()).map(Boolean.unbox)

  override def hpExpire(key: String, timeout: Duration, field: String)(implicit
    codec: RCodec
  ): Task[Boolean] = hExpire(key, timeout, field)

  override def hpExpire(key: String, timeout: Duration, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Int] = hExpire(key, timeout, fields)

  override def hpExpireNx(key: String, timeout: Duration, field: String)(implicit
    codec: RCodec
  ): Task[Boolean] = hExpireNx(key, timeout, field)

  override def hpExpireNx(key: String, timeout: Duration, fields: Set[String])(implicit
    codec: RCodec
  ): Task[Int] = hExpireNx(key, timeout, fields)

  override def hpTtl(key: String, field: String)(implicit
    codec: RCodec
  ): Task[Option[Duration]] = ZIO
    .fromCompletionStage(mapCache(key).remainTimeToLiveAsync(field))
    .map(_.toLong)
    .map(result =>
      if (result < 0) None
      else Some(Duration.fromMillis(result))
    )

  override def hRandField(key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Set[String]] = ZIO
    .fromCompletionStage(map(key).randomKeysAsync(count))
    .map(JavaDecoders.fromSetKeys)

  override def hRandFieldWithValues[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Map[String, T]] = ZIO
    .fromCompletionStage(map(key).randomEntriesAsync(count))
    .flatMap(JavaDecoders.fromMap(_))

  override def hScan[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)] = ZStream
    .fromJavaStream(map(key).entrySet().stream())
    .mapZIO(JavaDecoders.fromMapEntry(_))

  override def hScan[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)] = ZStream
    .fromJavaStream(map(key).entrySet(count).stream())
    .mapZIO(JavaDecoders.fromMapEntry(_))

  override def hScan[T: RedisDecoder](key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)] = ZStream
    .fromJavaStream(map(key).entrySet(pattern).stream())
    .mapZIO(JavaDecoders.fromMapEntry(_))

  override def hScan[T: RedisDecoder](key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, (String, T)] = ZStream
    .fromJavaStream(map(key).entrySet(pattern, count).stream())
    .mapZIO(JavaDecoders.fromMapEntry(_))

  override def hScanNoValues(key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String] = ZStream.fromJavaStream(map(key).keySet().stream())

  override def hScanNoValues(key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String] = ZStream.fromJavaStream(map(key).keySet(count).stream())

  override def hScanNoValues(key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String] = ZStream.fromJavaStream(map(key).keySet(pattern).stream())

  override def hScanNoValues(key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, String] = ZStream.fromJavaStream(map(key).keySet(pattern, count).stream())

  override def hScanNoKeys[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(map(key).values().stream())
    .mapZIO(JavaDecoders.fromValue(_))

  override def hScanNoKeys[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(map(key).values(count).stream())
    .mapZIO(JavaDecoders.fromValue(_))

  override def hScanNoKeys[T: RedisDecoder](key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(map(key).values(pattern).stream())
    .mapZIO(JavaDecoders.fromValue(_))

  override def hScanNoKeys[T: RedisDecoder](key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(map(key).values(pattern, count).stream())
    .mapZIO(JavaDecoders.fromValue(_))

  override def hSet[T: RedisEncoder](key: String, fieldValues: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(map(key).putAllAsync(JavaEncoders.fromMap(fieldValues)))
    .unit

  override def hSetNx[T: RedisEncoder](key: String, field: String, value: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(map(key).fastPutIfAbsentAsync(field, RedisEncoder[T].encode(value)))
    .map(Boolean.unbox)

  override def hStrLen(key: String, field: String)(implicit
    codec: RCodec
  ): Task[Int] = ZIO.fromCompletionStage(map(key).valueSizeAsync(field)).map(_.toInt)

  override def hTtl(key: String, field: String)(implicit
    codec: RCodec
  ): Task[Option[Duration]] = hpTtl(key, field)

  override def hVals[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(map(key).readAllValuesAsync())
    .flatMap(JavaDecoders.fromCollection(_))

}

case class RedisHashOperationsLive(redissonClient: RedissonClient) extends RedisHashOperationsImpl

object RedisHashOperations {

  val live: URLayer[RedissonClient, RedisHashOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisHashOperationsLive))

}
