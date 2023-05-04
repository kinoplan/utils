package io.kinoplan.utils.zio.redisson.operations

import zio._
import zio.test._

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.helpers.TestSpec

object RedisHyperLogLogOperationsSpec extends DefaultRedisCodecs {

  def specs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "pfAdd",
      for {
        redis <- redisClient
        key = generateKey
        case1 <- redis.pfAdd(key, "a")
        case2 <- redis.pfAdd(key, Seq("b", "c", "d", "e", "f", "g"))
        case3 <- redis.pfCount(key)
      } yield assertTrue(case1, case2, case3 == 7)
    ),
    TestSpec(
      "pfCount",
      for {
        redis <- redisClient
        (key1, key2) = (generateKey, generateKey)
        case1 <- redis.pfAdd(key1, Seq("foo", "bar", "zap"))
        case2 <- redis.pfAdd(key1, Seq("zap", "zap", "zap"))
        case3 <- redis.pfAdd(key1, Seq("foo", "bar"))
        case4 <- redis.pfCount(key1)
        case5 <- redis.pfAdd(key2, Seq(1, 2, 3))
        case6 <- redis.pfCount(key1, Seq(key2))
      } yield assertTrue(case1, !case2, !case3, case4 == 3, case5, case6 == 6)
    ),
    TestSpec(
      "pfMerge",
      for {
        redis <- redisClient
        (key1, key2, key3) = (generateKey, generateKey, generateKey)
        _ <- redis.pfAdd(key1, Seq("foo", "bar", "zap", "a"))
        _ <- redis.pfAdd(key1, Seq("a", "b", "c", "foo"))
        _ <- redis.pfMerge(key3, Seq(key1, key2))
        case1 <- redis.pfCount(key3)
      } yield assertTrue(case1 == 6)
    )
  )

}
