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
import io.kinoplan.utils.redisson.core.compat.crossFutureConverters.CompletionStageOps

trait RedisStreamOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  protected lazy val stream: String => RStream[String, String] =
    redissonClient.getStream[String, String](_, StringCodec.INSTANCE)

  protected def xGroupCreate(key: String, group: String): Future[Unit] = Future {
    stream(key).createGroup(StreamCreateGroupArgs.name(group))
  }

  protected def xAdd[T: RedisEncoder](key: String, kv: (String, T)): Future[StreamMessageId] = {
    import kv.{_1 => objKey, _2 => objValue}
    stream(key).addAsync(StreamAddArgs.entry(objKey, RedisEncoder[T].encode(objValue))).asScala
  }

  protected def xDel(key: String, id: StreamMessageId): Future[Long] = stream(key)
    .removeAsync(id)
    .asScala
    .map(_.longValue())

  protected def xAck(key: String, group: String, id: StreamMessageId): Future[Long] = stream(key)
    .ackAsync(group, id)
    .asScala
    .map(_.longValue())

  protected def xAck(key: String, group: String, ids: Set[StreamMessageId]): Future[Long] =
    stream(key).ackAsync(group, ids.toSeq: _*).asScala.map(_.longValue())

  protected def xPending(
    key: String,
    group: String,
    startId: StreamMessageId,
    endId: StreamMessageId,
    count: Int
  ): Future[List[PendingEntry]] = stream(key)
    .listPendingAsync(group, startId, endId, count)
    .asScala
    .map(_.asScala.toList)

  protected def xClaim[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    idleSeconds: Long,
    ids: Set[StreamMessageId]
  ): Future[Map[StreamMessageId, Map[String, T]]] = stream(key)
    .claimAsync(group, consumer, idleSeconds, TimeUnit.SECONDS, ids.toSeq: _*)
    .asScala
    .flatMap(decodeStreamEntries[T])

  protected def xReadGroup[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    count: Int
  ): Future[Map[StreamMessageId, Map[String, T]]] = {
    val args = StreamReadGroupArgs.neverDelivered().count(count)
    stream(key).readGroupAsync(group, consumer, args).asScala
  }.flatMap(decodeStreamEntries[T])

  protected def xReadGroup[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    count: Int,
    blockMs: Long
  ): Future[Map[StreamMessageId, Map[String, T]]] = {
    val args = StreamReadGroupArgs
      .neverDelivered()
      .count(count)
      .timeout(java.time.Duration.ofMillis(blockMs))
    stream(key).readGroupAsync(group, consumer, args).asScala
  }.flatMap(decodeStreamEntries[T])

  protected def xTrim(key: String, count: Int): Future[Long] = stream(key)
    .trimAsync(StreamTrimArgs.maxLen(count).asInstanceOf[StreamTrimArgs])
    .asScala
    .map(_.longValue())

  protected def xTrimNonStrict(key: String, count: Int): Future[Long] = stream(key)
    .trimNonStrictAsync(StreamTrimArgs.maxLen(count).asInstanceOf[StreamTrimArgs])
    .asScala
    .map(_.longValue())

  protected def xListConsumers(key: String, groupName: String): Future[List[StreamConsumer]] =
    stream(key).listConsumersAsync(groupName).asScala.map(_.asScala.toList)

  protected def xRemoveConsumer(
    key: String,
    groupName: String,
    consumerName: String
  ): Future[Long] = stream(key).removeConsumerAsync(groupName, consumerName).asScala.map(_.longValue())

}
