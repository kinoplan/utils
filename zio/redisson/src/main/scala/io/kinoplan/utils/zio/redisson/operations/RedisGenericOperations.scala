package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{RBucket, RedissonClient}
import org.redisson.client.codec.StringCodec
import zio.{Task, URLayer, ZIO, ZLayer}

trait RedisGenericOperations {
  def exists(key: String): Task[Boolean]

  def ttl(key: String): Task[Long]
}

trait RedisGenericOperationsImpl extends RedisGenericOperations {
  protected val redissonClient: RedissonClient

  private lazy val bucket: String => RBucket[String] =
    redissonClient.getBucket(_, StringCodec.INSTANCE)

  override def exists(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).isExistsAsync)
    .map(_.booleanValue())

  override def ttl(key: String): Task[Long] = ZIO
    .fromCompletionStage(bucket(key).remainTimeToLiveAsync())
    .map(_.longValue())

}

case class RedisGenericOperationsLive(redissonClient: RedissonClient)
    extends RedisGenericOperationsImpl

object RedisGenericOperations {

  val live: URLayer[RedissonClient, RedisGenericOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisGenericOperationsLive))

}
