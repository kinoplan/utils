package io.kinoplan.utils.zio.redisson

import io.kinoplan.utils.zio.redisson.operations._
import org.redisson.api.RedissonClient
import zio.macros.accessible
import zio.{URLayer, ZIO, ZLayer}

@accessible
trait RedisClient
    extends RedisBaseOperations
      with RedisBitmapOperations
      with RedisListOperations
      with RedisSetOperations
      with RedisSortedSetOperations
      with RedisStringOperations
      with RedisTopicOperations
      with RedisHyperLogLogOperations

case class RedisClientLive(redissonClient: RedissonClient)
    extends RedisClient
      with RedisBaseOperationsImpl
      with RedisBitmapOperationsImpl
      with RedisListOperationsImpl
      with RedisSetOperationsImpl
      with RedisSortedSetOperationsImpl
      with RedisStringOperationsImpl
      with RedisTopicOperationsImpl
      with RedisHyperLogLogOperationsImpl

object RedisClient {

  val live: URLayer[RedissonClient, RedisClient] = ZLayer
    .fromZIO(ZIO.service[RedissonClient].map(RedisClientLive))

}
