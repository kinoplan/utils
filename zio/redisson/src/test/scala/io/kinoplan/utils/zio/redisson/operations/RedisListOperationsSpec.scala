package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.queue.DequeMoveArgs
import zio._
import zio.test._

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.helpers.TestSpec

object RedisListOperationsSpec extends DefaultRedisCodecs {

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "blMove",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        timeout = 1.seconds
        _ <- redis.rPush(key1, "one")
        _ <- redis.rPush(key1, "two")
        _ <- redis.rPush(key1, "three")
        case1 <- redis.blMove(key1, timeout, DequeMoveArgs.pollLast().addFirstTo(key2)).as[String]
        case2 <- redis.blMove(key1, timeout, DequeMoveArgs.pollFirst().addLastTo(key2)).as[String]
        case3 <- redis.lRange(key1).as[String]
        case4 <- redis.lRange(key2).as[String]
      } yield assertTrue(
        case1 == "three",
        case2 == "one",
        case3 == Seq("two"),
        case4 == Seq("three", "one")
      )
    ),
    TestSpec(
      "blmPop",
      for {
        redis <- redisClient
        (key1, key2, key3, key4) = (generateKey, generateKey, generateKey, generateKey)
        timeout = 1.seconds
        case1 <- redis.blmPopLeft(key1, timeout, Seq(key2), 10).as[String]
        case2 <- redis.lPush(key3, Seq("one", "two", "three", "four", "five"))
        case3 <- redis.blmPopLeft(key3, timeout).as[String]
        case4 <- redis.lRange(key3).as[String]
        case5 <- redis.blmPopRight(key3, timeout, count = 10).as[String]
        case6 <- redis.lPush(key3, Seq("one", "two", "three", "four", "five"))
        case7 <- redis.lPush(key4, Seq("a", "b", "c", "d", "e"))
        case8 <- redis.blmPopRight(key3, timeout, Seq(key4), 3).as[String]
        case9 <- redis.lRange(key3).as[String]
        case10 <- redis.blmPopRight(key3, timeout, Seq(key4), 5).as[String]
        case11 <- redis.blmPopRight(key3, timeout, Seq(key4), 10).as[String]
        case12 <- redis.exists(key3)
        case13 <- redis.exists(key4)
      } yield assertTrue(
        case1 == Map.empty[String, List[String]],
        case2 == 5,
        case3 == Map((key3, List("five"))),
        case4 == Iterable("four", "three", "two", "one"),
        case5 == Map((key3, List("one", "two", "three", "four"))),
        case6 == 5,
        case7 == 5,
        case8 == Map((key3, List("one", "two", "three"))),
        case9 == Iterable("five", "four"),
        case10 == Map((key3, List("four", "five"))),
        case11 == Map((key4, List("a", "b", "c", "d", "e"))),
        !case12,
        !case13
      )
    ),
    TestSpec(
      "blPop",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        timeout = 1.seconds
        case1 <- redis.rPush(key1, Seq("one", "two", "three", "four", "five"))
        case2 <- redis.blPop(key1).as[String]
        case3 <- redis.blPop(key1, timeout).as[String]
        case4 <- redis.blPop(key2, timeout, Seq(key1)).as[String]
        case5 <- redis.lRange(key1).as[String]
      } yield assertTrue(
        case1 == 5,
        case2.contains("one"),
        case3.contains("two"),
        case4.contains("three"),
        case5 == Iterable("four", "five")
      )
    ),
    TestSpec(
      "brPop",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        timeout = 1.seconds
        case1 <- redis.rPush(key1, Seq("one", "two", "three", "four", "five"))
        case2 <- redis.brPop(key1).as[String]
        case3 <- redis.brPop(key1, timeout).as[String]
        case4 <- redis.brPop(key2, timeout, Seq(key1)).as[String]
        case5 <- redis.lRange(key1).as[String]
      } yield assertTrue(
        case1 == 5,
        case2.contains("five"),
        case3.contains("four"),
        case4.contains("three"),
        case5 == Iterable("one", "two")
      )
    ),
    TestSpec(
      "brPopLPush",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        timeout = 1.seconds
        _ <- redis.rPush(key1, "one")
        _ <- redis.rPush(key1, "two")
        _ <- redis.rPush(key1, "three")
        case1 <- redis.brPopLPush(key1, key2, timeout).as[String]
        case2 <- redis.lRange(key1).as[String]
        case3 <- redis.lRange(key2).as[String]
      } yield assertTrue(
        case1.contains("three"),
        case2 == Iterable("one", "two"),
        case3 == Iterable("three")
      )
    ),
    TestSpec(
      "lIndex",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.lPush(key1, "World")
        _ <- redis.lPush(key1, "Hello")
        case1 <- redis.lIndex(key1, 0).as[String]
        case2 <- redis.lIndex(key1, -1).as[String]
        case3 <- redis.lIndex(key1, Seq(0, 1, 3)).as[String]
        case4 <- redis.lIndex(key1, 3).as[String]
      } yield assertTrue(
        case1.contains("Hello"),
        case2.contains("World"),
        case3 == Iterable(Some("Hello"), Some("World"), None),
        case4.isEmpty
      )
    ),
    TestSpec(
      "lInsert",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.rPush(key1, "Hello")
        _ <- redis.rPush(key1, "World")
        case1 <- redis.lInsertBefore(key1, "World", "There")
        case2 <- redis.lInsertAfter(key1, "World", "Forever")
        case3 <- redis.lRange(key1).as[String]
      } yield assertTrue(
        case1 == 3,
        case2 == 4,
        case3 == Iterable("Hello", "There", "World", "Forever")
      )
    ),
    TestSpec(
      "lLen",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.lPush(key1, "World")
        _ <- redis.lPush(key1, "Hello")
        case1 <- redis.lLen(key1)
      } yield assertTrue(case1 == 2)
    ),
    TestSpec(
      "lMove",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.rPush(key1, Seq("one"))
        _ <- redis.rPush(key1, Seq("two"))
        _ <- redis.rPush(key1, Seq("three"))
        case1 <- redis.lMove(key1, DequeMoveArgs.pollLast().addFirstTo(key2)).as[String]
        case2 <- redis.lMove(key1, DequeMoveArgs.pollFirst().addLastTo(key2)).as[String]
        case3 <- redis.lRange(key1).as[String]
        case4 <- redis.lRange(key2).as[String]
      } yield assertTrue(
        case1 == "three",
        case2 == "one",
        case3 == Seq("two"),
        case4 == Seq("three", "one")
      )
    ),
    TestSpec(
      "lPop",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.rPush(key1, Seq("one", "two", "three", "four", "five"))
        case1 <- redis.lPop(key1).as[String]
        case2 <- redis.lPop(key1, 2).as[String]
        case3 <- redis.lRange(key1).as[String]
      } yield assertTrue(
        case1.contains("one"),
        case2 == Iterable("two", "three"),
        case3 == Iterable("four", "five")
      )
    ),
    TestSpec(
      "lPush",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.lPush(key1, "world")
        _ <- redis.lPush(key1, Seq("there", "hello"))
        case1 <- redis.lRange(key1).as[String]
      } yield assertTrue(case1 == Iterable("hello", "there", "world"))
    ),
    TestSpec(
      "lPushX",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.lPush(key1, "World")
        case1 <- redis.lPushX(key1, "Hello")
        case2 <- redis.lPushX(key1, Seq("Forever", "There"))
        case3 <- redis.lPushX(key2, "Hello")
        case4 <- redis.lRange(key1).as[String]
        case5 <- redis.lRange(key2).as[String]
      } yield assertTrue(
        case1 == 2,
        case2 == 4,
        case3 == 0,
        case4 == Iterable("There", "Forever", "Hello", "World"),
        case5 == Nil
      )
    ),
    TestSpec(
      "lRange",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.rPush(key1, "one")
        _ <- redis.rPush(key1, "two")
        _ <- redis.rPush(key1, "three")
        case1 <- redis.lRange(key1, 0, 0).as[String]
        case2 <- redis.lRange(key1, 1).as[String]
        case3 <- redis.lRange(key1, -3, 2).as[String]
        case4 <- redis.lRange(key1, -3, 2).as[String]
        case5 <- redis.lRange(key1, -100, 100).as[String]
        case6 <- redis.lRange(key1, 5, 10).as[String]
      } yield assertTrue(
        case1 == Iterable("one"),
        case2 == Iterable("one", "two"),
        case3 == Iterable("one", "two", "three"),
        case4 == Iterable("one", "two", "three"),
        case5 == Iterable("one", "two", "three"),
        case6 == Nil
      )
    ),
    TestSpec(
      "lRem",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.rPush(key1, Seq("hello", "hello", "foo", "hello"))
        case1 <- redis.lRem(key1, -2, "hello")
        case2 <- redis.lRange[String](key1).as[String]
        _ <- redis.lPush(key1, Seq("hello", "hello"))
        case3 <- redis.lRem(key1, 1, "hello")
        case4 <- redis.lRange(key1).as[String]
        case5 <- redis.lRem(key1, 0, "hello")
        case6 <- redis.lRange(key1).as[String]
        _ <- redis.rPush(key1, Seq("hello", "hello"))
        _ <- redis.lRem(key1, 1)
        case7 <- redis.lRemAndReturn(key1, 1).as[String]
        case8 <- redis.lRange(key1).as[String]
      } yield assertTrue(
        case1,
        case2 == Iterable("hello", "foo"),
        case3,
        case4 == Iterable("hello", "hello", "foo"),
        case5,
        case6 == Iterable("foo"),
        case7.contains("hello"),
        case8 == Iterable("foo")
      )
    ),
    TestSpec(
      "lSet",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.rPush(key1, Seq("one", "two", "three"))
        _ <- redis.lSet(key1, 0, "four")
        case1 <- redis.lSetAndReturn(key1, -2, "five")
        case2 <- redis.lSetAndReturn(key1, 2, "six")
        case3 <- redis.lRange(key1).as[String]
      } yield assertTrue(
        case1.contains("two"),
        case2.contains("three"),
        case3 == Iterable("four", "five", "six")
      )
    ),
    TestSpec(
      "lTrim",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.rPush(key1, Seq("one", "two", "three"))
        _ <- redis.lTrim(key1, 1, -1)
        case1 <- redis.lRange(key1).as[String]
      } yield assertTrue(case1 == Iterable("two", "three"))
    ),
    TestSpec(
      "rPop",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.rPush(key1, Seq("one", "two", "three", "four", "five"))
        case1 <- redis.rPop(key1).as[String]
        case2 <- redis.rPop(key1, 2).as[String]
        case3 <- redis.lRange(key1).as[String]
      } yield assertTrue(
        case1.contains("five"),
        case2 == Iterable("four", "three"),
        case3 == Iterable("one", "two")
      )
    ),
    TestSpec(
      "rPopLPush",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.rPush(key1, "one")
        _ <- redis.rPush(key1, "two")
        _ <- redis.rPush(key1, "three")
        case1 <- redis.rPopLPush(key1, key2).as[String]
        case2 <- redis.lRange(key1).as[String]
        case3 <- redis.lRange(key2).as[String]
      } yield assertTrue(
        case1.contains("three"),
        case2 == Iterable("one", "two"),
        case3 == Iterable("three")
      )
    ),
    TestSpec(
      "rPush",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.rPush(key1, "hello")
        _ <- redis.rPush(key1, Seq("there", "world"))
        case1 <- redis.lRange(key1).as[String]
      } yield assertTrue(case1 == Iterable("hello", "there", "world"))
    ),
    TestSpec(
      "rPushX",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.rPush(key1, "World")
        case1 <- redis.rPushX(key1, "Hello")
        case2 <- redis.rPushX(key1, Seq("Forever", "There"))
        case3 <- redis.rPushX(key2, "Hello")
        case4 <- redis.lRange(key1).as[String]
        case5 <- redis.lRange(key2).as[String]
      } yield assertTrue(
        case1 == 2,
        case2 == 4,
        case3 == 0,
        case4 == Iterable("World", "Hello", "Forever", "There"),
        case5 == Nil
      )
    )
  )

}
