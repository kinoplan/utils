package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{RBucket, RedissonClient}
import org.redisson.client.codec.StringCodec
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

trait RedisGenericOperations {
  def exists(key: String): Task[Boolean]

  def expire(key: String, duration: Duration): Task[Boolean]

  def pExpire(key: String, duration: Duration): Task[Boolean]

  def ttl(key: String): Task[Option[Duration]]

  def pTtl(key: String): Task[Option[Duration]]
}

trait RedisGenericOperationsImpl extends RedisGenericOperations {
  protected val redissonClient: RedissonClient

  private lazy val bucket: String => RBucket[String] =
    redissonClient.getBucket(_, StringCodec.INSTANCE)

  override def exists(key: String): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).isExistsAsync)
    .map(_.booleanValue())

  override def expire(key: String, duration: Duration): Task[Boolean] = ZIO
    .fromCompletionStage(bucket(key).expireAsync(duration))
    .map(_.booleanValue())

  override def pExpire(key: String, duration: Duration): Task[Boolean] = expire(key, duration)

  override def ttl(key: String): Task[Option[Duration]] = ZIO
    .fromCompletionStage(bucket(key).remainTimeToLiveAsync())
    .map(_.longValue())
    .map(result =>
      if (result < 0) None
      else Some(Duration.fromMillis(result))
    )

  override def pTtl(key: String): Task[Option[Duration]] = ttl(key)

}

case class RedisGenericOperationsLive(redissonClient: RedissonClient)
    extends RedisGenericOperationsImpl

object RedisGenericOperations {

  val live: URLayer[RedissonClient, RedisGenericOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisGenericOperationsLive))

}
