package io.kinoplan.utils.zio.redisson

import io.kinoplan.utils.zio.redisson.operations._
import org.redisson.api.RedissonClient
import zio.{URLayer, ZIO, ZLayer}

trait RedisClient
    extends RedisBaseOperations
      with RedisBitmapOperations
      with RedisGenericOperations
      with RedisGeoOperations
      with RedisHashOperations
      with RedisHyperLogLogOperations
      with RedisListOperations
      with RedisSetOperations
      with RedisSortedSetOperations
      with RedisStreamOperations
      with RedisStringOperations
      with RedisTopicOperations {

  val redissonClient: RedissonClient
}

case class RedisClientLive(redissonClient: RedissonClient)
    extends RedisClient
      with RedisBaseOperationsImpl
      with RedisBitmapOperationsImpl
      with RedisGenericOperationsImpl
      with RedisGeoOperationsImpl
      with RedisHashOperationsImpl
      with RedisHyperLogLogOperationsImpl
      with RedisListOperationsImpl
      with RedisSetOperationsImpl
      with RedisSortedSetOperationsImpl
      with RedisStreamOperationsImpl
      with RedisStringOperationsImpl
      with RedisTopicOperationsImpl

object RedisClient {

  val live: URLayer[RedissonClient, RedisClient] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisClientLive))

}
