package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.RedisClientSpec.redisClient
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import zio._
import zio.test._

import java.util.UUID

object RedisStringOperationsSpec extends DefaultRedisCodecs {
  private def generateKey = UUID.randomUUID().toString

  def tests: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "append",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.append(key, "Hello")
        _ <- redis.append(key, " World")
        result <- redis.get[String](key)
      } yield assertTrue(result.contains("Hello World"))
    ),
    TestSpec(
      "decr",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "10")
        result <- redis.decr(key)
      } yield assertTrue(result == 9)
    ),
    TestSpec(
      "decrBy",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "10")
        result <- redis.decrBy(key, 3)
      } yield assertTrue(result == 7)
    ),
    TestSpec(
      "get",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "Hello")
        result <- redis.get[String](key)
      } yield assertTrue(result.contains("Hello"))
    ),
    TestSpec(
      "getDel",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "Hello")
        result <- redis.getDel[String](key)
        postResult <- redis.get[String](key)
      } yield assertTrue(result.contains("Hello"), postResult.isEmpty)
    ),
    TestSpec(
      "getEx",
      for {
        redis <- redisClient
        key = generateKey
        duration = 60.seconds
        _ <- redis.set(key, "Hello")
        result <- redis.getEx[String](key, duration)
        ttl <- redis.ttl(key)
      } yield assertTrue(result.contains("Hello"), ttl > duration.minusSeconds(5).toMillis)
    ),
    TestSpec(
      "getExPersist",
      for {
        redis <- redisClient
        key = generateKey
        duration = 60.seconds
        _ <- redis.set(key, "Hello")
        resultGetEx <- redis.getEx[String](key, duration)
        ttlGetEx <- redis.ttl(key)
        resultGetExPersist <- redis.getExPersist[String](key, duration)
        ttlGetExPersist <- redis.ttl(key)
      } yield assertTrue(
        resultGetEx.contains("Hello"),
        ttlGetEx > duration.minusSeconds(5).toMillis,
        resultGetExPersist.contains("Hello"),
        ttlGetExPersist == -1
      )
    ),
    TestSpec(
      "getRange",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "This is a string")
        case1 <- redis.getRange[String](key, 0, 3)
        case2 <- redis.getRange[String](key, -3, -1)
        case3 <- redis.getRange[String](key, 0, -1)
        case4 <- redis.getRange[String](key, 10, 100)
      } yield assertTrue(
        case1.contains("This"),
        case2.contains("ing"),
        case3.contains("This is a string"),
        case4.contains("string")
      )
    ),
    TestSpec(
      "set",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "Hello")
        result <- redis.exists(key)
      } yield assertTrue(result)
    )
  )

}
