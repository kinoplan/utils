package io.kinoplan.utils.redisson.core.operation

import java.time.Duration

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{RAtomicLong, RBucket, RedissonClient}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.cross.collection.MapSyntax.syntaxMapOps
import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.JavaDecoders._

trait RedisValueOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  private lazy val bucket: String => RBucket[String] =
    redissonClient.getBucket(_, StringCodec.INSTANCE)

  private lazy val buckets = redissonClient.getBuckets(StringCodec.INSTANCE)
  private lazy val atomicLong: String => RAtomicLong = redissonClient.getAtomicLong

  protected def get[T: RedisDecoder](key: String): Future[Option[T]] = Future {
    bucket(key).get()
  }.flatMap(decodeNullableValue[T])

  protected def set[T: RedisEncoder](key: String, value: T): Future[Unit] = Future {
    bucket(key).set(RedisEncoder[T].encode(value))
  }

  protected def del(key: String): Future[Boolean] = Future {
    bucket(key).delete()
  }

  protected def incr(key: String): Future[Long] = Future {
    atomicLong(key).incrementAndGet()
  }

  protected def incrBy(key: String, delta: Long): Future[Long] = Future {
    atomicLong(key).addAndGet(delta)
  }

  protected def setEx[T: RedisEncoder](key: String, seconds: Long, value: T): Future[Unit] =
    Future {
      bucket(key).set(RedisEncoder[T].encode(value), Duration.ofSeconds(seconds))
    }

  protected def setExNx[T: RedisEncoder](key: String, seconds: Long, value: T): Future[Boolean] =
    Future {
      bucket(key).setIfAbsent(RedisEncoder[T].encode(value), Duration.ofSeconds(seconds))
    }

  protected def expire(key: String, seconds: Long): Future[Boolean] = Future {
    bucket(key).expire(Duration.ofSeconds(seconds))
  }

  protected def mSet[T: RedisEncoder](params: Map[String, T]): Future[Unit] = Future {
    buckets.set(params.crossMapValues(RedisEncoder[T].encode).asJava)
  }

  protected def mGet[T: RedisDecoder](keys: List[String]): Future[Map[String, T]] = Future {
    buckets.get[String](keys: _*)
  }.flatMap(decodeMap[T])

}
