package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.RedisClientSpec.redisClient
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import zio._
import zio.test._

object RedisStringOperationsSpec extends DefaultRedisCodecs {

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "append",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.append(key, "Hello")
        _ <- redis.append(key, " World")
        case1 <- redis.get[String](key)
      } yield assertTrue(case1.contains("Hello World"))
    ),
    TestSpec(
      "decr",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "10")
        case1 <- redis.decr(key)
      } yield assertTrue(case1 == 9)
    ),
    TestSpec(
      "decrBy",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "10")
        case1 <- redis.decrBy(key, 3)
      } yield assertTrue(case1 == 7)
    ),
    TestSpec(
      "get",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "Hello")
        case1 <- redis.get[String](key)
      } yield assertTrue(case1.contains("Hello"))
    ),
    TestSpec(
      "getDel",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "Hello")
        case1 <- redis.getDel[String](key)
        case2 <- redis.get[String](key)
      } yield assertTrue(case1.contains("Hello"), case2.isEmpty)
    ),
    TestSpec(
      "getEx",
      for {
        redis <- redisClient
        key = generateKey
        duration = 60.seconds
        _ <- redis.set(key, "Hello")
        case1 <- redis.getEx[String](key, duration)
        case2 <- redis.ttl(key)
      } yield assertTrue(case1.contains("Hello"), case2.exists(_ > duration.minusSeconds(5)))
    ),
    TestSpec(
      "getExPersist",
      for {
        redis <- redisClient
        key = generateKey
        duration = 60.seconds
        _ <- redis.set(key, "Hello")
        case1 <- redis.getEx[String](key, duration)
        case2 <- redis.ttl(key)
        case3 <- redis.getExPersist[String](key, duration)
        case4 <- redis.ttl(key)
      } yield assertTrue(
        case1.contains("Hello"),
        case2.exists(_ > duration.minusSeconds(5)),
        case3.contains("Hello"),
        case4.isEmpty
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
        case5 <- redis.getRange[String](key, -10, 100)
        case6 <- redis.getRange[String](key, 10, -100)
        case7 <- redis.getRange[String](key, -10, -100)
      } yield assertTrue(
        case1.contains("This"),
        case2.contains("ing"),
        case3.contains("This is a string"),
        case4.contains("string"),
        case5.contains("s a string"),
        case6.contains(""),
        case7.contains("")
      )
    ),
    TestSpec(
      "getSet",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "Hello")
        case1 <- redis.getSet(key, "World")
        case2 <- redis.get[String](key)
      } yield assertTrue(case1.contains("Hello"), case2.contains("World"))
    ),
    TestSpec(
      "incr",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, 10)
        case1 <- redis.incr(key)
        case2 <- redis.get[Long](key)
      } yield assertTrue(case1 == 11, case2.contains(11))
    ),
    TestSpec(
      "incrBy",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, 10)
        case1 <- redis.incrBy(key, 5)
      } yield assertTrue(case1 == 15)
    ),
    TestSpec(
      "incrByFloat",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, 10.50)
        case1 <- redis.incrByFloat(key, 0.1)
        case2 <- redis.incrByFloat(key, -5)
        _ <- redis.set(key, 5.0e3)
        case3 <- redis.incrByFloat(key, 2.0e2)
      } yield assertTrue(case1 == 10.6, case2 == 5.6, case3 == 5200)
    ),
    TestSpec(
      "mGet",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.set(key1, "Hello")
        _ <- redis.set(key2, "World")
        result <- redis.mGet[String](Seq(key1, key2, "nonexisting"))
      } yield assertTrue(result == Map((key1, "Hello"), (key2, "World")))
    ),
    TestSpec(
      "mSet",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.mSet[String](Map((key1, "Hello"), (key2, "World")))
        case1 <- redis.get[String](key1)
        case2 <- redis.get[String](key2)
      } yield assertTrue(case1.contains("Hello"), case2.contains("World"))
    ),
    TestSpec(
      "mSetNx",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        case1 <- redis.mSetNx[String](Map((key1, "Hello"), (key2, "there")))
        case2 <- redis.mSetNx[String](Map((key2, "new"), (key3, "world")))
        case3 <- redis.mGet[String](Seq(key1, key2, key3))
      } yield assertTrue(case1, !case2, case3 == Map((key1, "Hello"), (key2, "there")))
    ),
    TestSpec(
      "pSetEx",
      for {
        redis <- redisClient
        key = generateKey
        duration = 60.seconds
        _ <- redis.pSetEx(key, duration, "Hello")
        case1 <- redis.pTtl(key)
        case2 <- redis.get[String](key)
      } yield assertTrue(case1.exists(_ > duration.minusSeconds(5)), case2.contains("Hello"))
    ),
    TestSpec(
      "set",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "Hello")
        result <- redis.exists(key)
      } yield assertTrue(result)
    ),
    TestSpec(
      "setKeepTtl",
      for {
        redis <- redisClient
        key = generateKey
        duration = 60.seconds
        _ <- redis.set(key, "Hello")
        _ <- redis.expire(key, duration)
        case1 <- redis.ttl(key)
        _ <- redis.set(key, "Hello")
        case2 <- redis.ttl(key)
        _ <- redis.expire(key, duration)
        _ <- redis.setKeepTtl(key, "Hello")
        case3 <- redis.ttl(key)
        case4 <- redis.get[String](key)
      } yield assertTrue(
        case1.exists(_ > duration.minusSeconds(5)),
        case2.isEmpty,
        case3.exists(_ > duration.minusSeconds(5)),
        case4.contains("Hello")
      )
    ),
    TestSpec(
      "setEx",
      for {
        redis <- redisClient
        key = generateKey
        duration = 60.seconds
        _ <- redis.setEx(key, duration, "Hello")
        case1 <- redis.ttl(key)
      } yield assertTrue(case1.exists(_ > duration.minusSeconds(5)))
    ),
    TestSpec(
      "setNx",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.setNx(key, "Hello")
        case2 <- redis.setNx(key, "World")
        case3 <- redis.get[String](key)
      } yield assertTrue(case1, !case2, case3.contains("Hello"))
    ),
    TestSpec(
      "setNxEx",
      for {
        redis <- redisClient
        key = generateKey
        duration = 60.seconds
        case1 <- redis.setNxEx(key, duration, "Hello")
        case2 <- redis.setNxEx(key, 45.seconds, "World")
        case3 <- redis.ttl(key)
        case4 <- redis.get[String](key)
      } yield assertTrue(
        case1,
        !case2,
        case3.exists(_ > duration.minusSeconds(5)),
        case4.contains("Hello")
      )
    ),
    TestSpec(
      "setXx",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        case1 <- redis.setXx(key1, "Hello")
        case2 <- redis.get[String](key1)
        _ <- redis.set(key2, "Hello")
        case3 <- redis.setXx(key2, "World")
        case4 <- redis.get[String](key2)
      } yield assertTrue(!case1, case2.isEmpty, case3, case4.contains("World"))
    ),
    TestSpec(
      "setXxEx",
      for {
        redis <- redisClient
        key = generateKey
        duration = 60.seconds
        case1 <- redis.setXxEx(key, duration, "Hello")
        _ <- redis.set(key, "Hello")
        case2 <- redis.setXxEx(key, duration, "World")
        case3 <- redis.ttl(key)
        case4 <- redis.get[String](key)
      } yield assertTrue(
        !case1,
        case2,
        case3.exists(_ > duration.minusSeconds(5)),
        case4.contains("World")
      )
    ),
    TestSpec(
      "setRange",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        replaced = "Redis"
        _ <- redis.set(key1, "Hello World")
        case1 <- redis.setRange(key1, 6, replaced)
        case2 <- redis.get[String](key1)
        case3 <- redis.setRange(key2, 6, replaced)
        case4 <- redis.get[String](key2)
      } yield assertTrue(
        case1 == 11,
        case2.contains("Hello Redis"),
        case3 == 11,
        case4.contains(replaced.reverse.padTo(replaced.length + 6, '\u0000').reverse)
      )
    ),
    TestSpec(
      "strLen",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "Hello world")
        case1 <- redis.strLen(key)
        case2 <- redis.strLen("nonexisting")
      } yield assertTrue(case1 == 11, case2 == 0)
    )
  )

}
