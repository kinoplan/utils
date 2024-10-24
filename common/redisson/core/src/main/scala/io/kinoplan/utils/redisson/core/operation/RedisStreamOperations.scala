package io.kinoplan.utils.redisson.core.operation

import java.util.concurrent.TimeUnit

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{PendingEntry, RStream, RedissonClient, StreamConsumer, StreamMessageId}
import org.redisson.api.stream.{
  StreamAddArgs,
  StreamCreateGroupArgs,
  StreamReadGroupArgs,
  StreamTrimArgs
}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.JavaDecoders._

trait RedisStreamOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  private lazy val stream: String => RStream[String, String] =
    redissonClient.getStream[String, String](_, StringCodec.INSTANCE)

  protected def xGroupCreate(key: String, group: String): Future[Unit] = Future {
    stream(key).createGroup(StreamCreateGroupArgs.name(group))
  }

  protected def xAdd[T: RedisEncoder](key: String, kv: (String, T)): Future[StreamMessageId] =
    Future {
      import kv.{_1 => objKey, _2 => objValue}
      stream(key).add(StreamAddArgs.entry(objKey, RedisEncoder[T].encode(objValue)))
    }

  protected def xDel(key: String, id: StreamMessageId): Future[Long] = Future {
    stream(key).remove(id)
  }

  protected def xAck(key: String, group: String, id: StreamMessageId): Future[Long] = Future {
    stream(key).ack(group, id)
  }

  protected def xAck(key: String, group: String, ids: Set[StreamMessageId]): Future[Long] = Future {
    stream(key).ack(group, ids.toSeq: _*)
  }

  protected def xPending(
    key: String,
    group: String,
    startId: StreamMessageId,
    endId: StreamMessageId,
    count: Int
  ): Future[List[PendingEntry]] = Future {
    stream(key).listPending(group, startId, endId, count).asScala.toList
  }

  protected def xClaim[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    idleSeconds: Long,
    ids: Set[StreamMessageId]
  ): Future[Map[StreamMessageId, Map[String, T]]] = Future {
    stream(key).claim(group, consumer, idleSeconds, TimeUnit.SECONDS, ids.toSeq: _*)
  }.flatMap(decodeStreamEntries[T])

  protected def xReadGroup[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    count: Int
  ): Future[Map[StreamMessageId, Map[String, T]]] = Future {
    val args = StreamReadGroupArgs.neverDelivered().count(count)
    stream(key).readGroup(group, consumer, args)
  }.flatMap(decodeStreamEntries[T])

  protected def xReadGroup[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    count: Int,
    blockMs: Long
  ): Future[Map[StreamMessageId, Map[String, T]]] = Future {
    val args = StreamReadGroupArgs
      .neverDelivered()
      .count(count)
      .timeout(java.time.Duration.ofMillis(blockMs))
    stream(key).readGroup(group, consumer, args)
  }.flatMap(decodeStreamEntries[T])

  protected def xTrim(key: String, count: Int): Future[Long] = Future {
    stream(key).trim(StreamTrimArgs.maxLen(count).asInstanceOf[StreamTrimArgs])
  }

  protected def xTrimNonStrict(key: String, count: Int): Future[Long] = Future {
    stream(key).trimNonStrict(StreamTrimArgs.maxLen(count).asInstanceOf[StreamTrimArgs])
  }

  protected def xListConsumers(key: String, groupName: String): Future[List[StreamConsumer]] =
    Future {
      stream(key).listConsumers(groupName).asScala.toList
    }

  protected def xRemoveConsumer(
    key: String,
    groupName: String,
    consumerName: String
  ): Future[Long] = Future {
    stream(key).removeConsumer(groupName, consumerName)
  }

}
