package io.kinoplan.utils.zio.redisson

import io.kinoplan.utils.zio.redisson.operation.{
  RedisBaseOperations,
  RedisListOperations,
  RedisSetOperations,
  RedisTopicOperations
}
import org.redisson.api.RedissonClient
import zio.macros.accessible
import zio.{URLayer, ZIO, ZLayer}

@accessible
trait RedisClient {
  def base: RedisBaseOperations
  def list: RedisListOperations
  def set: RedisSetOperations
  def topic: RedisTopicOperations
}

object RedisClient {

  val live: URLayer[RedissonClient, RedisClient] = RedisBaseOperations.live ++
    RedisListOperations.live ++ RedisSetOperations.live ++ RedisTopicOperations.live >>>
    ZLayer.fromZIO(
      for {
        baseOperations <- ZIO.service[RedisBaseOperations]
        listOperations <- ZIO.service[RedisListOperations]
        setOperations <- ZIO.service[RedisSetOperations]
        topicOperations <- ZIO.service[RedisTopicOperations]
      } yield new RedisClient {
        override def base: RedisBaseOperations = baseOperations

        override def list: RedisListOperations = listOperations

        override def set: RedisSetOperations = setOperations

        override def topic: RedisTopicOperations = topicOperations
      }
    )

}
