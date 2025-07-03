package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.RedisClientSpec.redisClient
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import org.redisson.api.StreamMessageId
import org.redisson.api.stream.{StreamCreateGroupArgs, StreamReadGroupArgs}
import zio._
import zio.test._

object RedisStreamOperationsSpec extends DefaultRedisCodecs {

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "xAck",
      for {
        redis <- redisClient
        key = generateKey
        (group, consumer) = (generateKey, generateKey)
        createGroupArgs = StreamCreateGroupArgs.name(group).id(StreamMessageId.ALL).makeStream()
        _ <- redis.xGroupCreate(key, createGroupArgs)
        id1 <- redis.xAdd(key, "field", "Hello,")
        id2 <- redis.xAdd(key, "field", " World!")
        ids = NonEmptyChunk(id1, id2)
        case1 <- redis.xAck(key, group, ids)
        _ <- redis.xReadGroup[String](
          key,
          group,
          consumer,
          StreamReadGroupArgs.greaterThan(StreamMessageId.NEVER_DELIVERED)
        )
        case2 <- redis.xAck(key, group, ids)
      } yield assertTrue(case1 == 0, case2 == 2)
    )
  )

}
