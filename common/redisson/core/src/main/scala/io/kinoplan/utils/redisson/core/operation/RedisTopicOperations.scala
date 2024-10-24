package io.kinoplan.utils.redisson.core.operation

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.{RPatternTopic, RTopic, RedissonClient}
import org.redisson.api.listener.{MessageListener, PatternMessageListener}
import org.redisson.client.codec.StringCodec

import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}

trait RedisTopicOperations {
  implicit protected val executionContext: ExecutionContext
  protected val redissonClient: RedissonClient

  private lazy val topic: String => RTopic = redissonClient.getTopic(_, StringCodec.INSTANCE)

  private lazy val patternTopic: String => RPatternTopic =
    redissonClient.getPatternTopic(_, StringCodec.INSTANCE)

  protected def publish[T: RedisEncoder](key: String, value: T): Future[Long] = Future {
    topic(key).publish(RedisEncoder[T].encode(value))
  }

  protected def subscribe[T: RedisDecoder](key: String)(handler: (String, T) => Unit): Future[Int] =
    Future {
      val listener: MessageListener[String] = (channel: CharSequence, message: String) =>
        RedisDecoder[T].decode(message).foreach(handler(channel.toString, _))

      topic(key).addListener(classOf[String], listener)
    }

  protected def pSubscribe[T: RedisDecoder](
    pattern: String
  )(handler: (String, String, T) => Unit): Future[Int] = Future {
    val listener: PatternMessageListener[String] =
      (pattern: CharSequence, channel: CharSequence, message: String) =>
        RedisDecoder[T].decode(message).foreach(handler(pattern.toString, channel.toString, _))

    patternTopic(pattern).addListener(classOf[String], listener)
  }

}
