package io.kinoplan.utils.zio.redisson.operations

import java.util.concurrent.TimeUnit

import org.redisson.api._
import org.redisson.api.stream._
import zio._

import io.kinoplan.utils.redisson.codec.base.BaseRedisDecoder
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.operations.base.ResultBuilder._
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders

/** Interface representing operations that can be performed on Redis stream data.
  */
trait RedisStreamOperations {

  /** Acknowledge one or more messages as processed in a consumer group.
    *
    * Similar to the XACK command.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param ids
    *   A non-empty chunk of message IDs to acknowledge.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of messages acknowledged.
    */
  def xAck(key: String, group: String, ids: NonEmptyChunk[StreamMessageId])(implicit
    codec: RCodec[_, _]
  ): Task[Long]

  /** Add a single entry to a stream with a specific field and value.
    *
    * Similar to the XADD command.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   Add-arguments of type `org.redisson.api.stream.StreamAddArgs`. IMPORTANT: construct it via
    *   [[io.kinoplan.utils.zio.redisson.models.StreamAdd]] factory to ensure proper codec encoding.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The ID of the added entry.
    * @see
    *   [[io.kinoplan.utils.zio.redisson.models.StreamAdd]]
    */
  def xAdd[K, V](key: String, args: StreamAddArgs[K, V])(implicit
    codec: RCodec[K, V]
  ): Task[StreamMessageId]

  /** Add an entry to a stream with a specified ID, field, and value.
    *
    * Similar to the XADD command with ID specified.
    *
    * @param key
    *   The key of the stream.
    * @param id
    *   The specific ID for the new entry.
    * @param args
    *   Add-arguments of type `org.redisson.api.stream.StreamAddArgs`. IMPORTANT: construct it via
    *   [[io.kinoplan.utils.zio.redisson.models.StreamAdd]] factory to ensure proper codec encoding.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @see
    *   [[io.kinoplan.utils.zio.redisson.models.StreamAdd]]
    */
  def xAdd[K, V](key: String, id: StreamMessageId, args: StreamAddArgs[K, V])(implicit
    codec: RCodec[K, V]
  ): Task[Unit]

  /** Add multiple entries to a stream in a batch mode.
    *
    * Similar to the XADD command for batch processing. Consumes the entire input stream into a
    * single batch operation, executes it, and emits the resulting IDs.
    *
    * @param key
    *   The key of the stream.
    * @param chunks
    *   ZIO chunks of add-arguments of type `org.redisson.api.stream.StreamAddArgs`. IMPORTANT: each
    *   element should be constructed via [[io.kinoplan.utils.zio.redisson.models.StreamAdd]]
    *   factory to ensure proper codec encoding.
    * @param options
    *   Options for batch execution, with defaults provided.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   IDs of the added entries.
    * @note
    *   All input elements are accumulated in memory before batch execution. For large streams,
    *   consider memory implications.
    * @see
    *   [[io.kinoplan.utils.zio.redisson.models.StreamAdd]]
    */
  def xAdd[K, V](
    key: String,
    chunks: Chunk[StreamAddArgs[K, V]],
    options: BatchOptions = BatchOptions.defaults()
  )(implicit
    codec: RCodec[K, V]
  ): Task[List[StreamMessageId]]

  /** Auto claims messages that have been idle for a minimum amount of time.
    *
    * Similar to the XAUTOCLAIM command.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param consumer
    *   The name of the consumer claiming the message.
    * @param minIdleTime
    *   The minimum idle time for the messages to claim.
    * @param startId
    *   The ID to start the claiming from.
    * @param count
    *   The maximum number of messages to claim. Default is 100.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The result of the auto-claim operation.
    */
  def xAutoClaim[K, V](
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    startId: StreamMessageId,
    count: Int = 100
  )(implicit
    codec: RCodec[K, V]
  ): Task[AutoClaimResult[K, V]]

  /** Auto claims messages that have been idle for a minimum amount of time, in a faster mode.
    *
    * Similar to the XAUTOCLAIM command with faster execution.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param consumer
    *   The name of the consumer claiming the message.
    * @param minIdleTime
    *   The minimum idle time for the messages to claim.
    * @param startId
    *   The ID to start the claiming from.
    * @param count
    *   The maximum number of messages to claim. Default is 100.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The fast result of the auto-claim operation.
    */
  def xAutoClaimFast(
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    startId: StreamMessageId,
    count: Int = 100
  )(implicit
    codec: RCodec[_, _]
  ): Task[FastAutoClaimResult]

  /** Claim specific messages that have been idle for a minimum amount of time.
    *
    * Similar to the XCLAIM command.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param consumer
    *   The name of the consumer claiming the message.
    * @param minIdleTime
    *   The minimum idle time for the messages to claim.
    * @param ids
    *   A sequence of message IDs to claim.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of claimed message IDs to their fields and values.
    */
  def xClaim[K, V](
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    ids: Seq[StreamMessageId]
  )(implicit
    codec: RCodec[K, V]
  ): ResultBuilder16[K, V]

  /** Claim specific messages that have been idle for a minimum amount of time, in a faster mode.
    *
    * Similar to the XCLAIM command with faster execution.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param consumer
    *   The name of the consumer claiming the message.
    * @param minIdleTime
    *   The minimum idle time for the messages to claim.
    * @param ids
    *   A sequence of message IDs to claim.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A list of claimed message IDs.
    */
  def xClaimFast(
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    ids: Seq[StreamMessageId]
  )(implicit
    codec: RCodec[_, _]
  ): Task[List[StreamMessageId]]

  /** Delete one or more entries from a stream.
    *
    * Similar to the XDEL command.
    *
    * @param key
    *   The key of the stream.
    * @param ids
    *   A sequence of message IDs to delete.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of entries deleted.
    */
  def xDel(key: String, ids: Seq[StreamMessageId])(implicit
    codec: RCodec[_, _]
  ): Task[Long]

  /** Create a consumer group on a stream.
    *
    * Similar to the XGROUP CREATE command.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   Arguments for creating the group, including group name and ID.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def xGroupCreate(key: String, args: StreamCreateGroupArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Unit]

  /** Create a consumer within a consumer group on a stream.
    *
    * Similar to the XGROUP CREATECONSUMER command.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param consumer
    *   The name of the consumer to create.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def xGroupCreateConsumer(key: String, group: String, consumer: String)(implicit
    codec: RCodec[_, _]
  ): Task[Unit]

  /** Delete a consumer from a consumer group.
    *
    * Similar to the XGROUP DELCONSUMER command.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param consumer
    *   The name of the consumer to delete.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of pending messages of the consumer that were removed.
    */
  def xGroupDelConsumer(key: String, group: String, consumer: String)(implicit
    codec: RCodec[_, _]
  ): Task[Long]

  /** Destroy a consumer group on a stream.
    *
    * Similar to the XGROUP DESTROY command.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group to destroy.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def xGroupDestroy(key: String, group: String)(implicit
    codec: RCodec[_, _]
  ): Task[Unit]

  /** Set the ID for a consumer group to specify the last delivered ID.
    *
    * Similar to the XGROUP SETID command.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param id
    *   The ID to set for the consumer group.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def xGroupSetId(key: String, group: String, id: StreamMessageId)(implicit
    codec: RCodec[_, _]
  ): Task[Unit]

  /** Retrieve information about all consumers in a specific consumer group.
    *
    * Similar to the XINFO CONSUMERS command.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A list of consumers in the specified group.
    */
  def xInfoConsumers(key: String, group: String)(implicit
    codec: RCodec[_, _]
  ): Task[List[StreamConsumer]]

  /** Retrieve information about all consumer groups of a stream.
    *
    * Similar to the XINFO GROUPS command.
    *
    * @param key
    *   The key of the stream.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A list of consumer groups for the stream.
    */
  def xInfoGroups(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[List[StreamGroup]]

  /** Retrieve information about a stream.
    *
    * Similar to the XINFO STREAM command.
    *
    * @param key
    *   The key of the stream.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The details of the stream structure and contents.
    */
  def xInfoStream[K, V](key: String)(implicit
    codec: RCodec[K, V]
  ): Task[StreamInfo[K, V]]

  /** Retrieve the length of a stream.
    *
    * Similar to the XLEN command.
    *
    * @param key
    *   The key of the stream.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of entries in the stream.
    */
  def xLen(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[Long]

  /** Retrieve information about pending messages in a consumer group.
    *
    * Similar to the XPENDING command for getting the summary.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The pending messages summary for the group.
    */
  def xPendingInfo(key: String, group: String)(implicit
    codec: RCodec[_, _]
  ): Task[PendingResult]

  /** Retrieve a list of pending messages in a range of IDs for a consumer group.
    *
    * Similar to the XPENDING command for a specific range.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   The arguments specifying how to read pending entries from the stream.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A list of pending entries within the specified range.
    */
  def xPending(key: String, args: StreamPendingRangeArgs)(implicit
    codec: RCodec[_, _]
  ): Task[List[PendingEntry]]

  /** Retrieve entries from a stream within a specific range of IDs.
    *
    * Similar to the XRANGE command.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   The arguments specifying how to read entries from the stream.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of message IDs to their fields and values within the range.
    */
  def xRange[K, V](
    key: String,
    args: StreamRangeArgs = StreamRangeArgs.startId(StreamMessageId.MIN).endId(StreamMessageId.MAX)
  )(implicit
    codec: RCodec[K, V]
  ): ResultBuilder16[K, V]

  /** Read entries from a stream using specified read arguments.
    *
    * Similar to the XREAD command.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   The arguments specifying how to read entries from the stream.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The map of message IDs to their fields and values.
    */
  def xRead[K, V](key: String, args: StreamReadArgs)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder16[K, V]

  /** Read entries from multiple streams using specified read arguments.
    *
    * Similar to the XREAD command for multiple streams.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   The arguments specifying how to read entries from multiple streams.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of stream keys to message IDs and their fields and values.
    */
  def xRead[K, V](key: String, args: StreamMultiReadArgs)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder17[K, V]

  /** Read entries from a stream as part of a consumer group.
    *
    * Similar to the XREADGROUP command.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param consumer
    *   The name of the consumer reading from the group.
    * @param args
    *   The arguments specifying how to read entries from the stream, defaulting to neverDelivered.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The map of message IDs to their fields and values.
    */
  def xReadGroup[K, V](
    key: String,
    group: String,
    consumer: String,
    args: StreamReadGroupArgs = StreamReadGroupArgs.neverDelivered()
  )(implicit
    codec: RCodec[K, V]
  ): ResultBuilder16[K, V]

  /** Read entries from multiple streams as part of a consumer group.
    *
    * Similar to the XREADGROUP command for multiple streams.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param consumer
    *   The name of the consumer reading from the group.
    * @param args
    *   The arguments specifying how to read entries from multiple streams.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of stream keys to message IDs and their fields and values.
    */
  def xReadGroup[K, V](key: String, group: String, consumer: String, args: StreamMultiReadGroupArgs)(
    implicit
    codec: RCodec[K, V]
  ): ResultBuilder17[K, V]

  /** Retrieve entries from a stream in reverse order within a specific range of IDs.
    *
    * Similar to the XREVRANGE command.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   The arguments specifying how to read entries from the stream.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam K
    *   Type of the keys stored in Redisson.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map of message IDs to their fields and values in reverse order.
    */
  def xRevRange[K, V](
    key: String,
    args: StreamRangeArgs = StreamRangeArgs.startId(StreamMessageId.MAX).endId(StreamMessageId.MIN)
  )(implicit
    codec: RCodec[K, V]
  ): ResultBuilder16[K, V]

  /** Trim a stream to a specified length or by a maximum entry ID.
    *
    * Similar to the XTRIM command.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   The arguments specifying trim parameters, such as max length or min ID.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of entries removed from the stream.
    */
  def xTrim(key: String, args: StreamTrimArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Long]

  /** Trim a stream in a non-strict mode based on specified arguments.
    *
    * Similar to the XTRIM command with non-strict execution.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   The arguments specifying trim parameters.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of entries removed from the stream.
    */
  def xTrimNonStrict(key: String, args: StreamTrimArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Long]

}

trait RedisStreamOperationsImpl extends RedisStreamOperations {
  protected val redissonClient: RedissonClient

  private def stream[K, V](key: String)(implicit
    codec: RCodec[K, V]
  ): RStream[K, V] = codec
    .underlying
    .map(redissonClient.getStream[K, V](key, _))
    .getOrElse(redissonClient.getStream[K, V](key))

  private def batchStream[K, V](key: String, batch: RBatch)(implicit
    codec: RCodec[K, V]
  ): RStreamAsync[K, V] = codec
    .underlying
    .map(batch.getStream[K, V](key, _))
    .getOrElse(batch.getStream[K, V](key))

  override def xAck(key: String, group: String, ids: NonEmptyChunk[StreamMessageId])(implicit
    codec: RCodec[_, _]
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).ackAsync(group, ids: _*)).map(_.toLong)

  override def xAdd[K, V](key: String, args: StreamAddArgs[K, V])(implicit
    codec: RCodec[K, V]
  ): Task[StreamMessageId] = ZIO.fromCompletionStage(stream(key).addAsync(args))

  override def xAdd[K, V](key: String, id: StreamMessageId, args: StreamAddArgs[K, V])(implicit
    codec: RCodec[K, V]
  ): Task[Unit] = ZIO.fromCompletionStage(stream(key).addAsync(id, args)).unit

  override def xAdd[K, V](key: String, chunks: Chunk[StreamAddArgs[K, V]], options: BatchOptions)(
    implicit
    codec: RCodec[K, V]
  ): Task[List[StreamMessageId]] = {
    val batch = redissonClient.createBatch(options)
    val currentBatchStream = batchStream(key, batch)

    ZIO
      .foreachDiscard(chunks)(args => ZIO.attempt(currentBatchStream.addAsync(args)))
      .zipRight(ZIO.fromCompletionStage(batch.executeAsync()).map(_.getResponses))
      .map(JavaDecoders.fromListUnderlying(_))
      .flatMap(responses =>
        ZIO.foreach(responses)(response => ZIO.attempt(response.asInstanceOf[StreamMessageId]))
      )
  }

  override def xAutoClaim[K, V](
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    startId: StreamMessageId,
    count: Int
  )(implicit
    codec: RCodec[K, V]
  ): Task[AutoClaimResult[K, V]] = ZIO.fromCompletionStage(
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
    codec: RCodec[_, _]
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

  override def xClaim[K, V](
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    ids: Seq[StreamMessageId]
  )(implicit
    codec: RCodec[K, V]
  ): ResultBuilder16[K, V] = new ResultBuilder16[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[StreamMessageId, Map[K, T]]] = ZIO
      .fromCompletionStage(
        stream(key).claimAsync(group, consumer, minIdleTime.toMillis, TimeUnit.MILLISECONDS, ids: _*)
      )
      .flatMap(JavaDecoders.fromStreamMessages(_))
  }

  override def xClaimFast(
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    ids: Seq[StreamMessageId]
  )(implicit
    codec: RCodec[_, _]
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
    codec: RCodec[_, _]
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).removeAsync(ids: _*)).map(_.toLong)

  override def xGroupCreate(key: String, args: StreamCreateGroupArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Unit] = ZIO.fromCompletionStage(stream(key).createGroupAsync(args)).unit

  override def xGroupCreateConsumer(key: String, group: String, consumer: String)(implicit
    codec: RCodec[_, _]
  ): Task[Unit] = ZIO.fromCompletionStage(stream(key).createConsumerAsync(group, consumer)).unit

  override def xGroupDelConsumer(key: String, group: String, consumer: String)(implicit
    codec: RCodec[_, _]
  ): Task[Long] = ZIO
    .fromCompletionStage(stream(key).removeConsumerAsync(group, consumer))
    .map(_.toLong)

  override def xGroupDestroy(key: String, group: String)(implicit
    codec: RCodec[_, _]
  ): Task[Unit] = ZIO.fromCompletionStage(stream(key).removeGroupAsync(group)).unit

  override def xGroupSetId(key: String, group: String, id: StreamMessageId)(implicit
    codec: RCodec[_, _]
  ): Task[Unit] = ZIO.fromCompletionStage(stream(key).updateGroupMessageIdAsync(group, id)).unit

  override def xInfoConsumers(key: String, group: String)(implicit
    codec: RCodec[_, _]
  ): Task[List[StreamConsumer]] = ZIO
    .fromCompletionStage(stream(key).listConsumersAsync(group))
    .map(JavaDecoders.fromListUnderlying)

  override def xInfoGroups(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[List[StreamGroup]] = ZIO
    .fromCompletionStage(stream(key).listGroupsAsync)
    .map(JavaDecoders.fromListUnderlying)

  override def xInfoStream[K, V](key: String)(implicit
    codec: RCodec[K, V]
  ): Task[StreamInfo[K, V]] = ZIO.fromCompletionStage(stream(key).getInfoAsync)

  override def xLen(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).sizeAsync()).map(_.toLong)

  override def xPendingInfo(key: String, group: String)(implicit
    codec: RCodec[_, _]
  ): Task[PendingResult] = ZIO.fromCompletionStage(stream(key).getPendingInfoAsync(group))

  override def xPending(key: String, args: StreamPendingRangeArgs)(implicit
    codec: RCodec[_, _]
  ): Task[List[PendingEntry]] = ZIO
    .fromCompletionStage(stream(key).listPendingAsync(args))
    .map(JavaDecoders.fromListUnderlying)

  override def xRange[K, V](key: String, args: StreamRangeArgs)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder16[K, V] = new ResultBuilder16[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[StreamMessageId, Map[K, T]]] = ZIO
      .fromCompletionStage(stream(key).rangeAsync(args))
      .flatMap(JavaDecoders.fromStreamMessages(_))
  }

  override def xRead[K, V](key: String, args: StreamReadArgs)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder16[K, V] = new ResultBuilder16[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[StreamMessageId, Map[K, T]]] = ZIO
      .fromCompletionStage(stream(key).readAsync(args))
      .flatMap(JavaDecoders.fromStreamMessages(_))
  }

  override def xRead[K, V](key: String, args: StreamMultiReadArgs)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder17[K, V] = new ResultBuilder17[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, Map[StreamMessageId, Map[K, T]]]] = ZIO
      .fromCompletionStage(stream(key).readAsync(args))
      .flatMap(JavaDecoders.fromStreamMultiMessages(_))
  }

  override def xReadGroup[K, V](
    key: String,
    group: String,
    consumer: String,
    args: StreamReadGroupArgs
  )(implicit
    codec: RCodec[K, V]
  ): ResultBuilder16[K, V] = new ResultBuilder16[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[StreamMessageId, Map[K, T]]] = ZIO
      .fromCompletionStage(stream(key).readGroupAsync(group, consumer, args))
      .flatMap(JavaDecoders.fromStreamMessages(_))
  }

  override def xReadGroup[K, V](
    key: String,
    group: String,
    consumer: String,
    args: StreamMultiReadGroupArgs
  )(implicit
    codec: RCodec[K, V]
  ): ResultBuilder17[K, V] = new ResultBuilder17[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, Map[StreamMessageId, Map[K, T]]]] = ZIO
      .fromCompletionStage(stream(key).readGroupAsync(group, consumer, args))
      .flatMap(JavaDecoders.fromStreamMultiMessages(_))
  }

  override def xRevRange[K, V](key: String, args: StreamRangeArgs)(implicit
    codec: RCodec[K, V]
  ): ResultBuilder16[K, V] = new ResultBuilder16[K, V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[StreamMessageId, Map[K, T]]] = ZIO
      .fromCompletionStage(stream(key).rangeReversedAsync(args))
      .flatMap(JavaDecoders.fromStreamMessages(_))
  }

  override def xTrim(key: String, args: StreamTrimArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).trimAsync(args)).map(_.toLong)

  override def xTrimNonStrict(key: String, args: StreamTrimArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).trimNonStrictAsync(args)).map(_.toLong)

}

case class RedisStreamOperationsLive(redissonClient: RedissonClient)
    extends RedisStreamOperationsImpl

object RedisStreamOperations {

  val live: URLayer[RedissonClient, RedisStreamOperationsLive] =
    ZLayer.fromFunction(RedisStreamOperationsLive.apply _)

}
