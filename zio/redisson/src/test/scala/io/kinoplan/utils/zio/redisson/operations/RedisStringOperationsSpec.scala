package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.DefaultRedisCodecs
import io.kinoplan.utils.zio.redisson.RedisClient
import io.kinoplan.utils.zio.redisson.RedisClientSpec.{redisClient, test}
import zio._
import zio.test._

object RedisStringOperationsSpec extends DefaultRedisCodecs {

  val a = for {
    value <- ZIO.succeed("Hello World!")
    _ <- redisClient.flatMap(_.set("test", value))
    result <- redisClient.flatMap(_.get[String]("test"))
  } yield assertTrue(result.contains(value))

  def tests: Chunk[Spec[RedisClient, Throwable]] = Chunk(
    test("get") {
      for {
        value <- ZIO.succeed("Hello World!")
        _ <- redisClient.flatMap(_.set("test", value))
        result <- redisClient.flatMap(_.get[String]("test"))
      } yield assertTrue(result.contains(value))
    }
  )

}
