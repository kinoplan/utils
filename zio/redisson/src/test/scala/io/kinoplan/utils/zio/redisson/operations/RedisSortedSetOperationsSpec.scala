package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.helpers.TestSpec
import org.redisson.api.RScoredSortedSet.Aggregate
import org.redisson.api.{SetIntersectionArgs, SetUnionArgs}
import zio._
import zio.test._

object RedisSortedSetOperationsSpec extends DefaultRedisCodecs {

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "bzmPop",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        case1 <- redis.bzmPopMin[String]("notsuchkey", timeout)
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case2 <- redis.bzmPopMin[String](key1, timeout)
        case3 <- redis.zRange[String](key1)
        case4 <- redis.bzmPopMax[String](key1, timeout, 10)
        _ <- redis.zAdd(key2, Map(("four", 4), ("five", 5), ("six", 6)))
        case5 <- redis.bzmPopMin[String](key1, timeout, 10, Seq(key2))
        case6 <- redis.zRange[String](key1)
        case7 <- redis.bzmPopMax[String](key1, timeout, 10, Seq(key2))
        case8 <- redis.zRange[String](key2)
        case9 <- redis.exists(key1)
        case10 <- redis.exists(key2)
      } yield assertTrue(
        case1.isEmpty,
        case2 == Map((key1, Map(("one", 1.0)))),
        case3 == Iterable("two", "three"),
        case4 == Map((key1, Map(("three", 3.0), ("two", 2.0)))),
        case5 == Map((key2, Map(("four", 4.0), ("five", 5.0), ("six", 6.0)))),
        case6.isEmpty,
        case7.isEmpty,
        case8.isEmpty,
        !case9,
        !case10
      )
    ),
    TestSpec(
      "bzPopMax",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("a", 0), ("b", 1), ("c", 2)))
        case1 <- redis.bzPopMax[String](key1, timeout)
      } yield assertTrue(case1.contains("c"))
    ),
    TestSpec(
      "bzPopMin",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("a", 0), ("b", 1), ("c", 2)))
        case1 <- redis.bzPopMin[String](key1, timeout)
      } yield assertTrue(case1.contains("a"))
    ),
    TestSpec(
      "zAdd",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.zAdd(key, 1, "one")
        case2 <- redis.zAdd(key, 1, "uno")
        case3 <- redis.zAdd(key, Map(("two", 2), ("three", 3)))
        case4 <- redis.zRange[String](key)
      } yield assertTrue(case1, case2, case3 == 2, case4 == Iterable("one", "uno", "two", "three"))
    ),
    TestSpec(
      "zCard",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.zAdd(key, 1, "one")
        _ <- redis.zAdd(key, 2, "two")
        case1 <- redis.zCard(key)
      } yield assertTrue(case1 == 2)
    ),
    TestSpec(
      "zCount",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.zAdd(key, 1, "one")
        _ <- redis.zAdd(key, 2, "two")
        _ <- redis.zAdd(key, 2, "three")
        case1 <- redis.zCount(
          key,
          Double.NegativeInfinity,
          startInclusive = true,
          Double.PositiveInfinity,
          endInclusive = true
        )
        case2 <- redis.zCount(key, 1, startInclusive = false, 3, endInclusive = true)
      } yield assertTrue(case1 == 3, case2 == 2)
    ),
    TestSpec(
      "zDiff",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        _ <- redis.zAdd(key2, Map(("one", 1), ("two", 2)))
        case1 <- redis.zDiff[String](key1, Seq(key2))
      } yield assertTrue(case1 == Iterable("three"))
    ),
    TestSpec(
      "zDiffStore",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        _ <- redis.zAdd(key2, Map(("one", 1), ("two", 2)))
        case1 <- redis.zDiffStore(key3, Seq(key1, key2))
        case2 <- redis.zRange[String](key3)
      } yield assertTrue(case1 == 1, case2 == Iterable("three"))
    ),
    TestSpec(
      "zIncrBy",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2)))
        case1 <- redis.zIncrBy(key1, 2, "one")
        case2 <- redis.zRange[String](key1)
      } yield assertTrue(case1 == 3, case2 == Iterable("two", "one"))
    ),
    TestSpec(
      "zInter",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2)))
        _ <- redis.zAdd(key2, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zInter[String](key1, SetIntersectionArgs.names(key2))
      } yield assertTrue(case1 == Iterable("one", "two"))
    ),
    TestSpec(
      "zInterStore",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.zAdd(key1, Map(("M", 5), ("N", 6), ("O", 7)))
        _ <- redis.zAdd(key2, Map(("N", 3), ("O", 2), ("P", 4)))
        case1 <- redis.zInterStore(key3, SetIntersectionArgs.names(key1, key2))
        case2 <- redis.zRange[String](key3)
        case3 <- redis.zInterStore(key3, SetIntersectionArgs.names(key1, key2).weights(2.0, 3.0))
        case4 <- redis.zRange[String](key3)
        case5 <-
          redis.zInterStore(key3, SetIntersectionArgs.names(key1, key2).aggregate(Aggregate.MIN))
        case6 <- redis.zRange[String](key3)
        case7 <-
          redis.zInterStore(key3, SetIntersectionArgs.names(key1, key2).aggregate(Aggregate.MAX))
        case8 <- redis.zRange[String](key3)
      } yield assertTrue(
        case1 == 2,
        case2 == Iterable("N", "O"),
        case3 == 2,
        case4 == Iterable("O", "N"),
        case5 == 2,
        case6 == Iterable("O", "N"),
        case7 == 2,
        case8 == Iterable("N", "O")
      )
    ),
    TestSpec(
      "zLexCount", {
        implicit val codec: RCodec = RCodec.stringCodec

        for {
          redis <- redisClient
          key = generateKey
          scoreMembers =
            Map(("Delhi", 1.0), ("Mumbai", 1.0), ("London", 1.0), ("Paris", 1.0), ("Tokyo", 1.0))
          _ <- redis.zAdd[String](key, scoreMembers)
          case1 <- redis.zLexCount(key, "Delhi", fromInclusive = true, "Tokyo", toInclusive = true)
          case2 <- redis.zLexCountMax(key, "London", fromInclusive = true)
          case3 <- redis.zLexCountMax(key, "London", fromInclusive = false)
          case4 <- redis.zLexCountMin(key, "Delhi", toInclusive = true)
          case5 <-
            redis.zLexCount(key, "London", fromInclusive = false, "Paris", toInclusive = false)
          case6 <- redis.zLexCount(key, "London", fromInclusive = true, "Paris", toInclusive = false)
        } yield assertTrue(case1 == 5, case2 == 4, case3 == 3, case4 == 1, case5 == 1, case6 == 2)
      }
    ),
    TestSpec(
      "zmPop",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        case1 <- redis.zmPopMin[String]("notsuchkey")
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case2 <- redis.zmPopMin[String](key1)
        case3 <- redis.zRange[String](key1)
        case4 <- redis.zmPopMax[String](key1, 10)
        _ <- redis.zAdd(key2, Map(("four", 4), ("five", 5), ("six", 6)))
        case5 <- redis.zmPopMin[String](key1, 10, Seq(key2))
        case6 <- redis.zRange[String](key1)
        case7 <- redis.zmPopMax[String](key1, 10, Seq(key2))
        case8 <- redis.zRange[String](key2)
        case9 <- redis.exists(key1)
        case10 <- redis.exists(key2)
      } yield assertTrue(
        case1.isEmpty,
        case2 == Map((key1, Map(("one", 1.0)))),
        case3 == Iterable("two", "three"),
        case4 == Map((key1, Map(("three", 3.0), ("two", 2.0)))),
        case5 == Map((key2, Map(("four", 4.0), ("five", 5.0), ("six", 6.0)))),
        case6.isEmpty,
        case7.isEmpty,
        case8.isEmpty,
        !case9,
        !case10
      )
    ),
    TestSpec(
      "zPopMax",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3), ("four", 4)))
        case1 <- redis.zPopMax[String](key1)
        case2 <- redis.zPopMax[String](key1, 2)
      } yield assertTrue(case1.contains("four"), case2 == Iterable("two", "three"))
    ),
    TestSpec(
      "zPopMin",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3), ("four", 4)))
        case1 <- redis.zPopMin[String](key1)
        case2 <- redis.zPopMin[String](key1, 2)
      } yield assertTrue(case1.contains("one"), case2 == Iterable("two", "three"))
    ),
    TestSpec(
      "zRandMember",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(
          key1,
          Map(("uno", 1), ("due", 2), ("tre", 3), ("quattro", 4), ("cinque", 5), ("sei", 6))
        )
        case1 <- redis.zRandMember[String](key1)
        case2 <- redis.zRandMember[String](key1)
        case3 <- redis.zRandMember[String](key1, 5)
      } yield assertTrue(case1.nonEmpty, case2.nonEmpty, case3.size == 5)
    ),
    TestSpec(
      "zRange",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zRange[String](key1, 0, -1)
        case2 <- redis.zRange[String](key1, 2, 3)
        case3 <- redis.zRange[String](key1, -2, -1)
        case4 <- redis.zRange[String](key1, 2, fromInclusive = true, 3, toInclusive = true)
        case5 <- redis.zRange[String](key1, 0, fromInclusive = true, 3, toInclusive = true, 1, 1)
      } yield assertTrue(
        case1 == Iterable("one", "two", "three"),
        case2 == Iterable("three"),
        case3 == Iterable("two", "three"),
        case4 == Iterable("two", "three"),
        case5 == Iterable("two")
      )
    ),
    TestSpec(
      "zRangeWithScores",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zRangeWithScores[String](key1, 0, -1)
        case2 <- redis.zRangeWithScores[String](key1, 2, 3)
        case3 <- redis.zRangeWithScores[String](key1, -2, -1)
        case4 <- redis.zRangeWithScores[String](key1, 2, fromInclusive = true, 3, toInclusive = true)
        case5 <-
          redis.zRangeWithScores[String](key1, 0, fromInclusive = true, 3, toInclusive = true, 1, 1)
      } yield assertTrue(
        case1 == Map(("one", 1.0), ("two", 2.0), ("three", 3.0)),
        case2 == Map(("three", 3.0)),
        case3 == Map(("two", 2.0), ("three", 3.0)),
        case4 == Map(("two", 2.0), ("three", 3.0)),
        case5 == Map(("two", 2.0))
      )
    ),
    TestSpec(
      "zRangeByLex", {
        implicit val codec: RCodec = RCodec.stringCodec

        for {
          redis <- redisClient
          key1 = generateKey
          scoreMembers = Map(
            ("Delhi", 1.0),
            ("London", 2.0),
            ("Paris", 3.0),
            ("Tokyo", 4.0),
            ("NewYork", 5.0),
            ("Seoul", 6.0)
          )
          _ <- redis.zAdd[String](key1, scoreMembers)
          case1 <- redis.zRangeByLex[String](key1)
          case2 <- redis.zRangeByLexMax[String](key1, "London", fromInclusive = true)
          case3 <- redis.zRangeByLexMax[String](key1, "London", fromInclusive = true, 1, 2)
          case4 <- redis.zRangeByLexMax[String](key1, "London", fromInclusive = false)
          case5 <- redis.zRangeByLexMin[String](key1, "London", toInclusive = true)
          case6 <- redis.zRangeByLexMin[String](key1, "Tokyo", toInclusive = true, 1, 2)
          case7 <- redis.zRangeByLex[String](
            key1,
            "London",
            fromInclusive = false,
            "Seoul",
            toInclusive = false
          )
          case8 <- redis.zRangeByLex[String](
            key1,
            "Delhi",
            fromInclusive = true,
            "Seoul",
            toInclusive = true,
            1,
            2
          )
        } yield assertTrue(
          case1 == Iterable("Delhi", "London", "Paris", "Tokyo", "NewYork", "Seoul"),
          case2 == Iterable("London", "Paris", "Tokyo", "NewYork", "Seoul"),
          case3 == Iterable("Paris", "Tokyo"),
          case4 == Iterable("Paris", "Tokyo", "NewYork", "Seoul"),
          case5 == Iterable("Delhi", "London"),
          case6 == Iterable("London", "Paris"),
          case7 == Iterable("Paris"),
          case8 == Iterable("London", "Paris")
        )
      }
    ),
    TestSpec(
      "zRangeByScore",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zRangeByScore[String](
          key1,
          Double.NegativeInfinity,
          startInclusive = true,
          Double.PositiveInfinity,
          endInclusive = true
        )
        case2 <- redis.zRangeByScore[String](key1, 1, startInclusive = true, 2, endInclusive = true)
        case3 <- redis.zRangeByScore[String](key1, 1, startInclusive = false, 2, endInclusive = true)
        case4 <-
          redis.zRangeByScore[String](key1, 1, startInclusive = false, 2, endInclusive = false)
        case5 <-
          redis.zRangeByScore[String](key1, 0, startInclusive = true, 3, endInclusive = true, 1, 1)
      } yield assertTrue(
        case1 == Iterable("one", "two", "three"),
        case2 == Iterable("one", "two"),
        case3 == Iterable("two"),
        case4.isEmpty,
        case5 == Iterable("two")
      )
    ),
    TestSpec(
      "zRank",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zRank(key1, "three")
        case2 <- redis.zRank(key1, "four")
        case3 <- redis.zRank(key1, "two")
      } yield assertTrue(case1 == 2, case2 == 0, case3 == 1)
    ),
    TestSpec(
      "zRem",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3), ("four", 4)))
        case1 <- redis.zRem(key1, "four")
        case2 <- redis.zRem(key1, Set("one", "three"))
        case3 <- redis.zRange[String](key1)
      } yield assertTrue(case1, case2, case3 == Iterable("two"))
    ),
    TestSpec(
      "zRemRangeByLex", {
        implicit val codec: RCodec = RCodec.stringCodec

        for {
          redis <- redisClient
          key1 = generateKey
          _ <- redis.zAdd(key1, Map(("aaaa", 0), ("b", 0), ("c", 0), ("d", 0), ("e", 0)))
          _ <- redis.zAdd(key1, Map(("foo", 0), ("zap", 0), ("zip", 0), ("ALPHA", 0), ("alpha", 0)))
          case1 <-
            redis.zRemRangeByLex(key1, "alpha", fromInclusive = true, "omega", toInclusive = true)
          case2 <- redis.zRange[String](key1)
          case3 <- redis.zRemRangeByLexMax(key1, "zip", fromInclusive = true)
          case4 <- redis.zRange[String](key1)
          case5 <- redis.zRemRangeByLexMin(key1, "aaaa", toInclusive = true)
          case6 <- redis.zRange[String](key1)
        } yield assertTrue(
          case1 == 6,
          case2 == Iterable("ALPHA", "aaaa", "zap", "zip"),
          case3 == 1,
          case4 == Iterable("ALPHA", "aaaa", "zap"),
          case5 == 2,
          case6 == Iterable("zap")
        )
      }
    ),
    TestSpec(
      "zRemRangeByRank",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zRemRangeByRank(key1, 0, 1)
        case2 <- redis.zRange[String](key1)
      } yield assertTrue(case1 == 2, case2 == Iterable("three"))
    ),
    TestSpec(
      "zRemRangeByScore",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zRemRangeByScore(
          key1,
          Double.NegativeInfinity,
          startInclusive = true,
          2,
          endInclusive = false
        )
        case2 <- redis.zRange[String](key1)
      } yield assertTrue(case1 == 1, case2 == Iterable("two", "three"))
    ),
    TestSpec(
      "zRevRange",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zRevRange[String](key1, 0, -1)
        case2 <- redis.zRevRange[String](key1, 2, 3)
        case3 <- redis.zRevRange[String](key1, -2, -1)
        case4 <- redis.zRevRange[String](key1, 2, fromInclusive = true, 3, toInclusive = true)
        case5 <- redis.zRevRange[String](key1, 0, fromInclusive = true, 3, toInclusive = true, 1, 1)
      } yield assertTrue(
        case1 == Iterable("three", "two", "one"),
        case2 == Iterable("one"),
        case3 == Iterable("two", "one"),
        case4 == Iterable("three", "two"),
        case5 == Iterable("two")
      )
    ),
    TestSpec(
      "zRevRangeWithScores",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zRevRangeWithScores[String](key1, 0, -1)
        case2 <- redis.zRevRangeWithScores[String](key1, 2, 3)
        case3 <- redis.zRevRangeWithScores[String](key1, -2, -1)
        case4 <-
          redis.zRevRangeWithScores[String](key1, 2, fromInclusive = true, 3, toInclusive = true)
        case5 <- redis.zRevRangeWithScores[String](
          key1,
          0,
          fromInclusive = true,
          3,
          toInclusive = true,
          1,
          1
        )
      } yield assertTrue(
        case1 == Map(("three", 3.0), ("two", 2.0), ("one", 1.0)),
        case2 == Map(("one", 1.0)),
        case3 == Map(("two", 2.0), ("one", 1.0)),
        case4 == Map(("three", 3.0), ("two", 2.0)),
        case5 == Map(("two", 2.0))
      )
    ),
    TestSpec(
      "zRevRangeByLex", {
        implicit val codec: RCodec = RCodec.stringCodec

        for {
          redis <- redisClient
          key1 = generateKey
          scoreMembers =
            Map(("a", 0.0), ("b", 0.0), ("c", 0.0), ("d", 0.0), ("e", 0.0), ("f", 0.0), ("g", 0.0))
          _ <- redis.zAdd[String](key1, scoreMembers)
          case1 <-
            redis.zRevRangeByLex[String](key1, "a", fromInclusive = true, "c", toInclusive = true)
          case2 <-
            redis.zRevRangeByLex[String](key1, "a", fromInclusive = true, "c", toInclusive = false)
          case3 <-
            redis.zRevRangeByLex[String](key1, "aaa", fromInclusive = true, "g", toInclusive = false)
          case4 <- redis.zRevRangeByLex[String](
            key1,
            "aaa",
            fromInclusive = true,
            "g",
            toInclusive = false,
            1,
            2
          )
        } yield assertTrue(
          case1 == Iterable("c", "b", "a"),
          case2 == Iterable("b", "a"),
          case3 == Iterable("f", "e", "d", "c", "b"),
          case4 == Iterable("e", "d")
        )
      }
    ),
    TestSpec(
      "zRevRangeByScore",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zRevRangeByScore[String](
          key1,
          Double.NegativeInfinity,
          startInclusive = true,
          Double.PositiveInfinity,
          endInclusive = true
        )
        case2 <-
          redis.zRevRangeByScore[String](key1, 1, startInclusive = true, 2, endInclusive = true)
        case3 <-
          redis.zRevRangeByScore[String](key1, 1, startInclusive = false, 2, endInclusive = true)
        case4 <-
          redis.zRevRangeByScore[String](key1, 2, startInclusive = false, 1, endInclusive = false)
        case5 <- redis.zRevRangeByScore[String](
          key1,
          0,
          startInclusive = true,
          3,
          endInclusive = true,
          1,
          1
        )
      } yield assertTrue(
        case1 == Iterable("three", "two", "one"),
        case2 == Iterable("two", "one"),
        case3 == Iterable("two"),
        case4.isEmpty,
        case5 == Iterable("two")
      )
    ),
    TestSpec(
      "zRevRank",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zRevRank(key1, "one")
        case2 <- redis.zRevRank(key1, "four")
        case3 <- redis.zRevRank(key1, "three")
      } yield assertTrue(case1 == 2, case2 == 0, case3 == 0)
    ),
    TestSpec(
      "zScan", {
        implicit val codec: RCodec = RCodec.stringCodec

        for {
          redis <- redisClient
          key1 = generateKey
          _ <- redis.zAdd(
            key1,
            Map(
              ("M1", 1),
              ("M2", 2),
              ("M3", 3),
              ("N1", 4),
              ("N2", 5),
              ("N3", 6),
              ("O1", 7),
              ("O2", 8),
              ("O3", 9)
            )
          )
          case1 <- redis.zScan[String](key1).runCollect
          case2 <- redis.zScan[String](key1, 5).runCollect
          case3 <- redis.zScan[String](key1, "N*").runCollect
          case4 <- redis.zScan[String](key1, "*3*").runCollect
          case5 <- redis.zScan[String](key1, "*3*", 20).runCollect
        } yield assertTrue(
          case1 == Chunk("M1", "M2", "M3", "N1", "N2", "N3", "O1", "O2", "O3"),
          case2 == Chunk("M1", "M2", "M3", "N1", "N2", "N3", "O1", "O2", "O3"),
          case3 == Chunk("N1", "N2", "N3"),
          case4 == Chunk("M3", "N3", "O3"),
          case5 == Chunk("M3", "N3", "O3")
        )
      }
    ),
    TestSpec(
      "zScore",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.zAdd(key1, Map(("one", 1), ("two", 2), ("three", 3)))
        case1 <- redis.zScore(key1, "one")
        case2 <- redis.zScore(key1, Set("two", "three", "four"))
        case3 <- redis.zScore(key1, "four")
      } yield assertTrue(case1 == 1.0, case2 == List(2.0, 3.0, 0), case3 == 0.0)
    ),
    TestSpec(
      "zUnion",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.zAdd(key1, Map(("M", 5), ("N", 6), ("O", 7)))
        _ <- redis.zAdd(key2, Map(("N", 3), ("O", 2), ("P", 4)))
        case1 <- redis.zUnion[String](key1, SetUnionArgs.names(key2))
        case2 <- redis.zUnion[String](key1, SetUnionArgs.names(key2).aggregate(Aggregate.MIN))
        case3 <- redis.zUnion[String](key1, SetUnionArgs.names(key2).aggregate(Aggregate.MAX))
      } yield assertTrue(
        case1 == Iterable("P", "M", "N", "O"),
        case2 == Iterable("O", "N", "P", "M"),
        case3 == Iterable("P", "M", "N", "O")
      )
    ),
    TestSpec(
      "zUnionStore",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.zAdd(key1, Map(("M", 5), ("N", 6), ("O", 7)))
        _ <- redis.zAdd(key2, Map(("N", 3), ("O", 2), ("P", 4)))
        case1 <- redis.zUnionStore(key3, SetUnionArgs.names(key1, key2))
        case2 <- redis.zRange[String](key3)
        case3 <- redis.zUnionStore(key3, SetUnionArgs.names(key1, key2).weights(2.0, 3.0))
        case4 <- redis.zRange[String](key3)
        case5 <- redis.zUnionStore(key3, SetUnionArgs.names(key1, key2).aggregate(Aggregate.MIN))
        case6 <- redis.zRange[String](key3)
        case7 <- redis.zUnionStore(
          key3,
          SetUnionArgs.names(key1, key2).weights(2.0, 3.0).aggregate(Aggregate.MAX)
        )
        case8 <- redis.zRange[String](key3)
      } yield assertTrue(
        case1 == 4,
        case2 == Iterable("P", "M", "N", "O"),
        case3 == 4,
        case4 == Iterable("M", "P", "O", "N"),
        case5 == 4,
        case6 == Iterable("O", "N", "P", "M"),
        case7 == 4,
        case8 == Iterable("M", "N", "P", "O")
      )
    )
  )

}
