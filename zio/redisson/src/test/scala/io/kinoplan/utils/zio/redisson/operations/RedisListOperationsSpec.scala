package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.RedisClientSpec.redisClient
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import org.redisson.api.queue.DequeMoveArgs
import zio._
import zio.test._

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
        case1 <- redis.blMove[String](key1, timeout, DequeMoveArgs.pollLast().addFirstTo(key2))
        case2 <- redis.blMove[String](key1, timeout, DequeMoveArgs.pollFirst().addLastTo(key2))
        case3 <- redis.lRange[String](key1)
        case4 <- redis.lRange[String](key2)
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
        case1 <- redis.blmPopLeft[String](key1, timeout, Seq(key2), 10)
        case2 <- redis.lPush(key3, Seq("one", "two", "three", "four", "five"))
        case3 <- redis.blmPopLeft[String](key3, timeout)
        case4 <- redis.lRange[String](key3)
        case5 <- redis.blmPopRight[String](key3, timeout, count = 10)
        case6 <- redis.lPush(key3, Seq("one", "two", "three", "four", "five"))
        case7 <- redis.lPush(key4, Seq("a", "b", "c", "d", "e"))
        case8 <- redis.blmPopRight[String](key3, timeout, Seq(key4), 3)
        case9 <- redis.lRange[String](key3)
        case10 <- redis.blmPopRight[String](key3, timeout, Seq(key4), 5)
        case11 <- redis.blmPopRight[String](key3, timeout, Seq(key4), 10)
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
        case1 <- redis.rPush[String](key1, Seq("one", "two", "three", "four", "five"))
        case2 <- redis.blPop[String](key1)
        case3 <- redis.blPop[String](key1, timeout)
        case4 <- redis.blPop[String](key2, timeout, Seq(key1))
        case5 <- redis.lRange[String](key1)
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
        case1 <- redis.rPush[String](key1, Seq("one", "two", "three", "four", "five"))
        case2 <- redis.brPop[String](key1)
        case3 <- redis.brPop[String](key1, timeout)
        case4 <- redis.brPop[String](key2, timeout, Seq(key1))
        case5 <- redis.lRange[String](key1)
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
        case1 <- redis.brPopLPush[String](key1, key2, timeout)
        case2 <- redis.lRange[String](key1)
        case3 <- redis.lRange[String](key2)
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
        case1 <- redis.lIndex[String](key1, 0)
        case2 <- redis.lIndex[String](key1, -1)
        case3 <- redis.lIndex[String](key1, Seq(0, 1, 3))
        case4 <- redis.lIndex[String](key1, 3)
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
        case3 <- redis.lRange[String](key1)
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
        case1 <- redis.lMove[String](key1, DequeMoveArgs.pollLast().addFirstTo(key2))
        case2 <- redis.lMove[String](key1, DequeMoveArgs.pollFirst().addLastTo(key2))
        case3 <- redis.lRange[String](key1)
        case4 <- redis.lRange[String](key2)
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
        _ <- redis.rPush[String](key1, Seq("one", "two", "three", "four", "five"))
        case1 <- redis.lPop[String](key1)
        case2 <- redis.lPop[String](key1, 2)
        case3 <- redis.lRange[String](key1)
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
        _ <- redis.lPush[String](key1, "world")
        _ <- redis.lPush[String](key1, Seq("there", "hello"))
        case1 <- redis.lRange[String](key1)
      } yield assertTrue(case1 == Iterable("hello", "there", "world"))
    ),
    TestSpec(
      "lPushX",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.lPush[String](key1, "World")
        case1 <- redis.lPushX[String](key1, "Hello")
        case2 <- redis.lPushX[String](key1, Seq("Forever", "There"))
        case3 <- redis.lPushX[String](key2, "Hello")
        case4 <- redis.lRange[String](key1)
        case5 <- redis.lRange[String](key2)
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
        _ <- redis.rPush[String](key1, "one")
        _ <- redis.rPush[String](key1, "two")
        _ <- redis.rPush[String](key1, "three")
        case1 <- redis.lRange[String](key1, 0, 0)
        case2 <- redis.lRange[String](key1, 1)
        case3 <- redis.lRange[String](key1, -3, 2)
        case4 <- redis.lRange[String](key1, -3, 2)
        case5 <- redis.lRange[String](key1, -100, 100)
        case6 <- redis.lRange[String](key1, 5, 10)
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
        _ <- redis.rPush[String](key1, Seq("hello", "hello", "foo", "hello"))
        case1 <- redis.lRem(key1, -2, "hello")
        case2 <- redis.lRange[String](key1)
        _ <- redis.lPush[String](key1, Seq("hello", "hello"))
        case3 <- redis.lRem(key1, 1, "hello")
        case4 <- redis.lRange[String](key1)
        case5 <- redis.lRem(key1, 0, "hello")
        case6 <- redis.lRange[String](key1)
        _ <- redis.rPush[String](key1, Seq("hello", "hello"))
        _ <- redis.lRem(key1, 1)
        case7 <- redis.lRemAndReturn[String](key1, 1)
        case8 <- redis.lRange[String](key1)
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
        _ <- redis.rPush[String](key1, Seq("one", "two", "three"))
        _ <- redis.lSet(key1, 0, "four")
        case1 <- redis.lSetAndReturn(key1, -2, "five")
        case2 <- redis.lSetAndReturn(key1, 2, "six")
        case3 <- redis.lRange[String](key1)
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
        _ <- redis.rPush[String](key1, Seq("one", "two", "three"))
        _ <- redis.lTrim(key1, 1, -1)
        case1 <- redis.lRange[String](key1)
      } yield assertTrue(case1 == Iterable("two", "three"))
    ),
    TestSpec(
      "rPop",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.rPush[String](key1, Seq("one", "two", "three", "four", "five"))
        case1 <- redis.rPop[String](key1)
        case2 <- redis.rPop[String](key1, 2)
        case3 <- redis.lRange[String](key1)
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
        case1 <- redis.rPopLPush[String](key1, key2)
        case2 <- redis.lRange[String](key1)
        case3 <- redis.lRange[String](key2)
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
        _ <- redis.rPush[String](key1, "hello")
        _ <- redis.rPush[String](key1, Seq("there", "world"))
        case1 <- redis.lRange[String](key1)
      } yield assertTrue(case1 == Iterable("hello", "there", "world"))
    ),
    TestSpec(
      "rPushX",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.rPush[String](key1, "World")
        case1 <- redis.rPushX[String](key1, "Hello")
        case2 <- redis.rPushX[String](key1, Seq("Forever", "There"))
        case3 <- redis.rPushX[String](key2, "Hello")
        case4 <- redis.lRange[String](key1)
        case5 <- redis.lRange[String](key2)
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
