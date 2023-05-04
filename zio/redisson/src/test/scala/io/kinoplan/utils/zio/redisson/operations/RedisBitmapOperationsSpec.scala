package io.kinoplan.utils.zio.redisson.operations

import zio._
import zio.test._

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.helpers.TestSpec

object RedisBitmapOperationsSpec extends DefaultRedisCodecs {
  implicit val codec: RCodec[String, String] = RCodec.stringCodec

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "bitCount",
      for {
        redis <- redisClient
        key = generateKey
        _ <- redis.set(key, "foobar")
        case1 <- redis.bitCount(key)
      } yield assertTrue(case1 == 26)
    ),
//    TestSpec(
//      "bitOpAnd",
//      for {
//        redis <- redisClient
//        (key1, key2, key3) = (generateKey, generateKey, generateKey)
//        _ <- redis.set(key1, "foobar")
//        _ <- redis.set(key2, "abcdef")
//        _ <- redis.bitOpAnd(key3, Seq(key1, key2))
//        case1 <- redis.get[String](key3)
//      } yield assertTrue(case1.contains("`bc`ab"))
//    ),
    TestSpec(
      "bitOpOr",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.set(key1, "foobar")
        _ <- redis.set(key2, "abcdef")
        _ <- redis.bitOpOr(key3, Seq(key1, key2))
        case1 <- redis.get(key3).as[String]
      } yield assertTrue(case1.contains("goofev"))
    ),
//    TestSpec(
//      "bitOpXor",
//      for {
//        redis <- redisClient
//        (key1, key2, key3) = (generateKey, generateKey, generateKey)
//        _ <- redis.set(key1, "foobar")
//        _ <- redis.set(key2, "abcdef")
//        _ <- redis.bitOpXor(key3, Seq(key1, key2))
//        case1 <- redis.get[String](key3)
//      } yield assertTrue(case1.contains("`bc`ab"))
//    )
//    TestSpec(
//      "bitPos",
//      for {
//        redis <- redisClient
//        key = generateKey
//        _ <- redis.set(key, "foobar")
//        case1 <- redis.bitPos(key)
//      } yield assertTrue(case1 == 12)
//    )
    TestSpec(
      "getBit",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.setBit(key1, 7, value = true)
        case1 <- redis.getBit(key1, 0)
        case2 <- redis.getBit(key1, 7)
        case3 <- redis.getBit(key1, 100)
      } yield assertTrue(!case1, case2, !case3)
    ),
    TestSpec(
      "setBit",
      for {
        redis <- redisClient
        key1 = generateKey
        _ <- redis.setBit(key1, 2, value = true)
        _ <- redis.setBit(key1, 3, value = true)
        _ <- redis.setBit(key1, 5, value = true)
        _ <- redis.setBit(key1, 10, value = true)
        _ <- redis.setBit(key1, 11, value = true)
        _ <- redis.setBit(key1, 14, value = true)
        case1 <- redis.get(key1).as[String]
      } yield assertTrue(case1.contains("42"))
    )
  )

}
