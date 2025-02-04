package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{RBitSet, RedissonClient}
import zio.{Task, URLayer, ZIO, ZLayer}

trait RedisBitmapOperations {

  def bitCount(key: String): Task[Long]

  def bitOpAnd(key: String, names: Seq[String]): Task[Unit]

  def bitOpOr(key: String, names: Seq[String]): Task[Unit]

  def bitOpXor(key: String, names: Seq[String]): Task[Unit]

  def bitOpNot(key: String): Task[Unit]

  def bitPos(key: String): Task[Long]

  def getBit(key: String, index: Long): Task[Boolean]

  def setBit(key: String, fromIndex: Long, toIndex: Long, value: Boolean): Task[Unit]

  def setBit(key: String, fromIndex: Long, toIndex: Long): Task[Unit]

  def setBit(key: String, index: Long): Task[Boolean]

  def setBit(key: String, index: Long, value: Boolean): Task[Boolean]

  def setBit(key: String, indexes: Iterable[Long], value: Boolean): Task[Unit]

  def clearBit(key: String, index: Long): Task[Boolean]

  def clearBit(key: String, fromIndex: Long, toIndex: Long): Task[Unit]

  def clearBit(key: String): Task[Unit]

}

trait RedisBitmapOperationsImpl extends RedisBitmapOperations {
  protected val redissonClient: RedissonClient

  private lazy val bitSet: String => RBitSet = redissonClient.getBitSet

  override def bitCount(key: String): Task[Long] = ZIO
    .fromCompletionStage(bitSet(key).cardinalityAsync())
    .map(_.longValue())

  override def bitOpAnd(key: String, names: Seq[String]): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).andAsync(names: _*))
    .unit

  override def bitOpOr(key: String, names: Seq[String]): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).orAsync(names: _*))
    .unit

  override def bitOpXor(key: String, names: Seq[String]): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).xorAsync(names: _*))
    .unit

  override def bitOpNot(key: String): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).notAsync())
    .unit

  override def bitPos(key: String): Task[Long] = ZIO
    .fromCompletionStage(bitSet(key).lengthAsync())
    .map(_.longValue())

  override def getBit(key: String, index: Long): Task[Boolean] = ZIO
    .fromCompletionStage(bitSet(key).getAsync(index))
    .map(_.booleanValue())

  override def setBit(key: String, fromIndex: Long, toIndex: Long, value: Boolean): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).setAsync(fromIndex, toIndex, value))
    .unit

  override def setBit(key: String, fromIndex: Long, toIndex: Long): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).setAsync(fromIndex, toIndex))
    .unit

  override def setBit(key: String, index: Long): Task[Boolean] = ZIO
    .fromCompletionStage(bitSet(key).setAsync(index))
    .map(_.booleanValue())

  override def setBit(key: String, index: Long, value: Boolean): Task[Boolean] = ZIO
    .fromCompletionStage(bitSet(key).setAsync(index, value))
    .map(_.booleanValue())

  override def setBit(key: String, indexes: Iterable[Long], value: Boolean): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).setAsync(indexes.toArray, value))
    .unit

  override def clearBit(key: String, index: Long): Task[Boolean] = ZIO
    .fromCompletionStage(bitSet(key).clearAsync(index))
    .map(_.booleanValue())

  override def clearBit(key: String, fromIndex: Long, toIndex: Long): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).clearAsync(fromIndex, toIndex))
    .unit

  override def clearBit(key: String): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).clearAsync())
    .unit

}

case class RedisBitmapOperationsLive(redissonClient: RedissonClient)
    extends RedisBitmapOperationsImpl

object RedisBitmapOperations {

  val live: URLayer[RedissonClient, RedisBitmapOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisBitmapOperationsLive))

}
