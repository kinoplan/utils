package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{RPatternTopic, RTopic, RedissonClient}
import org.redisson.api.listener.{MessageListener, PatternMessageListener}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.core.compat.crossFutureConverters.CompletionStageOps

trait RedisTopicOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  protected lazy val topic: String => RTopic = redissonClient.getTopic(_, StringCodec.INSTANCE)

  protected lazy val patternTopic: String => RPatternTopic =
    redissonClient.getPatternTopic(_, StringCodec.INSTANCE)

  protected def publish[T: RedisEncoder](key: String, value: T): Future[Long] = topic(key)
    .publishAsync(RedisEncoder[T].encode(value))
    .asScala
    .map(_.longValue())

  protected def subscribe[T: RedisDecoder](key: String)(handler: (String, T) => Unit): Future[Int] = {
    val listener: MessageListener[String] = (channel: CharSequence, message: String) =>
      RedisDecoder[T].decode(message).foreach(handler(channel.toString, _))

    topic(key).addListenerAsync(classOf[String], listener).asScala.map(_.intValue())

  }

  protected def pSubscribe[T: RedisDecoder](
    pattern: String
  )(handler: (String, String, T) => Unit): Future[Int] = {
    val listener: PatternMessageListener[String] =
      (pattern: CharSequence, channel: CharSequence, message: String) =>
        RedisDecoder[T].decode(message).foreach(handler(pattern.toString, channel.toString, _))

    patternTopic(pattern).addListenerAsync(classOf[String], listener).asScala.map(_.intValue())
  }

}
