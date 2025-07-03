package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.{JavaDecoders, JavaEncoders}
import org.redisson.api.stream._
import org.redisson.api._
import zio.{Duration, NonEmptyChunk, Task, URLayer, ZIO, ZLayer}

import java.util.concurrent.TimeUnit

trait RedisStreamOperations {

  def xAck(key: String, group: String, ids: NonEmptyChunk[StreamMessageId])(implicit
    codec: RCodec
  ): Task[Long]

  def xAdd[T: RedisEncoder](key: String, field: String, value: T)(implicit
    codec: RCodec
  ): Task[StreamMessageId]

  def xAdd[T: RedisEncoder](key: String, id: StreamMessageId, field: String, value: T)(implicit
    codec: RCodec
  ): Task[Unit]

  def xAdd[T: RedisEncoder](key: String, fieldsValue: Map[String, T])(implicit
    codec: RCodec
  ): Task[StreamMessageId]

  def xAdd[T: RedisEncoder](key: String, id: StreamMessageId, fieldsValue: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit]

  def xAdd[T: RedisEncoder](key: String, fieldsValues: Seq[Map[String, T]], options: BatchOptions)(
    implicit
    codec: RCodec
  ): Task[List[StreamMessageId]]

  def xAutoClaim[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    startId: StreamMessageId,
    count: Int = 100
  )(implicit
    codec: RCodec
  ): Task[AutoClaimResult[String, String]]

  def xAutoClaimFast(
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    startId: StreamMessageId,
    count: Int = 100
  )(implicit
    codec: RCodec
  ): Task[FastAutoClaimResult]

  def xClaim[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    ids: Seq[StreamMessageId]
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  def xClaimFast(
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    ids: Seq[StreamMessageId]
  )(implicit
    codec: RCodec
  ): Task[List[StreamMessageId]]

  def xDel(key: String, ids: Seq[StreamMessageId])(implicit
    codec: RCodec
  ): Task[Long]

  def xGroupCreate(key: String, args: StreamCreateGroupArgs)(implicit
    codec: RCodec
  ): Task[Unit]

  def xGroupCreateConsumer(key: String, group: String, consumer: String)(implicit
    codec: RCodec
  ): Task[Unit]

  def xGroupDelConsumer(key: String, group: String, consumer: String)(implicit
    codec: RCodec
  ): Task[Long]

  def xGroupDestroy(key: String, group: String)(implicit
    codec: RCodec
  ): Task[Unit]

  def xGroupSetId(key: String, group: String, id: StreamMessageId)(implicit
    codec: RCodec
  ): Task[Unit]

  def xInfoConsumers(key: String, group: String)(implicit
    codec: RCodec
  ): Task[List[StreamConsumer]]

  def xInfoGroups(key: String)(implicit
    codec: RCodec
  ): Task[List[StreamGroup]]

  def xInfoStream(key: String)(implicit
    codec: RCodec
  ): Task[StreamInfo[String, String]]

  def xLen(key: String)(implicit
    codec: RCodec
  ): Task[Long]

  def xPendingInfo(key: String, group: String)(implicit
    codec: RCodec
  ): Task[PendingResult]

  def xPending(key: String, group: String, start: StreamMessageId, end: StreamMessageId, count: Int)(
    implicit
    codec: RCodec
  ): Task[List[PendingEntry]]

  def xPending(
    key: String,
    group: String,
    start: StreamMessageId,
    end: StreamMessageId,
    minIdleTime: Duration,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[List[PendingEntry]]

  def xPending(
    key: String,
    group: String,
    consumer: String,
    start: StreamMessageId,
    end: StreamMessageId,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[List[PendingEntry]]

  def xPending(
    key: String,
    group: String,
    consumer: String,
    start: StreamMessageId,
    end: StreamMessageId,
    minIdleTime: Duration,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[List[PendingEntry]]

  def xRange[T: RedisDecoder](key: String, start: StreamMessageId, end: StreamMessageId)(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  def xRange[T: RedisDecoder](key: String, start: StreamMessageId, end: StreamMessageId, count: Int)(
    implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  def xRead[T: RedisDecoder](key: String, args: StreamReadArgs)(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  def xRead[T: RedisDecoder](key: String, args: StreamMultiReadArgs)(implicit
    codec: RCodec
  ): Task[Map[String, Map[StreamMessageId, Map[String, T]]]]

  def xReadGroup[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    args: StreamReadGroupArgs
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  def xReadGroup[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    args: StreamMultiReadGroupArgs
  )(implicit
    codec: RCodec
  ): Task[Map[String, Map[StreamMessageId, Map[String, T]]]]

  def xRevRange[T: RedisDecoder](key: String, start: StreamMessageId, end: StreamMessageId)(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  def xRevRange[T: RedisDecoder](
    key: String,
    start: StreamMessageId,
    end: StreamMessageId,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  def xTrim[T: RedisDecoder](key: String, args: StreamTrimArgs)(implicit
    codec: RCodec
  ): Task[Long]

  def xTrimNonStrict[T: RedisDecoder](key: String, args: StreamTrimArgs)(implicit
    codec: RCodec
  ): Task[Long]

}

trait RedisStreamOperationsImpl extends RedisStreamOperations {
  protected val redissonClient: RedissonClient

  private def stream(key: String)(implicit
    codec: RCodec
  ): RStream[String, String] = codec
    .underlying
    .map(redissonClient.getStream[String, String](key, _))
    .getOrElse(redissonClient.getStream[String, String](key))

  private def batchStream(key: String, batch: RBatch)(implicit
    codec: RCodec
  ): RStreamAsync[String, String] = codec
    .underlying
    .map(batch.getStream[String, String](key, _))
    .getOrElse(batch.getStream[String, String](key))

  override def xAck(key: String, group: String, ids: NonEmptyChunk[StreamMessageId])(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).ackAsync(group, ids: _*)).map(_.toLong)

  override def xAdd[T: RedisEncoder](key: String, field: String, value: T)(implicit
    codec: RCodec
  ): Task[StreamMessageId] = ZIO.fromCompletionStage(
    stream(key).addAsync(StreamAddArgs.entry(field, RedisEncoder[T].encode(value)))
  )

  override def xAdd[T: RedisEncoder](key: String, id: StreamMessageId, field: String, value: T)(
    implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(
      stream(key).addAsync(id, StreamAddArgs.entry(field, RedisEncoder[T].encode(value)))
    )
    .unit

  override def xAdd[T: RedisEncoder](key: String, fieldsValue: Map[String, T])(implicit
    codec: RCodec
  ): Task[StreamMessageId] = ZIO.fromCompletionStage(
    stream(key).addAsync(StreamAddArgs.entries(JavaEncoders.fromMap(fieldsValue)))
  )

  override def xAdd[T: RedisEncoder](key: String, id: StreamMessageId, fieldsValue: Map[String, T])(
    implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(
      stream(key).addAsync(id, StreamAddArgs.entries(JavaEncoders.fromMap(fieldsValue)))
    )
    .unit

  override def xAdd[T: RedisEncoder](
    key: String,
    fieldsValues: Seq[Map[String, T]],
    options: BatchOptions
  )(implicit
    codec: RCodec
  ): Task[List[StreamMessageId]] = {
    val batch = redissonClient.createBatch(options)
    val currentBatchStream = batchStream(key, batch)

    fieldsValues.foreach { fieldsValue =>
      currentBatchStream.addAsync(StreamAddArgs.entries(JavaEncoders.fromMap(fieldsValue)))
    }

    ZIO
      .fromCompletionStage(batch.executeAsync())
      .map(_.getResponses)
      .map(JavaDecoders.fromListUnderlying(_))
      .flatMap(responses =>
        ZIO.foreach(responses)(response => ZIO.attempt(response.asInstanceOf[StreamMessageId]))
      )
  }

  override def xAutoClaim[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    startId: StreamMessageId,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[AutoClaimResult[String, String]] = ZIO.fromCompletionStage(
    stream(key).autoClaimAsync(
      group,
      consumer,
      minIdleTime.toMillis,
      TimeUnit.MILLISECONDS,
      startId,
      count
    )
  )

  override def xAutoClaimFast(
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    startId: StreamMessageId,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[FastAutoClaimResult] = ZIO.fromCompletionStage(
    stream(key).fastAutoClaimAsync(
      group,
      consumer,
      minIdleTime.toMillis,
      TimeUnit.MILLISECONDS,
      startId,
      count
    )
  )

  override def xClaim[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    ids: Seq[StreamMessageId]
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]] = ZIO
    .fromCompletionStage(
      stream(key).claimAsync(group, consumer, minIdleTime.toMillis, TimeUnit.MILLISECONDS, ids: _*)
    )
    .flatMap(JavaDecoders.fromStreamMessages(_))

  override def xClaimFast(
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    ids: Seq[StreamMessageId]
  )(implicit
    codec: RCodec
  ): Task[List[StreamMessageId]] = ZIO
    .fromCompletionStage(
      stream(key).fastClaimAsync(
        group,
        consumer,
        minIdleTime.toMillis,
        TimeUnit.MILLISECONDS,
        ids: _*
      )
    )
    .map(JavaDecoders.fromListUnderlying)

  override def xDel(key: String, ids: Seq[StreamMessageId])(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).removeAsync(ids: _*)).map(_.toLong)

  override def xGroupCreate(key: String, args: StreamCreateGroupArgs)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO.fromCompletionStage(stream(key).createGroupAsync(args)).unit

  override def xGroupCreateConsumer(key: String, group: String, consumer: String)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO.fromCompletionStage(stream(key).createConsumerAsync(group, consumer)).unit

  override def xGroupDelConsumer(key: String, group: String, consumer: String)(implicit
    codec: RCodec
  ): Task[Long] = ZIO
    .fromCompletionStage(stream(key).removeConsumerAsync(group, consumer))
    .map(_.toLong)

  override def xGroupDestroy(key: String, group: String)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO.fromCompletionStage(stream(key).removeGroupAsync(group)).unit

  override def xGroupSetId(key: String, group: String, id: StreamMessageId)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO.fromCompletionStage(stream(key).updateGroupMessageIdAsync(group, id)).unit

  override def xInfoConsumers(key: String, group: String)(implicit
    codec: RCodec
  ): Task[List[StreamConsumer]] = ZIO
    .fromCompletionStage(stream(key).listConsumersAsync(group))
    .map(JavaDecoders.fromListUnderlying)

  override def xInfoGroups(key: String)(implicit
    codec: RCodec
  ): Task[List[StreamGroup]] = ZIO
    .fromCompletionStage(stream(key).listGroupsAsync)
    .map(JavaDecoders.fromListUnderlying)

  override def xInfoStream(key: String)(implicit
    codec: RCodec
  ): Task[StreamInfo[String, String]] = ZIO.fromCompletionStage(stream(key).getInfoAsync)

  override def xLen(key: String)(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).sizeAsync()).map(_.toLong)

  override def xPendingInfo(key: String, group: String)(implicit
    codec: RCodec
  ): Task[PendingResult] = ZIO.fromCompletionStage(stream(key).getPendingInfoAsync(group))

  override def xPending(
    key: String,
    group: String,
    start: StreamMessageId,
    end: StreamMessageId,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[List[PendingEntry]] = ZIO
    .fromCompletionStage(stream(key).listPendingAsync(group, start, end, count))
    .map(JavaDecoders.fromListUnderlying)

  override def xPending(
    key: String,
    group: String,
    start: StreamMessageId,
    end: StreamMessageId,
    minIdleTime: Duration,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[List[PendingEntry]] = ZIO
    .fromCompletionStage(
      stream(key).listPendingAsync(
        group,
        start,
        end,
        minIdleTime.toMillis,
        TimeUnit.MILLISECONDS,
        count
      )
    )
    .map(JavaDecoders.fromListUnderlying)

  override def xPending(
    key: String,
    group: String,
    consumer: String,
    start: StreamMessageId,
    end: StreamMessageId,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[List[PendingEntry]] = ZIO
    .fromCompletionStage(stream(key).listPendingAsync(group, consumer, start, end, count))
    .map(JavaDecoders.fromListUnderlying)

  override def xPending(
    key: String,
    group: String,
    consumer: String,
    start: StreamMessageId,
    end: StreamMessageId,
    minIdleTime: Duration,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[List[PendingEntry]] = ZIO
    .fromCompletionStage(
      stream(key).listPendingAsync(
        group,
        consumer,
        start,
        end,
        minIdleTime.toMillis,
        TimeUnit.MILLISECONDS,
        count
      )
    )
    .map(JavaDecoders.fromListUnderlying)

  override def xRange[T: RedisDecoder](key: String, start: StreamMessageId, end: StreamMessageId)(
    implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]] = ZIO
    .fromCompletionStage(stream(key).rangeAsync(start, end))
    .flatMap(JavaDecoders.fromStreamMessages(_))

  override def xRange[T: RedisDecoder](
    key: String,
    start: StreamMessageId,
    end: StreamMessageId,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]] = ZIO
    .fromCompletionStage(stream(key).rangeAsync(count, start, end))
    .flatMap(JavaDecoders.fromStreamMessages(_))

  override def xRead[T: RedisDecoder](key: String, args: StreamReadArgs)(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]] = ZIO
    .fromCompletionStage(stream(key).readAsync(args))
    .flatMap(JavaDecoders.fromStreamMessages(_))

  override def xRead[T: RedisDecoder](key: String, args: StreamMultiReadArgs)(implicit
    codec: RCodec
  ): Task[Map[String, Map[StreamMessageId, Map[String, T]]]] = ZIO
    .fromCompletionStage(stream(key).readAsync(args))
    .flatMap(JavaDecoders.fromStreamMultiMessages(_))

  override def xReadGroup[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    args: StreamReadGroupArgs
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]] = ZIO
    .fromCompletionStage(stream(key).readGroupAsync(group, consumer, args))
    .flatMap(JavaDecoders.fromStreamMessages(_))

  override def xReadGroup[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    args: StreamMultiReadGroupArgs
  )(implicit
    codec: RCodec
  ): Task[Map[String, Map[StreamMessageId, Map[String, T]]]] = ZIO
    .fromCompletionStage(stream(key).readGroupAsync(group, consumer, args))
    .flatMap(JavaDecoders.fromStreamMultiMessages(_))

  override def xRevRange[T: RedisDecoder](key: String, start: StreamMessageId, end: StreamMessageId)(
    implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]] = ZIO
    .fromCompletionStage(stream(key).rangeReversedAsync(start, end))
    .flatMap(JavaDecoders.fromStreamMessages(_))

  override def xRevRange[T: RedisDecoder](
    key: String,
    start: StreamMessageId,
    end: StreamMessageId,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]] = ZIO
    .fromCompletionStage(stream(key).rangeReversedAsync(count, start, end))
    .flatMap(JavaDecoders.fromStreamMessages(_))

  override def xTrim[T: RedisDecoder](key: String, args: StreamTrimArgs)(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).trimAsync(args)).map(_.toLong)

  override def xTrimNonStrict[T: RedisDecoder](key: String, args: StreamTrimArgs)(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).trimNonStrictAsync(args)).map(_.toLong)

}

case class RedisStreamOperationsLive(redissonClient: RedissonClient)
    extends RedisStreamOperationsImpl

object RedisStreamOperations {

  val live: URLayer[RedissonClient, RedisStreamOperationsLive] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisStreamOperationsLive))

}
