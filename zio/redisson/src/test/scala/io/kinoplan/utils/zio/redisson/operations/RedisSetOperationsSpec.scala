package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.RedisClientSpec.redisClient
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import zio._
import zio.test._

object RedisSetOperationsSpec extends DefaultRedisCodecs {

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "sAdd",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.sAdd(key, "Hello")
        case2 <- redis.sAdd(key, Seq("World", "There"))
        case3 <- redis.sAdd(key, "Hello")
        case4 <- redis.sMembers[String](key)
      } yield assertTrue(case1, case2, !case3, case4 == Set("Hello", "World", "There"))
    ),
    TestSpec(
      "sCard",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.sAdd(key, "Hello")
        _ <- redis.sAdd(key, "World")
        case1 <- redis.sCard(key)
      } yield assertTrue(case1 == 2)
    ),
    TestSpec(
      "sDiff",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.sAdd(key1, Seq("a", "b", "c"))
        _ <- redis.sAdd(key2, Seq("c", "d", "e"))
        case1 <- redis.sDiff[String](key1, Seq(key2))
      } yield assertTrue(case1 == Set("a", "b"))
    ),
    TestSpec(
      "sDiffStore",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.sAdd(key1, Seq("a", "b", "c"))
        _ <- redis.sAdd(key2, Seq("c", "d", "e"))
        case1 <- redis.sDiffStore(key3, Seq(key1, key2))
        case2 <- redis.sMembers[String](key3)
      } yield assertTrue(case1 == 2, case2 == Set("a", "b"))
    ),
    TestSpec(
      "sInter",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.sAdd(key1, Seq("a", "b", "c"))
        _ <- redis.sAdd(key2, Seq("c", "d", "e"))
        case1 <- redis.sInter[String](key1, Seq(key2))
      } yield assertTrue(case1 == Set("c"))
    ),
    TestSpec(
      "sInterStore",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.sAdd(key1, Seq("a", "b", "c"))
        _ <- redis.sAdd(key2, Seq("c", "d", "e"))
        case1 <- redis.sInterStore(key3, Seq(key1, key2))
        case2 <- redis.sMembers[String](key3)
      } yield assertTrue(case1 == 1, case2 == Set("c"))
    ),
    TestSpec(
      "sIsMember",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.sAdd(key, "one")
        case1 <- redis.sIsMember(key, "one")
        case2 <- redis.sIsMember(key, "two")
      } yield assertTrue(case1, !case2)
    ),
    TestSpec(
      "sMembers",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.sAdd(key, "Hello")
        _ <- redis.sAdd(key, "World")
        case1 <- redis.sMembers[String](key)
      } yield assertTrue(case1 == Set("Hello", "World"))
    ),
    TestSpec(
      "sMove",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.sAdd(key1, Seq("one", "two"))
        _ <- redis.sAdd(key2, "three")
        case1 <- redis.sMove[String](key1, key2, "two")
        case2 <- redis.sMembers[String](key1)
        case3 <- redis.sMembers[String](key2)
      } yield assertTrue(case1, case2 == Set("one"), case3 == Set("three", "two"))
    ),
    TestSpec(
      "sPop",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.sAdd(key1, Seq("one", "two", "three"))
        case1 <- redis.sPop[String](key1)
        case2 <- redis.sMembers[String](key1)
        _ <- redis.sAdd(key1, Seq("four", "five"))
        case3 <- redis.sPop[String](key1, 3)
        case4 <- redis.sMembers[String](key1)
      } yield assertTrue(case1.nonEmpty, case2.size == 2, case3.size == 3, case4.size == 1)
    ),
    TestSpec(
      "sRandMember",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.sAdd(key1, Seq("one", "two", "three"))
        case1 <- redis.sRandMember[String](key1)
        case2 <- redis.sRandMember[String](key1, 2)
        case3 <- redis.sRandMember[String](key1, -5)
      } yield assertTrue(case1.nonEmpty, case2.size == 2, case3.nonEmpty)
    ),
    TestSpec(
      "sRem",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.sAdd(key1, Seq("one", "two", "three", "four", "five"))
        case1 <- redis.sRem[String](key1, "one")
        case2 <- redis.sRem[String](key1, Seq("three", "four"))
        case3 <- redis.sRem[String](key1, "six")
        case4 <- redis.sMembers[String](key1)
      } yield assertTrue(case1, case2, !case3, case4 == Set("two", "five"))
    ),
    TestSpec(
      "sScan", {
        implicit val codec: RCodec = stringCodec

        val members = Seq(
          "M1",
          "M2",
          "M3",
          "N1",
          "N2",
          "N3",
          "O1",
          "O2",
          "O3",
          "P1",
          "P2",
          "P3",
          "Q1",
          "Q2",
          "Q3"
        )

        for {
          redis <- redisClient
          key1 = generateKey
          _ <- redis.sAdd(key1, members)
          case1 <- redis.sScan[String](key1).runCollect
          case2 <- redis.sScan[String](key1, 18).runCollect
          case3 <- redis.sScan[String](key1, "N*").runCollect
          case4 <- redis.sScan[String](key1, "*3*").runCollect
          case5 <- redis.sScan[String](key1, "*3*", 20).runCollect
        } yield assertTrue(
          case1 == Chunk.fromIterable(members),
          case2 == Chunk.fromIterable(members),
          case3 == Chunk("N1", "N2", "N3"),
          case4 == Chunk("M3", "N3", "O3", "P3", "Q3"),
          case5 == Chunk("M3", "N3", "O3", "P3", "Q3")
        )
      }
    ),
    TestSpec(
      "sUnion",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.sAdd(key1, Seq("a", "b", "c"))
        _ <- redis.sAdd(key2, Seq("c", "d", "e"))
        case1 <- redis.sUnion[String](key1, Seq(key2))
      } yield assertTrue(case1 == Set("a", "b", "c", "d", "e"))
    ),
    TestSpec(
      "sUnionStore",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.sAdd(key1, Seq("a", "b", "c"))
        _ <- redis.sAdd(key2, Seq("c", "d", "e"))
        case1 <- redis.sUnionStore(key3, Seq(key1, key2))
        case2 <- redis.sMembers[String](key3)
      } yield assertTrue(case1 == 5, case2 == Set("a", "b", "c", "d", "e"))
    )
  )

}
