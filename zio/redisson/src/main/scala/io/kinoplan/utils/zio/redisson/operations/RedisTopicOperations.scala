package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{RPatternTopic, RTopic, RedissonClient}
import org.redisson.api.listener.{MessageListener, PatternMessageListener}
import org.redisson.client.codec.StringCodec
import zio.{Chunk, Task, URLayer, ZIO, ZLayer}
import zio.macros.accessible
import zio.stream.{Stream, ZStream}
import io.kinoplan.utils.redisson.base.codec.{RedisDecoder, RedisEncoder}

@accessible
trait RedisTopicOperations {

  def pSubscribe[T: RedisDecoder, A](pattern: String)(
    handler: (String, String, T) => ZIO[Any, Throwable, Chunk[A]]
  ): Stream[Throwable, A]

  def publish[T: RedisEncoder](channel: String, message: T): Task[Long]

  def pubSubNumSub(channel: String): Task[Long]

  def pUnsubscribe(pattern: String): Task[Unit]

  def pUnsubscribe(pattern: String, listenerId: Int): Task[Unit]

  def subscribe[T: RedisDecoder, A](channel: String)(
    handler: (String, T) => ZIO[Any, Throwable, Chunk[A]]
  ): Stream[Throwable, A]

  def unsubscribe(channel: String): Task[Unit]

  def unsubscribe(channel: String, listenerId: Int): Task[Unit]
}

trait RedisTopicOperationsImpl extends RedisTopicOperations {
  protected val redissonClient: RedissonClient

  private lazy val topic: String => RTopic = redissonClient.getTopic(_, StringCodec.INSTANCE)

  private lazy val patternTopic: String => RPatternTopic =
    redissonClient.getPatternTopic(_, StringCodec.INSTANCE)

  override def pSubscribe[T: RedisDecoder, A](pattern: String)(
    handler: (String, String, T) => ZIO[Any, Throwable, Chunk[A]]
  ): Stream[Throwable, A] = ZStream.asyncInterrupt[Any, Throwable, A] { register =>
    val listener: PatternMessageListener[String] =
      (pattern: CharSequence, channel: CharSequence, message: String) =>
        register {
          for {
            decodedMessage <- ZIO.fromTry(RedisDecoder[T].decode(message)).asSomeError
            result <- handler(pattern.toString, channel.toString, decodedMessage).asSomeError
          } yield result
        }

    val listenerId = patternTopic(pattern).addListener(classOf[String], listener)

    Left(ZIO.succeed(patternTopic(pattern).removeListener(listenerId)))
  }

  override def publish[T: RedisEncoder](channel: String, message: T): Task[Long] = ZIO
    .fromCompletionStage(topic(channel).publishAsync(RedisEncoder[T].encode(message)))
    .map(_.longValue())

  override def pubSubNumSub(channel: String): Task[Long] = ZIO
    .fromCompletionStage(topic(channel).countSubscribersAsync())
    .map(_.longValue())

  override def pUnsubscribe(pattern: String): Task[Unit] = ZIO
    .fromCompletionStage(patternTopic(pattern).removeAllListenersAsync())
    .unit

  override def pUnsubscribe(pattern: String, listenerId: Int): Task[Unit] = ZIO
    .fromCompletionStage(patternTopic(pattern).removeListenerAsync(listenerId))
    .unit

  override def subscribe[T: RedisDecoder, A](channel: String)(
    handler: (String, T) => ZIO[Any, Throwable, Chunk[A]]
  ): Stream[Throwable, A] = ZStream.asyncInterrupt[Any, Throwable, A] { register =>
    val listener: MessageListener[String] = (channel: CharSequence, message: String) =>
      register {
        for {
          decodedMessage <- ZIO.fromTry(RedisDecoder[T].decode(message)).asSomeError
          result <- handler(channel.toString, decodedMessage).asSomeError
        } yield result
      }

    val listenerId = topic(channel).addListener(classOf[String], listener)

    Left(ZIO.succeed(topic(channel).removeListener(listenerId)))
  }

  override def unsubscribe(channel: String): Task[Unit] = ZIO
    .fromCompletionStage(topic(channel).removeListenerAsync())
    .unit

  override def unsubscribe(channel: String, listenerId: Int): Task[Unit] = ZIO
    .fromCompletionStage(topic(channel).removeListenerAsync(listenerId))
    .unit

}

case class RedisTopicOperationsLive(redissonClient: RedissonClient) extends RedisTopicOperationsImpl

object RedisTopicOperations {

  val live: URLayer[RedissonClient, RedisTopicOperations] = ZLayer
    .fromZIO(ZIO.serviceWith[RedissonClient](RedisTopicOperationsLive))

}
