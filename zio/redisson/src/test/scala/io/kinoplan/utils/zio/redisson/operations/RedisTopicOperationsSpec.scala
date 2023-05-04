package io.kinoplan.utils.zio.redisson.operations

import zio._
import zio.test._

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.helpers.TestSpec

object RedisTopicOperationsSpec extends DefaultRedisCodecs {

  case class PTopicMessage(pattern: String, channel: String, message: String)

  object PTopicMessage {

    def handler: (String, String, String) => ZIO[Any, Throwable, Chunk[PTopicMessage]] =
      (pattern: String, channel: String, message: String) =>
        ZIO.attempt(PTopicMessage(pattern, channel, message)).map(Chunk.single)

  }

  case class TopicMessage(channel: String, message: String)

  object TopicMessage {

    def handler: (String, String) => ZIO[Any, Throwable, Chunk[TopicMessage]] =
      (channel: String, message: String) =>
        ZIO.attempt(TopicMessage(channel, message)).map(Chunk.single)

  }

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "pSubscribe",
      for {
        redis <- redisClient
        fiber <-
          redis.pSubscribe("h?llo")(PTopicMessage.handler).timeout(10.seconds).runCollect.fork
        _ <- ZIO.sleep(2.seconds)
        _ <- redis.publish("hxlo", "four")
        _ <- redis.publish("hxlo", "five")
        _ <- redis.publish("hello", "one")
        _ <- redis.publish("hallo", "two")
        _ <- redis.publish("hxllo", "three")
        _ <- ZIO.sleep(2.seconds)
        result <- fiber.join
      } yield assertTrue(
        result ==
          Chunk(
            PTopicMessage("h?llo", "hello", "one"),
            PTopicMessage("h?llo", "hallo", "two"),
            PTopicMessage("h?llo", "hxllo", "three")
          )
      )
    ),
    TestSpec(
      "publish",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.publish(key, "one")
        _ <- redis.subscribe(key)(TopicMessage.handler).timeout(10.seconds).runDrain.fork
        _ <- ZIO.sleep(2.seconds)
        case2 <- redis.publish(key, "two")
      } yield assertTrue(case1 == 0, case2 == 1)
    ),
    TestSpec(
      "pubSubNumSub",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.pubSubNumSub(key)
        _ <- redis.subscribe(key)(TopicMessage.handler).timeout(10.seconds).runDrain.fork
        _ <- ZIO.sleep(2.seconds)
        case2 <- redis.pubSubNumSub(key)
      } yield assertTrue(case1 == 0, case2 == 1)
    ),
    TestSpec(
      "pUnsubscribe",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.publish(key, "one")
        _ <- redis.pSubscribe(key)(PTopicMessage.handler).timeout(10.seconds).runDrain.fork
        _ <- ZIO.sleep(2.seconds)
        case2 <- redis.publish(key, "two")
        _ <- redis.pUnsubscribe(key)
        _ <- ZIO.sleep(2.seconds)
        case3 <- redis.publish(key, "three")
      } yield assertTrue(case1 == 0, case2 == 1, case3 == 0)
    ),
    TestSpec(
      "subscribe",
      for {
        redis <- redisClient
        fiber <- redis.subscribe("hello1")(TopicMessage.handler).timeout(10.seconds).runCollect.fork
        _ <- ZIO.sleep(2.seconds)
        _ <- redis.publish("hallo1", "five")
        _ <- redis.publish("hullo1", "four")
        _ <- redis.publish("hello1", "one")
        _ <- redis.publish("hello1", "two")
        _ <- redis.publish("hello1", "three")
        _ <- ZIO.sleep(2.seconds)
        result <- fiber.join
      } yield assertTrue(
        result ==
          Chunk(
            TopicMessage("hello1", "one"),
            TopicMessage("hello1", "two"),
            TopicMessage("hello1", "three")
          )
      )
    ),
    TestSpec(
      "unsubscribe",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.pubSubNumSub(key)
        _ <- redis.subscribe(key)(TopicMessage.handler).timeout(10.seconds).runDrain.fork
        _ <- ZIO.sleep(2.seconds)
        case2 <- redis.pubSubNumSub(key)
        _ <- redis.unsubscribe(key)
        _ <- ZIO.sleep(2.seconds)
        case3 <- redis.pubSubNumSub(key)
      } yield assertTrue(case1 == 0, case2 == 1, case3 == 0)
    )
  )

}
