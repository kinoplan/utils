package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import org.redisson.api.{RPatternTopic, RTopic, RedissonClient}
import zio.stream.{Stream, ZStream}
import zio.{Chunk, Task, URLayer, ZIO, ZLayer}

/** Interface representing operations that can be performed on Redis Pub/Sub channels.
  */
trait RedisTopicOperations {

  /** Subscribes to messages published to channels matching the given pattern.
    *
    * Similar to the PSUBSCRIBE command.
    *
    * @tparam T
    *   The type of the message payload, requires RedisDecoder[T].
    * @tparam A
    *   The type of the result handled by the subscriber.
    * @param pattern
    *   The pattern to match against published channel names.
    * @param handler
    *   The function that processes each message. It receives the pattern, the actual channel name,
    *   and the message payload, then returns a ZIO effect yielding a chunk of results.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of processed results from the handler function.
    */
  def pSubscribe[T: RedisDecoder, A](pattern: String)(
    handler: (String, String, T) => ZIO[Any, Throwable, Chunk[A]]
  )(implicit
    codec: RCodec
  ): Stream[Throwable, A]

  /** Publishes a message to a specified channel.
    *
    * Similar to the PUBLISH command.
    *
    * @tparam T
    *   The type of the message payload, requires RedisEncoder[T].
    * @param channel
    *   The channel to which the message should be published.
    * @param message
    *   The message payload to be published.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Task yielding the number of clients that received the message.
    */
  def publish[T: RedisEncoder](channel: String, message: T)(implicit
    codec: RCodec
  ): Task[Long]

  /** Retrieves the number of subscribers (excluding clients subscribed to patterns) for a specified
    * channel.
    *
    * Similar to the PUBSUB NUMSUB command.
    *
    * @param channel
    *   The channel to query the number of subscribers for.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Task yielding the number of subscribers for the channel.
    */
  def pubSubNumSub(channel: String)(implicit
    codec: RCodec
  ): Task[Long]

  /** Unsubscribes from all channels that match the given pattern.
    *
    * Similar to the PUNSUBSCRIBE command.
    *
    * @param pattern
    *   The pattern used for previous subscriptions.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def pUnsubscribe(pattern: String)(implicit
    codec: RCodec
  ): Task[Unit]

  /** Subscribes to messages published to the given channel.
    *
    * Similar to the SUBSCRIBE command.
    *
    * @tparam T
    *   The type of the message payload, requires RedisDecoder[T].
    * @tparam A
    *   The type of the result handled by the subscriber.
    * @param channel
    *   The channel to subscribe to.
    * @param handler
    *   The function that processes each message. It receives the channel name and the message
    *   payload, then returns a ZIO effect yielding a chunk of results.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of processed results from the handler function.
    */
  def subscribe[T: RedisDecoder, A](channel: String)(
    handler: (String, T) => ZIO[Any, Throwable, Chunk[A]]
  )(implicit
    codec: RCodec
  ): Stream[Throwable, A]

  /** Unsubscribes from the specified channel.
    *
    * Similar to the UNSUBSCRIBE command.
    *
    * @param channel
    *   The channel to unsubscribe from.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def unsubscribe(channel: String)(implicit
    codec: RCodec
  ): Task[Unit]

}

trait RedisTopicOperationsImpl extends RedisTopicOperations {
  protected val redissonClient: RedissonClient

  private def topic(key: String)(implicit
    codec: RCodec
  ): RTopic = codec
    .underlying
    .map(redissonClient.getTopic(key, _))
    .getOrElse(redissonClient.getTopic(key))

  private def patternTopic(key: String)(implicit
    codec: RCodec
  ): RPatternTopic = codec
    .underlying
    .map(redissonClient.getPatternTopic(key, _))
    .getOrElse(redissonClient.getPatternTopic(key))

  override def pSubscribe[T: RedisDecoder, A](pattern: String)(
    handler: (String, String, T) => ZIO[Any, Throwable, Chunk[A]]
  )(implicit
    codec: RCodec
  ): Stream[Throwable, A] = ZStream.asyncScoped[Any, Throwable, A] { register =>
    ZIO.acquireRelease(
      ZIO.fromCompletionStage(
        patternTopic(pattern).addListenerAsync(
          classOf[String],
          (pattern: CharSequence, channel: CharSequence, message: String) =>
            register(
              for {
                decodedMessage <- ZIO.fromTry(RedisDecoder[T].decode(message)).asSomeError
                result <- handler(pattern.toString, channel.toString, decodedMessage).asSomeError
              } yield result
            )
        )
      )
    )(listenerId =>
      ZIO.fromCompletionStage(patternTopic(pattern).removeListenerAsync(listenerId)).orDie
    )
  }

  override def publish[T: RedisEncoder](channel: String, message: T)(implicit
    codec: RCodec
  ): Task[Long] = ZIO
    .fromCompletionStage(topic(channel).publishAsync(RedisEncoder[T].encode(message)))
    .map(_.toLong)

  override def pubSubNumSub(channel: String)(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(topic(channel).countSubscribersAsync()).map(_.toLong)

  override def pUnsubscribe(pattern: String)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO.fromCompletionStage(patternTopic(pattern).removeAllListenersAsync()).unit

  override def subscribe[T: RedisDecoder, A](channel: String)(
    handler: (String, T) => ZIO[Any, Throwable, Chunk[A]]
  )(implicit
    codec: RCodec
  ): Stream[Throwable, A] = ZStream.asyncScoped[Any, Throwable, A](register =>
    ZIO.acquireRelease(
      ZIO.fromCompletionStage(
        topic(channel).addListenerAsync(
          classOf[String],
          (channel: CharSequence, message: String) =>
            register(
              for {
                decodedMessage <- ZIO.fromTry(RedisDecoder[T].decode(message)).asSomeError
                result <- handler(channel.toString, decodedMessage).asSomeError
              } yield result
            )
        )
      )
    )(listenerId => ZIO.fromCompletionStage(topic(channel).removeListenerAsync(listenerId)).orDie)
  )

  override def unsubscribe(channel: String)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO.fromCompletionStage(topic(channel).removeAllListenersAsync()).unit

}

case class RedisTopicOperationsLive(redissonClient: RedissonClient) extends RedisTopicOperationsImpl

object RedisTopicOperations {

  val live: URLayer[RedissonClient, RedisTopicOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisTopicOperationsLive))

}
