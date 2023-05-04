package io.kinoplan.utils.zio.redisson.operations

import java.time.OffsetDateTime

import org.redisson.api.RType
import org.redisson.api.options.KeysScanOptions
import zio._
import zio.test._

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.helpers.TestSpec

object RedisGenericOperationsSpec extends DefaultRedisCodecs {

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "copy",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.set(key1, "sheep")
        case1 <- redis.copy(key1, key2)
        case2 <- redis.get(key2).as[String]
        _ <- redis.set(key3, "one")
        case3 <- redis.copy(key2, key3)
        case4 <- redis.get(key3).as[String]
        case5 <- redis.copy(key2, key3, 1)
      } yield assertTrue(case1, case2.contains("sheep"), !case3, case4.contains("one"), case5)
    ),
    TestSpec(
      "copyReplace",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.set(key1, "sheep")
        case1 <- redis.copyReplace(key1, key2)
        case2 <- redis.get(key2).as[String]
        _ <- redis.set(key3, "one")
        case3 <- redis.copyReplace(key2, key3)
        case4 <- redis.get(key3).as[String]
        case5 <- redis.copyReplace(key2, key3, 1)
      } yield assertTrue(case1, case2.contains("sheep"), case3, case4.contains("sheep"), case5)
    ),
    TestSpec(
      "del",
      for {
        redis <- redisClient
        (key1, key2, key3, key4) = (generateKey, generateKey, generateKey, generateKey)
        _ <- redis.set(key1, "Hello")
        _ <- redis.set(key2, "World")
        _ <- redis.set(key3, "Forever")
        case1 <- redis.del(key1)
        case2 <- redis.del(Seq(key2, key3, key4))
      } yield assertTrue(case1, case2 == 2)
    ),
    TestSpec(
      "dump",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.set(key1, 10)
        case1 <- redis.dump(key1)
      } yield assertTrue(
        case1.sameElements(Array[Byte](0, 3, 3, 49, -80, 12, 0, -105, 45, 65, -96, 49, -12, -78, 126))
      )
    ),
    TestSpec(
      "exists",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.set(key1, "Hello")
        case1 <- redis.exists(key1)
        case2 <- redis.exists(key2)
      } yield assertTrue(case1, !case2)
    ),
    TestSpec(
      "expire",
      for {
        redis <- redisClient
        key1 = generateKey
        timeout = 60.seconds
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expire(key1, timeout)
        case2 <- redis.ttl(key1)
        _ <- redis.set(key1, "Hello World")
        case3 <- redis.ttl(key1)
      } yield assertTrue(
        case1,
        case2.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5)),
        case3.isEmpty
      )
    ),
    TestSpec(
      "expireNx",
      for {
        redis <- redisClient
        key1 = generateKey
        timeout = 60.seconds
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expireNx(key1, timeout)
        case2 <- redis.expireNx(key1, 1.hours)
        case3 <- redis.ttl(key1)
      } yield assertTrue(
        case1,
        !case2,
        case3.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5))
      )
    ),
    TestSpec(
      "expireXx",
      for {
        redis <- redisClient
        key1 = generateKey
        timeout = 60.seconds
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expire(key1, 1.hours)
        case2 <- redis.expireXx(key1, timeout)
        case3 <- redis.ttl(key1)
      } yield assertTrue(
        case1,
        case2,
        case3.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5))
      )
    ),
    TestSpec(
      "expireGt",
      for {
        redis <- redisClient
        key1 = generateKey
        timeout = 60.seconds
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expire(key1, 30.seconds)
        case2 <- redis.expireGt(key1, timeout)
        case3 <- redis.ttl(key1)
      } yield assertTrue(
        case1,
        case2,
        case3.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5))
      )
    ),
    TestSpec(
      "expireLt",
      for {
        redis <- redisClient
        key1 = generateKey
        timeout = 60.seconds
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expire(key1, 1.hours)
        case2 <- redis.expireLt(key1, timeout)
        case3 <- redis.ttl(key1)
      } yield assertTrue(
        case1,
        case2,
        case3.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5))
      )
    ),
    TestSpec(
      "expireAt",
      for {
        redis <- redisClient
        key1 = generateKey
        time = OffsetDateTime.now().plusDays(1).toInstant
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expireAt(key1, time)
        case2 <- redis.expireTime(key1)
      } yield assertTrue(case1, case2.map(_.getEpochSecond).contains(time.getEpochSecond))
    ),
    TestSpec(
      "expireAtNx",
      for {
        redis <- redisClient
        key1 = generateKey
        time = OffsetDateTime.now().plusDays(1).toInstant
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expireAtNx(key1, time)
        case2 <- redis.expireTime(key1)
      } yield assertTrue(case1, case2.map(_.getEpochSecond).contains(time.getEpochSecond))
    ),
    TestSpec(
      "expireAtXx",
      for {
        redis <- redisClient
        key1 = generateKey
        time = OffsetDateTime.now().plusDays(2)
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expireAtXx(key1, time.toInstant)
        case2 <- redis.expireAt(key1, time.minusDays(1).toInstant)
        case3 <- redis.expireAtXx(key1, time.toInstant)
        case4 <- redis.expireTime(key1)
      } yield assertTrue(
        !case1,
        case2,
        case3,
        case4.map(_.getEpochSecond).contains(time.toInstant.getEpochSecond)
      )
    ),
    TestSpec(
      "expireAtGt",
      for {
        redis <- redisClient
        key1 = generateKey
        time = OffsetDateTime.now().plusDays(2)
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expireAt(key1, time.minusDays(1).toInstant)
        case2 <- redis.expireAtGt(key1, time.toInstant)
        case3 <- redis.expireTime(key1)
      } yield assertTrue(
        case1,
        case2,
        case3.map(_.getEpochSecond).contains(time.toInstant.getEpochSecond)
      )
    ),
    TestSpec(
      "expireAtLt",
      for {
        redis <- redisClient
        key1 = generateKey
        time = OffsetDateTime.now().plusDays(1)
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expireAt(key1, time.plusDays(1).toInstant)
        case2 <- redis.expireAtLt(key1, time.toInstant)
        case3 <- redis.expireTime(key1)
      } yield assertTrue(
        case1,
        case2,
        case3.map(_.getEpochSecond).contains(time.toInstant.getEpochSecond)
      )
    ),
    TestSpec(
      "expireTime",
      for {
        redis <- redisClient
        key1 = generateKey
        time = OffsetDateTime.now().plusDays(1).toInstant
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expireAt(key1, time)
        case2 <- redis.expireTime(key1)
      } yield assertTrue(case1, case2.map(_.getEpochSecond).contains(time.getEpochSecond))
    ),
    TestSpec(
      "keys",
      for {
        redis <- redisClient
        keys = (1 to 20).map(_ => generateKey).toSet
        _ <- ZIO.foreachDiscard(keys)(redis.set(_, "Hello"))
        case1 <- redis.keys()
        (key1, key2) = ("lastname", "firstname")
        _ <- redis.set(key1, "Hello")
        _ <- redis.set(key2, "Hello")
        case2 <- redis.keys(KeysScanOptions.defaults().limit(10))
        case3 <-
          redis.keys(KeysScanOptions.defaults().pattern("*name*").limit(1).`type`(RType.OBJECT))
        (key3, key4) = ("lastname1", "firstname1")
        _ <- redis.hSet(key3, Map(("field1", "hello"), ("field2", "world")))
        _ <- redis.hSet(key4, Map(("field1", "hello"), ("field2", "world")))
        case4 <- redis.keys(KeysScanOptions.defaults().pattern("*name*").`type`(RType.MAP))
      } yield assertTrue(
        case1.toSet.intersect(keys).size == 20,
        case2.size == 10,
        case3.size == 1,
        case3.headOption.exists(current => current == key1 || current == key2),
        case4.toSet == Set(key3, key4)
      )
    ),
    TestSpec(
      "move",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.set(key1, "sheep")
        case1 <- redis.move(key1, 1)
      } yield assertTrue(case1)
    ),
    TestSpec(
      "persist",
      for {
        redis <- redisClient
        key1 = generateKey
        timeout = 60.seconds
        _ <- redis.set(key1, "Hello")
        _ <- redis.expire(key1, timeout)
        case1 <- redis.ttl(key1)
        case2 <- redis.persist(key1)
        case3 <- redis.ttl(key1)
      } yield assertTrue(case1.nonEmpty, case2, case3.isEmpty)
    ),
    TestSpec(
      "randomKey",
      for {
        redis <- redisClient
        case1 <- redis.randomKey()
      } yield assertTrue(case1.nonEmpty)
    ),
    TestSpec(
      "rename",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.set(key1, "Hello")
        _ <- redis.rename(key1, key2)
        case2 <- redis.get(key2).as[String]
      } yield assertTrue(case2.contains("Hello"))
    ),
    TestSpec(
      "renameNx",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.set(key1, "Hello")
        _ <- redis.set(key2, "World")
        case1 <- redis.renameNx(key1, key2)
        case2 <- redis.get(key2).as[String]
      } yield assertTrue(!case1, case2.contains("World"))
    ),
    TestSpec(
      "restore",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.sAdd(key1, Set("1", "2", "3"))
        state <- redis.dump(key1)
        case1 <- redis.del(key1)
        _ <- redis.restore(key1, state)
        case2 <- redis.getType(key1)
        case3 <- redis.sMembers(key1).as[String]
      } yield assertTrue(case1, case2 == RType.SET, case3 == Set("1", "2", "3"))
    ),
    TestSpec(
      "restore (with ttl)",
      for {
        redis <- redisClient
        key1 = generateKey
        timeout = 60.seconds
        _ <- redis.sAdd(key1, Set("1", "2", "3"))
        state <- redis.dump(key1)
        case1 <- redis.del(key1)
        _ <- redis.restore(key1, timeout, state)
        case2 <- redis.getType(key1)
        case3 <- redis.sMembers(key1).as[String]
        case4 <- redis.ttl(key1)
      } yield assertTrue(
        case1,
        case2 == RType.SET,
        case3 == Set("1", "2", "3"),
        case4.exists(ttl => ttl < timeout && ttl > timeout.minusSeconds(10))
      )
    ),
    TestSpec(
      "restoreAndReplace",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.sAdd(key1, Set("1", "2", "3"))
        state <- redis.dump(key1)
        _ <- redis.restoreAndReplace(key1, state)
        case1 <- redis.getType(key1)
        case2 <- redis.sMembers(key1).as[String]
      } yield assertTrue(case1 == RType.SET, case2 == Set("1", "2", "3"))
    ),
    TestSpec(
      "restoreAndReplace (with ttl)",
      for {
        redis <- redisClient
        key1 = generateKey
        timeout = 60.seconds
        _ <- redis.sAdd(key1, Set("1", "2", "3"))
        state <- redis.dump(key1)
        _ <- redis.restoreAndReplace(key1, timeout, state)
        case1 <- redis.getType(key1)
        case2 <- redis.sMembers(key1).as[String]
        case3 <- redis.ttl(key1)
      } yield assertTrue(
        case1 == RType.SET,
        case2 == Set("1", "2", "3"),
        case3.exists(ttl => ttl < timeout && ttl > timeout.minusSeconds(10))
      )
    ),
    TestSpec(
      "scan",
      for {
        redis <- redisClient
        keys = (1 to 20).map(_ => generateKey).toSet
        _ <- ZIO.foreachDiscard(keys)(redis.set(_, "Hello"))
        case1 <- redis.scan().runCollect
        (key1, key2) = ("lastname", "firstname")
        _ <- redis.set(key1, "Hello")
        _ <- redis.set(key2, "Hello")
        case2 <- redis.scan(KeysScanOptions.defaults().limit(10)).runCollect
        case3 <- redis
          .scan(KeysScanOptions.defaults().pattern("*name*").limit(1).`type`(RType.OBJECT))
          .runCollect
        (key3, key4) = ("lastname1", "firstname1")
        _ <- redis.hSet(key3, Map(("field1", "hello"), ("field2", "world")))
        _ <- redis.hSet(key4, Map(("field1", "hello"), ("field2", "world")))
        case4 <-
          redis.scan(KeysScanOptions.defaults().pattern("*name*").`type`(RType.MAP)).runCollect
      } yield assertTrue(
        case1.toSet.intersect(keys).size == 20,
        case2.size == 10,
        case3.size == 1,
        case3.headOption.exists(current => current == key1 || current == key2),
        case4.toSet == Set(key3, key4)
      )
    ),
    TestSpec(
      "touch",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.set(key1, "Hello")
        _ <- redis.set(key2, "World")
        case1 <- redis.touch(key1)
        case2 <- redis.touch(key2)
        case3 <- redis.touch(key3)
      } yield assertTrue(case1, case2, !case3)
    ),
    TestSpec(
      "ttl",
      for {
        redis <- redisClient
        key1 = generateKey
        timeout = 60.seconds
        _ <- redis.set(key1, "Hello")
        case1 <- redis.expire(key1, timeout)
        case2 <- redis.ttl(key1)
      } yield assertTrue(case1, case2.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5)))
    ),
    TestSpec(
      "getType",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.set(key1, "value")
        _ <- redis.lPush(key2, "value")
        _ <- redis.sAdd(key3, "value")
        case1 <- redis.getType(key1)
        case2 <- redis.getType(key2)
        case3 <- redis.getType(key3)
      } yield assertTrue(case1 == RType.OBJECT, case2 == RType.LIST, case3 == RType.SET)
    ),
    TestSpec(
      "unlink",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.set(key1, "Hello")
        _ <- redis.set(key2, "World")
        case1 <- redis.unlink(key1)
        case2 <- redis.unlink(key2)
        case3 <- redis.unlink(key3)
      } yield assertTrue(case1, case2, !case3)
    )
  )

}
