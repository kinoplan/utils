package io.kinoplan.utils.zio.redisson.operations

import zio._
import zio.test._

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.helpers.TestSpec

object RedisHashOperationsSpec extends DefaultRedisCodecs {

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "hDel",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.hSet(key, Map(("field1", 1), ("field2", 2), ("field3", 3)))
        case1 <- redis.hDel(key, Seq("field1"))
        case2 <- redis.hDel(key, "field2", 2)
        case3 <- redis.hDel(key, Seq("field11"))
      } yield assertTrue(case1 == 1, case2, case3 == 0)
    ),
    TestSpec(
      "hExists",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.hSet(key, Map(("field1", 1)))
        case1 <- redis.hExists(key, "field1")
        case2 <- redis.hExists(key, "field2")
      } yield assertTrue(case1, !case2)
    ),
    TestSpec(
      "hExpire",
      for {
        redis <- redisClient
        key = generateKey
        timeout = 60.seconds
        _ <- redis.hSet(key, Map(("field1", "hello"), ("field2", "world")))
        case1 <- redis.hExpire(key, timeout, "field0")
        case2 <- redis.hExpire(key, timeout, Set("field1", "field2"))
        case3 <- redis.hTtl(key, "field1")
        case4 <- redis.hTtl(key, "field2")
      } yield assertTrue(
        !case1,
        case2 == 2,
        case3.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5)),
        case4.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5))
      )
    ),
    TestSpec(
      "hExpireNx",
      for {
        redis <- redisClient
        key = generateKey
        timeout = 60.seconds
        _ <- redis.hSet(key, Map(("field1", "hello"), ("field2", "world")))
        case1 <- redis.hExpireNx(key, timeout, "field0")
        case2 <- redis.hExpireNx(key, timeout, Set("field1", "field2"))
        case3 <- redis.hTtl(key, "field1")
        case4 <- redis.hTtl(key, "field2")
      } yield assertTrue(
        !case1,
        case2 == 2,
        case3.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5)),
        case4.exists(ttl => ttl <= timeout && ttl >= timeout.minusSeconds(5))
      )
    ),
    TestSpec(
      "hExpireGt",
      for {
        redis <- redisClient
        key = generateKey
        (timeout1, timeout2) = (60.seconds, 1.hours)
        _ <- redis.hSet(key, Map(("field1", "hello"), ("field2", "world")))
        _ <- redis.hExpire(key, timeout1, Set("field1", "field2"))
        case1 <- redis.hExpireGt(key, 10.seconds, "field1")
        case2 <- redis.hExpireGt(key, timeout2, Set("field1", "field2"))
        case3 <- redis.hTtl(key, "field1")
        case4 <- redis.hTtl(key, "field2")
      } yield assertTrue(
        !case1,
        case2 == 2,
        case3.exists(ttl => ttl <= timeout2 && ttl >= timeout2.minusSeconds(5)),
        case4.exists(ttl => ttl <= timeout2 && ttl >= timeout2.minusSeconds(5))
      )
    ),
    TestSpec(
      "hExpireLt",
      for {
        redis <- redisClient
        key = generateKey
        (timeout1, timeout2) = (1.hours, 60.seconds)
        _ <- redis.hSet(key, Map(("field1", "hello"), ("field2", "world")))
        _ <- redis.hExpire(key, timeout1, Set("field1", "field2"))
        case1 <- redis.hExpireLt(key, 2.hours, "field1")
        case2 <- redis.hExpireLt(key, timeout2, Set("field1", "field2"))
        case3 <- redis.hTtl(key, "field1")
        case4 <- redis.hTtl(key, "field2")
      } yield assertTrue(
        !case1,
        case2 == 2,
        case3.exists(ttl => ttl <= timeout2 && ttl >= timeout2.minusSeconds(5)),
        case4.exists(ttl => ttl <= timeout2 && ttl >= timeout2.minusSeconds(5))
      )
    ),
    TestSpec(
      "hGet",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.hSet(key, Map(("field1", "foo")))
        case1 <- redis.hGet(key, "field1").as[String]
        case2 <- redis.hGet(key, "field2").as[String]
      } yield assertTrue(case1.contains("foo"), case2.isEmpty)
    ),
    TestSpec(
      "hGetAll",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.hSet(key, Map(("field1", "Hello"), ("field2", "World")))
        case1 <- redis.hGetAll(key).as[String]
      } yield assertTrue(case1 == Map(("field1", "Hello"), ("field2", "World")))
    ),
    TestSpec(
      "hGetDel",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.hSet(key, Map(("field1", "Hello"), ("field2", "World"), ("field3", "!")))
        case1 <- redis.hGetAll(key).as[String]
        case2 <- redis.hGetDel(key, "field3").as[String]
        case3 <- redis.hGetDel(key, "field4").as[String]
        case4 <- redis.hGetAll(key).as[String]
      } yield assertTrue(
        case1 == Map(("field1", "Hello"), ("field2", "World"), ("field3", "!")),
        case2.contains("!"),
        case3.isEmpty,
        case4 == Map(("field1", "Hello"), ("field2", "World"))
      )
    ),
    TestSpec(
      "hIncrBy", {
        implicit val codec: RCodec[String, String] = RCodec.stringCodec

        for {
          redis <- redisClient
          (key1, key2) = (generateKey, generateKey)
          _ <- redis.hSet(key1, Map(("field", 5)))
          case1 <- redis.hIncrBy(key1, "field", 1)
          case2 <- redis.hIncrBy(key1, "field", -1)
          case3 <- redis.hIncrBy(key1, "field", -10)
          case4 <- redis.hIncrBy(key1, "field1", 1)
          case5 <- redis.hIncrBy(key2, "field", 1)
        } yield assertTrue(case1 == 6, case2 == 5, case3 == -5, case4 == 1, case5 == 1)
      }
    ),
    TestSpec(
      "hIncrByFloat", {
        implicit val codec: RCodec[String, String] = RCodec.stringCodec

        for {
          redis <- redisClient
          (key1, key2) = (generateKey, generateKey)
          _ <- redis.hSet(key1, Map(("field", 10.50)))
          case1 <- redis.hIncrByFloat(key1, "field", 0.1)
          case2 <- redis.hIncrByFloat(key1, "field", -5)
          _ <- redis.hSet(key1, Map(("field", 5.0e3d)))
          case3 <- redis.hIncrByFloat(key1, "field", 2.0e2)
          case4 <- redis.hIncrByFloat(key1, "field1", 1)
          case5 <- redis.hIncrByFloat(key2, "field", 1)
        } yield assertTrue(case1 == 10.6, case2 == 5.6, case3 == 5200, case4 == 1, case5 == 1)
      }
    ),
    TestSpec(
      "hKeys",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.hSet(key1, Map(("field1", "Hello"), ("field2", "World")))
        case1 <- redis.hKeys(key1)
        case2 <- redis.hKeys(key2)
      } yield assertTrue(case1 == Set("field1", "field2"), case2.isEmpty)
    ),
    TestSpec(
      "hLen",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.hSet(key1, Map(("field1", "Hello"), ("field2", "World")))
        case1 <- redis.hLen(key1)
        case2 <- redis.hLen(key2)
      } yield assertTrue(case1 == 2, case2 == 0)
    ),
    TestSpec(
      "hmGet",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.hSet(key1, Map(("field1", "Hello"), ("field2", "World")))
        case1 <- redis.hmGet(key1, Set("field1", "field2", "nofield")).as[String]
        case2 <- redis.hmGet(key2, Set("field1", "field2", "nofield")).as[String]
      } yield assertTrue(case1 == Map(("field1", "Hello"), ("field2", "World")), case2.isEmpty)
    ),
    TestSpec(
      "hmGet",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.hSet(key1, Map(("field1", "Hello"), ("field2", "World")))
        case1 <- redis.hmGet(key1, Set("field1", "field2", "nofield")).as[String]
        case2 <- redis.hmGet(key2, Set("field1", "field2", "nofield")).as[String]
      } yield assertTrue(case1 == Map(("field1", "Hello"), ("field2", "World")), case2.isEmpty)
    ),
    TestSpec(
      "hmSet",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.hmSet(key1, Map(("field1", "Hello"), ("field2", "World")))
        case1 <- redis.hGet(key1, "field1").as[String]
        case2 <- redis.hGet(key1, "field2").as[String]
      } yield assertTrue(case1.contains("Hello"), case2.contains("World"))
    ),
    TestSpec(
      "hPersist",
      for {
        redis <- redisClient
        key1 = generateKey
        timeout = 60.seconds
        _ <- redis.hSet(key1, Map(("field1", "hello"), ("field2", "world"), ("field3", "there")))
        case1 <- redis.hExpire(key1, 60.seconds, Set("field1", "field2", "field3"))
        case2 <- redis.hTtl(key1, Set("field1", "field2", "field3"))
        case3 <- redis.hPersist(key1, "field3")
        case4 <- redis.hPersist(key1, "field4")
        case5 <- redis.hTtl(key1, Set("field1", "field2", "field3"))
        case6 <- redis.hPersist(key1, Set("field1", "field2"))
        case7 <- redis.hTtl(key1, Set("field1", "field2", "field3"))
      } yield assertTrue(
        case1 == 3,
        case2.get("field1").flatten.exists(ttl => ttl < timeout && ttl > timeout.minusSeconds(5)),
        case2.get("field2").flatten.exists(ttl => ttl < timeout && ttl > timeout.minusSeconds(5)),
        case2.get("field3").flatten.exists(ttl => ttl < timeout && ttl > timeout.minusSeconds(5)),
        case3,
        !case4,
        case5.get("field1").flatten.exists(ttl => ttl < timeout && ttl > timeout.minusSeconds(10)),
        case5.get("field2").flatten.exists(ttl => ttl < timeout && ttl > timeout.minusSeconds(10)),
        case5.get("field3").flatten.isEmpty,
        case6 == Map(("field1", true), ("field2", true)),
        case7 == Map(("field1", None), ("field2", None), ("field3", None))
      )
    ),
    TestSpec(
      "hRandField",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.hSet(key1, Map(("heads", "obverse"), ("tails", "reverse"), ("edge", "null")))
        case1 <- redis.hRandField(key1)
        case2 <- redis.hRandField(key1, 2)
        case3 <- redis.hRandFieldWithValues(key1, 5).as[String]
      } yield assertTrue(case1.size == 1, case2.size == 2, case3.nonEmpty)
    ),
    TestSpec(
      "hScan", {
        implicit val codec: RCodec[String, String] = RCodec.stringCodec

        for {
          redis <- redisClient
          key1 = generateKey
          members = Map(
            ("lang1", "PHP"),
            ("lang2", "JavaScript"),
            ("lang3", "Python"),
            ("lang4", "GoLanguage")
          )
          _ <- redis.hSet(key1, members)
          case1 <- redis.hScan(key1).as[String].runCollect.map(_.toMap)
          case2 <- redis.hScan(key1, 2).as[String].runCollect.map(_.toMap)
          case3 <- redis.hScan(key1, "la*").as[String].runCollect.map(_.toMap)
          case4 <- redis.hScan(key1, "*ang3", 5).as[String].runCollect.map(_.toMap)
        } yield assertTrue(
          case1 == members,
          case2 == members,
          case3 == members,
          case4 == Map(("lang3", "Python"))
        )
      }
    ),
    TestSpec(
      "hScanNoValues", {
        implicit val codec: RCodec[String, String] = RCodec.stringCodec

        for {
          redis <- redisClient
          key1 = generateKey
          members = Map(
            ("lang1", "PHP"),
            ("lang2", "JavaScript"),
            ("lang3", "Python"),
            ("lang4", "GoLanguage")
          )
          _ <- redis.hSet(key1, members)
          case1 <- redis.hScanNoValues(key1).runCollect.map(_.toSet)
          case2 <- redis.hScanNoValues(key1, 2).runCollect.map(_.toSet)
          case3 <- redis.hScanNoValues(key1, "la*").runCollect.map(_.toSet)
          case4 <- redis.hScanNoValues(key1, "*ang3", 5).runCollect.map(_.toSet)
        } yield assertTrue(
          case1 == members.keySet,
          case2 == members.keySet,
          case3 == members.keySet,
          case4 == Set("lang3")
        )
      }
    ),
    TestSpec(
      "hScanNoKeys", {
        implicit val codec: RCodec[String, String] = RCodec.stringCodec

        for {
          redis <- redisClient
          key1 = generateKey
          members = Map(
            ("lang1", "PHP"),
            ("lang2", "JavaScript"),
            ("lang3", "Python"),
            ("lang4", "GoLanguage")
          )
          _ <- redis.hSet(key1, members)
          case1 <- redis.hScanNoKeys(key1).as[String].runCollect
          case2 <- redis.hScanNoKeys(key1, 2).as[String].runCollect
          case3 <- redis.hScanNoKeys(key1, "la*").as[String].runCollect
          case4 <- redis.hScanNoKeys(key1, "*ang3", 5).as[String].runCollect
        } yield assertTrue(
          case1 == Chunk.fromIterable(members.values),
          case2 == Chunk.fromIterable(members.values),
          case3 == Chunk.fromIterable(members.values),
          case4 == Chunk.single("Python")
        )
      }
    ),
    TestSpec(
      "hSet",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.hSet(key1, Map(("field1", "Hello")))
        case1 <- redis.hGet(key1, "field1").as[String]
        _ <- redis.hSet(key1, Map(("field2", "Hi"), ("field3", "World")))
        case2 <- redis.hGet(key1, "field2").as[String]
        case3 <- redis.hGet(key1, "field3").as[String]
        case4 <- redis.hGetAll(key1).as[String]
      } yield assertTrue(
        case1.contains("Hello"),
        case2.contains("Hi"),
        case3.contains("World"),
        case4 == Map(("field1", "Hello"), ("field2", "Hi"), ("field3", "World"))
      )
    ),
    TestSpec(
      "hSetNx",
      for {
        redis <- redisClient
        key1 = generateKey
        case1 <- redis.hSetNx(key1, "field", "Hello")
        case2 <- redis.hSetNx(key1, "field", "World")
        case3 <- redis.hGet(key1, "field").as[String]
      } yield assertTrue(case1, !case2, case3.contains("Hello"))
    ),
    TestSpec(
      "hStrLen", {
        implicit val codec: RCodec[String, String] = RCodec.stringCodec

        for {
          redis <- redisClient
          key1 = generateKey
          _ <- redis.hSet(key1, Map(("f1", "HelloWorld"), ("f2", "99"), ("f3", "-256")))
          case1 <- redis.hStrLen(key1, "f1")
          case2 <- redis.hStrLen(key1, "f2")
          case3 <- redis.hStrLen(key1, "f3")
          case4 <- redis.hStrLen(key1, "f4")
        } yield assertTrue(case1 == 10, case2 == 2, case3 == 4, case4 == 0)
      }
    ),
    TestSpec(
      "hTtl",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        timeout = 60.seconds
        case1 <- redis.hTtl(key1, Set("field1", "field2", "field3"))
        _ <- redis.hSet(key2, Map(("field1", "hello"), ("field2", "world")))
        _ <- redis.hExpire(key2, timeout, Set("field1", "field3"))
        case2 <- redis.hTtl(key2, "field1")
        case3 <- redis.hTtl(key2, Set("field2", "field3"))
      } yield assertTrue(
        case1 == Map(("field1", None), ("field2", None), ("field3", None)),
        case2.exists(ttl => ttl < timeout && ttl > timeout.minusSeconds(5)),
        case3 == Map(("field2", None), ("field3", None))
      )
    ),
    TestSpec(
      "hVals",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        _ <- redis.hSet(key1, Map(("field1", "Hello"), ("field2", "World")))
        case1 <- redis.hVals(key1).as[String]
        case2 <- redis.hVals(key2).as[String]
      } yield assertTrue(case1 == Iterable("Hello", "World"), case2.isEmpty)
    )
  )

}
