package io.kinoplan.utils.zio.redisson.operations

import zio._
import zio.test._

import io.kinoplan.utils.zio.redisson.{RedisClient, redisClient}
import io.kinoplan.utils.zio.redisson.helpers.TestSpec

object RedisConnectionOperationsSpec {

  def singleSpecs: Chunk[TestSpec[RedisClient, Throwable, TestResult]] = Chunk(
    TestSpec(
      "pingSingle",
      for {
        redis <- redisClient
        case1 <- redis.pingSingle()
        case2 <- redis.pingSingle(10.seconds)
      } yield assertTrue(case1, case2)
    )
  )

}
