package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.{JavaDecoders, JavaEncoders}
import org.redisson.api.stream._
import org.redisson.api._
import zio.{Duration, NonEmptyChunk, Task, URLayer, ZIO, ZLayer}

import java.util.concurrent.TimeUnit

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
    codec: RCodec
  ): Task[Long]

  /** Add a single entry to a stream with a specific field and value.
    *
    * Similar to the XADD command.
    *
    * @param key
    *   The key of the stream.
    * @param field
    *   The field of the entry to add.
    * @param value
    *   The value to associate with the field.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The ID of the added entry.
    */
  def xAdd[T: RedisEncoder](key: String, field: String, value: T)(implicit
    codec: RCodec
  ): Task[StreamMessageId]

  /** Add an entry to a stream with a specified ID, field, and value.
    *
    * Similar to the XADD command with ID specified.
    *
    * @param key
    *   The key of the stream.
    * @param id
    *   The specific ID for the new entry.
    * @param field
    *   The field of the entry to add.
    * @param value
    *   The value to associate with the field.
    * @tparam T
    *   Type of the value, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def xAdd[T: RedisEncoder](key: String, id: StreamMessageId, field: String, value: T)(implicit
    codec: RCodec
  ): Task[Unit]

  /** Add multiple fields and values to a stream as a single entry.
    *
    * Similar to the XADD command with multiple fields.
    *
    * @param key
    *   The key of the stream.
    * @param fieldsValue
    *   A map of fields and their associated values.
    * @tparam T
    *   Type of the values, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The ID of the added entry.
    */
  def xAdd[T: RedisEncoder](key: String, fieldsValue: Map[String, T])(implicit
    codec: RCodec
  ): Task[StreamMessageId]

  /** Add multiple fields and values to a stream with a specified ID.
    *
    * Similar to the XADD command with multiple fields and a specified ID.
    *
    * @param key
    *   The key of the stream.
    * @param id
    *   The specific ID for the new entry.
    * @param fieldsValue
    *   A map of fields and their associated values.
    * @tparam T
    *   Type of the values, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def xAdd[T: RedisEncoder](key: String, id: StreamMessageId, fieldsValue: Map[String, T])(implicit
    codec: RCodec
  ): Task[Unit]

  /** Add multiple entries to a stream in a batch mode.
    *
    * Similar to the XADD command for batch processing.
    *
    * @param key
    *   The key of the stream.
    * @param fieldsValues
    *   A sequence of maps, each containing fields and their associated values.
    * @param options
    *   Options for batch execution, with defaults provided.
    * @tparam T
    *   Type of the values, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A list of IDs of the added entries.
    */
  def xAdd[T: RedisEncoder](
    key: String,
    fieldsValues: Seq[Map[String, T]],
    options: BatchOptions = BatchOptions.defaults()
  )(implicit
    codec: RCodec
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
    * @return
    *   The result of the auto-claim operation.
    */
  def xAutoClaim(
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    startId: StreamMessageId,
    count: Int = 100
  )(implicit
    codec: RCodec
  ): Task[AutoClaimResult[String, String]]

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
    codec: RCodec
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
    * @tparam T
    *   Type of the message values, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of claimed message IDs to their fields and values.
    */
  def xClaim[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    minIdleTime: Duration,
    ids: Seq[StreamMessageId]
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

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
    codec: RCodec
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
    codec: RCodec
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
    codec: RCodec
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
    codec: RCodec
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
    codec: RCodec
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
    codec: RCodec
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
    codec: RCodec
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
    codec: RCodec
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
    codec: RCodec
  ): Task[List[StreamGroup]]

  /** Retrieve information about a stream.
    *
    * Similar to the XINFO STREAM command.
    *
    * @param key
    *   The key of the stream.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The details of the stream structure and contents.
    */
  def xInfoStream(key: String)(implicit
    codec: RCodec
  ): Task[StreamInfo[String, String]]

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
    codec: RCodec
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
    codec: RCodec
  ): Task[PendingResult]

  /** Retrieve a list of pending messages in a range of IDs for a consumer group.
    *
    * Similar to the XPENDING command for a specific range.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param start
    *   The starting ID for the range.
    * @param end
    *   The ending ID for the range.
    * @param count
    *   The maximum number of pending entries to return.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A list of pending entries within the specified range.
    */
  def xPending(key: String, group: String, start: StreamMessageId, end: StreamMessageId, count: Int)(
    implicit
    codec: RCodec
  ): Task[List[PendingEntry]]

  /** Retrieve a list of pending messages in a range of IDs for a consumer group, filtered by
    * minimum idle time.
    *
    * Similar to the XPENDING command for a specific range with min idle time.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param start
    *   The starting ID for the range.
    * @param end
    *   The ending ID for the range.
    * @param minIdleTime
    *   The minimum idle time for messages to be included.
    * @param count
    *   The maximum number of pending entries to return.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A list of pending entries within the specified range and idle time.
    */
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

  /** Retrieve a list of pending messages for a specific consumer in a range of IDs.
    *
    * Similar to the XPENDING command for a specific consumer and range.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param consumer
    *   The name of the consumer to retrieve pending messages for.
    * @param start
    *   The starting ID for the range.
    * @param end
    *   The ending ID for the range.
    * @param count
    *   The maximum number of pending entries to return.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A list of pending entries for the specified consumer.
    */
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

  /** Retrieve a list of pending messages for a specific consumer in a range of IDs, filtered by
    * minimum idle time.
    *
    * Similar to the XPENDING command for a specific consumer and range with min idle time.
    *
    * @param key
    *   The key of the stream.
    * @param group
    *   The name of the consumer group.
    * @param consumer
    *   The name of the consumer to retrieve pending messages for.
    * @param start
    *   The starting ID for the range.
    * @param end
    *   The ending ID for the range.
    * @param minIdleTime
    *   The minimum idle time for messages to be included.
    * @param count
    *   The maximum number of pending entries to return.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A list of pending entries for the specified consumer and idle time.
    */
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

  /** Retrieve entries from a stream within a specific range of IDs.
    *
    * Similar to the XRANGE command.
    *
    * @param key
    *   The key of the stream.
    * @param start
    *   The starting ID for the range (inclusive).
    * @param end
    *   The ending ID for the range (inclusive).
    * @tparam T
    *   Type of the message values, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of message IDs to their fields and values within the range.
    */
  def xRange[T: RedisDecoder](
    key: String,
    start: StreamMessageId = StreamMessageId.MIN,
    end: StreamMessageId = StreamMessageId.MAX
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  /** Retrieve a limited number of entries from a stream within a specific range of IDs.
    *
    * Similar to the XRANGE command with a count.
    *
    * @param key
    *   The key of the stream.
    * @param start
    *   The starting ID for the range (inclusive).
    * @param end
    *   The ending ID for the range (inclusive).
    * @param count
    *   The maximum number of entries to retrieve.
    * @tparam T
    *   Type of the message values, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of message IDs to their fields and values within the range.
    */
  def xRange[T: RedisDecoder](key: String, start: StreamMessageId, end: StreamMessageId, count: Int)(
    implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  /** Read entries from a stream using specified read arguments.
    *
    * Similar to the XREAD command.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   The arguments specifying how to read entries from the stream.
    * @tparam T
    *   Type of the message values, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The map of message IDs to their fields and values.
    */
  def xRead[T: RedisDecoder](key: String, args: StreamReadArgs)(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  /** Read entries from multiple streams using specified read arguments.
    *
    * Similar to the XREAD command for multiple streams.
    *
    * @param key
    *   The key of the stream.
    * @param args
    *   The arguments specifying how to read entries from multiple streams.
    * @tparam T
    *   Type of the message values, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of stream keys to message IDs and their fields and values.
    */
  def xRead[T: RedisDecoder](key: String, args: StreamMultiReadArgs)(implicit
    codec: RCodec
  ): Task[Map[String, Map[StreamMessageId, Map[String, T]]]]

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
    * @tparam T
    *   Type of the message values, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The map of message IDs to their fields and values.
    */
  def xReadGroup[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    args: StreamReadGroupArgs = StreamReadGroupArgs.neverDelivered()
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

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
    * @tparam T
    *   Type of the message values, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of stream keys to message IDs and their fields and values.
    */
  def xReadGroup[T: RedisDecoder](
    key: String,
    group: String,
    consumer: String,
    args: StreamMultiReadGroupArgs
  )(implicit
    codec: RCodec
  ): Task[Map[String, Map[StreamMessageId, Map[String, T]]]]

  /** Retrieve entries from a stream in reverse order within a specific range of IDs.
    *
    * Similar to the XREVRANGE command.
    *
    * @param key
    *   The key of the stream.
    * @param start
    *   The starting ID for the range (inclusive).
    * @param end
    *   The ending ID for the range (inclusive).
    * @tparam T
    *   Type of the message values, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of message IDs to their fields and values in reverse order.
    */
  def xRevRange[T: RedisDecoder](key: String, start: StreamMessageId, end: StreamMessageId)(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

  /** Retrieve a limited number of entries from a stream in reverse order within a specific range of
    * IDs.
    *
    * Similar to the XREVRANGE command with a count.
    *
    * @param key
    *   The key of the stream.
    * @param start
    *   The starting ID for the range (inclusive).
    * @param end
    *   The ending ID for the range (inclusive).
    * @param count
    *   The maximum number of entries to retrieve.
    * @tparam T
    *   Type of the message values, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A map of message IDs to their fields and values in reverse, limited by count.
    */
  def xRevRange[T: RedisDecoder](
    key: String,
    start: StreamMessageId,
    end: StreamMessageId,
    count: Int
  )(implicit
    codec: RCodec
  ): Task[Map[StreamMessageId, Map[String, T]]]

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
    codec: RCodec
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

  override def xAutoClaim(
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

  override def xTrim(key: String, args: StreamTrimArgs)(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).trimAsync(args)).map(_.toLong)

  override def xTrimNonStrict(key: String, args: StreamTrimArgs)(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(stream(key).trimNonStrictAsync(args)).map(_.toLong)

}

case class RedisStreamOperationsLive(redissonClient: RedissonClient)
    extends RedisStreamOperationsImpl

object RedisStreamOperations {

  val live: URLayer[RedissonClient, RedisStreamOperationsLive] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisStreamOperationsLive))

}
