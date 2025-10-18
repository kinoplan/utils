package io.kinoplan.utils.zio.redisson

import org.redisson.api.RedissonClient
import zio.{URLayer, ZLayer}

import io.kinoplan.utils.zio.redisson.operations._

/** A comprehensive Redis client supporting various Redis operations.
  *
  * Mixes in a wide range of Redis operation traits to provide full Redis functionality.
  */
trait RedisClient
    extends RedisBitmapOperations
      with RedisConnectionOperations
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

  /** Underlying Redisson client instance for connecting to Redis. */
  val redissonClient: RedissonClient
}

/** Live implementation of RedisClient.
  *
  * Provides concrete implementations for all Redis operations.
  *
  * @param redissonClient
  *   The Redisson client used for Redis operations.
  */
case class RedisClientLive(redissonClient: RedissonClient)
    extends RedisClient
      with RedisBitmapOperationsImpl
      with RedisConnectionOperationsImpl
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

/** Companion object for RedisClient containing utility for layer creation. */
object RedisClient {

  /** Creates a live RedisClient layer from a RedissonClient.
    *
    * @return
    *   A ZLayer that requires a RedissonClient and provides a RedisClient.
    */
  val live: URLayer[RedissonClient, RedisClient] = ZLayer.fromFunction(RedisClientLive.apply _)

}
