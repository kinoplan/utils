package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.RedisEncoder
import org.redisson.api.{RHyperLogLog, RedissonClient}
import org.redisson.client.codec.StringCodec
import zio.{Task, URLayer, ZIO, ZLayer}

import scala.jdk.CollectionConverters.IterableHasAsJava

trait RedisHyperLogLogOperations {

  def pfAdd[T: RedisEncoder](key: String, element: T): Task[Boolean]

  def pfAdd[T: RedisEncoder](key: String, elements: Iterable[T]): Task[Boolean]

  def pfCount(key: String): Task[Long]

  def pfCount(key: String, names: Seq[String]): Task[Long]

  def pfMerge(key: String, names: Seq[String]): Task[Unit]

}

trait RedisHyperLogLogOperationsImpl extends RedisHyperLogLogOperations {

  protected val redissonClient: RedissonClient

  private lazy val hyperLogLog: String => RHyperLogLog[String] =
    redissonClient.getHyperLogLog[String](_, StringCodec.INSTANCE)

  override def pfAdd[T: RedisEncoder](key: String, element: T): Task[Boolean] = ZIO
    .fromCompletionStage(hyperLogLog(key).addAsync(RedisEncoder[T].encode(element)))
    .map(_.booleanValue())

  override def pfAdd[T: RedisEncoder](key: String, elements: Iterable[T]): Task[Boolean] = ZIO
    .fromCompletionStage(
      hyperLogLog(key).addAllAsync(elements.map(RedisEncoder[T].encode).asJavaCollection)
    )
    .map(_.booleanValue())

  override def pfCount(key: String): Task[Long] = ZIO
    .fromCompletionStage(hyperLogLog(key).countAsync())
    .map(_.longValue())

  override def pfCount(key: String, names: Seq[String]): Task[Long] = ZIO
    .fromCompletionStage(hyperLogLog(key).countWithAsync(names: _*))
    .map(_.longValue())

  override def pfMerge(key: String, names: Seq[String]): Task[Unit] = ZIO
    .fromCompletionStage(hyperLogLog(key).mergeWithAsync(names: _*))
    .unit

}

case class RedisHyperLogLogOperationsLive(redissonClient: RedissonClient)
    extends RedisHyperLogLogOperationsImpl

object RedisHyperLogLogOperations {

  val live: URLayer[RedissonClient, RedisHyperLogLogOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisHyperLogLogOperationsLive))

}
