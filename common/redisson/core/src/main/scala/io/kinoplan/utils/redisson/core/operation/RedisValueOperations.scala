package io.kinoplan.utils.redisson.core.operation

import java.time.Duration

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{RAtomicLong, RBucket, RedissonClient}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.cross.collection.MapSyntax.syntaxMapOps
import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.JavaDecoders._
import io.kinoplan.utils.redisson.core.compat.crossFutureConverters.CompletionStageOps

trait RedisValueOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  protected lazy val bucket: String => RBucket[String] =
    redissonClient.getBucket(_, StringCodec.INSTANCE)

  protected lazy val buckets = redissonClient.getBuckets(StringCodec.INSTANCE)
  protected lazy val atomicLong: String => RAtomicLong = redissonClient.getAtomicLong

  protected def get[T: RedisDecoder](key: String): Future[Option[T]] = bucket(key)
    .getAsync
    .asScala
    .flatMap(decodeNullableValue[T])

  protected def set[T: RedisEncoder](key: String, value: T): Future[Unit] = bucket(key)
    .setAsync(RedisEncoder[T].encode(value))
    .asScala
    .map(_ => ())

  protected def del(key: String): Future[Boolean] = bucket(key)
    .deleteAsync()
    .asScala
    .map(_.booleanValue())

  protected def incr(key: String): Future[Long] = atomicLong(key)
    .incrementAndGetAsync()
    .asScala
    .map(_.longValue())

  protected def incrBy(key: String, delta: Long): Future[Long] = atomicLong(key)
    .addAndGetAsync(delta)
    .asScala
    .map(_.longValue())

  protected def setEx[T: RedisEncoder](key: String, seconds: Long, value: T): Future[Unit] = bucket(
    key
  ).setAsync(RedisEncoder[T].encode(value), Duration.ofSeconds(seconds)).asScala.map(_ => ())

  protected def setExNx[T: RedisEncoder](key: String, seconds: Long, value: T): Future[Boolean] =
    bucket(key)
      .setIfAbsentAsync(RedisEncoder[T].encode(value), Duration.ofSeconds(seconds))
      .asScala
      .map(_.booleanValue())

  protected def expire(key: String, seconds: Long): Future[Boolean] = bucket(key)
    .expireAsync(Duration.ofSeconds(seconds))
    .asScala
    .map(_.booleanValue())

  protected def mSet[T: RedisEncoder](params: Map[String, T]): Future[Unit] = buckets
    .setAsync(params.crossMapValues(RedisEncoder[T].encode).asJava)
    .asScala
    .map(_ => ())

  protected def mGet[T: RedisDecoder](keys: List[String]): Future[Map[String, T]] = buckets
    .getAsync[String](keys: _*)
    .asScala
    .flatMap(decodeMap[T])

}
