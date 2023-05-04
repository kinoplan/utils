package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{RBitSet, RedissonClient}
import zio.{Task, URLayer, ZIO, ZLayer}

/** Interface representing operations that can be performed on Redis bitmap data.
  */
trait RedisBitmapOperations {

  /** Count the number of set bits (population counting) in a string.
    *
    * Similar to the BITCOUNT command.
    *
    * @param key
    *   The key of the bitmap.
    * @return
    *   The number of bits set to 1.
    */
  def bitCount(key: String): Task[Long]

  /** Perform a bitwise AND operation between multiple keys and store the result in the destination
    * key.
    *
    * Similar to the BITOP AND command.
    *
    * @param key
    *   The destination key where the result is stored.
    * @param names
    *   The keys on which to perform the AND operation.
    */
  def bitOpAnd(key: String, names: Seq[String]): Task[Unit]

  /** Perform a bitwise OR operation between multiple keys and store the result in the destination
    * key.
    *
    * Similar to the BITOP OR command.
    *
    * @param key
    *   The destination key where the result is stored.
    * @param names
    *   The keys on which to perform the OR operation.
    */
  def bitOpOr(key: String, names: Seq[String]): Task[Unit]

  /** Perform a bitwise XOR operation between multiple keys and store the result in the destination
    * key.
    *
    * Similar to the BITOP XOR command.
    *
    * @param key
    *   The destination key where the result is stored.
    * @param names
    *   The keys on which to perform the XOR operation.
    */
  def bitOpXor(key: String, names: Seq[String]): Task[Unit]

  /** Perform a bitwise NOT operation on the given key and store the result in the destination key.
    *
    * Similar to the BITOP NOT command.
    *
    * @param key
    *   The key on which to perform the NOT operation and store the result.
    */
  def bitOpNot(key: String): Task[Unit]

  /** Find the position of the first bit set to 1 or 0 in a string.
    *
    * Similar to the BITPOS command.
    *
    * @param key
    *   The key of the bitmap.
    * @return
    *   The position of the first bit set to 1.
    */
  def bitPos(key: String): Task[Long]

  /** Get the bit value at a specific position.
    *
    * Similar to the GETBIT command.
    *
    * @param key
    *   The key of the bitmap.
    * @param index
    *   The position of the bit to retrieve.
    * @return
    *   True if the bit is set (1); False if it is not set (0).
    */
  def getBit(key: String, index: Long): Task[Boolean]

  /** Set or clear a range of bits between two indices. This involves setting a value of 1 or
    * clearing with a value of 0 over a specified range.
    *
    * Similar to the SETBIT command.
    *
    * @param key
    *   The key of the bitmap.
    * @param fromIndex
    *   The starting index (inclusive).
    * @param toIndex
    *   The ending index (inclusive).
    * @param value
    *   True to set bits to 1, False to set them to 0.
    */
  def setBit(key: String, fromIndex: Long, toIndex: Long, value: Boolean): Task[Unit]

  /** Set a specified range of bits between two indices to 1.
    *
    * Similar to the SETBIT command.
    *
    * @param key
    *   The key of the bitmap.
    * @param fromIndex
    *   The starting index (inclusive).
    * @param toIndex
    *   The ending index (inclusive).
    */
  def setBit(key: String, fromIndex: Long, toIndex: Long): Task[Unit]

  /** Set the bit at a specific position and return its old value.
    *
    * Similar to the SETBIT command.
    *
    * @param key
    *   The key of the bitmap.
    * @param index
    *   The position to set the bit.
    * @return
    *   The previous value of the bit.
    */
  def setBit(key: String, index: Long): Task[Boolean]

  /** Set or clear the bit at a specific position and return its old value.
    *
    * Similar to the SETBIT command.
    *
    * @param key
    *   The key of the bitmap.
    * @param index
    *   The position to set the bit.
    * @param value
    *   True to set the bit to 1, False to set it to 0.
    * @return
    *   The previous value of the bit.
    */
  def setBit(key: String, index: Long, value: Boolean): Task[Boolean]

  /** Set or clear multiple bits specified by their indices.
    *
    * Similar to the SETBIT command.
    *
    * @param key
    *   The key of the bitmap.
    * @param indexes
    *   The positions to be set or cleared.
    * @param value
    *   True to set the bits to 1, False to clear them to 0.
    */
  def setBit(key: String, indexes: Iterable[Long], value: Boolean): Task[Unit]

  /** Clear the bit at a specific position and return its old value.
    *
    * @param key
    *   The key of the bitmap.
    * @param index
    *   The position to clear the bit.
    * @return
    *   The previous value of the bit.
    */
  def clearBit(key: String, index: Long): Task[Boolean]

  /** Clear a range of bits between two indices.
    *
    * @param key
    *   The key of the bitmap.
    * @param fromIndex
    *   The starting index (inclusive).
    * @param toIndex
    *   The ending index (inclusive).
    */
  def clearBit(key: String, fromIndex: Long, toIndex: Long): Task[Unit]

  /** Clear all bits in a bitmap string.
    *
    * @param key
    *   The key of the bitmap.
    */
  def clearBit(key: String): Task[Unit]
}

trait RedisBitmapOperationsImpl extends RedisBitmapOperations {
  protected val redissonClient: RedissonClient

  private def bitSet(key: String): RBitSet = redissonClient.getBitSet(key)

  override def bitCount(key: String): Task[Long] = ZIO
    .fromCompletionStage(bitSet(key).cardinalityAsync())
    .map(_.toLong)

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
    .map(_.toLong)

  override def getBit(key: String, index: Long): Task[Boolean] = ZIO
    .fromCompletionStage(bitSet(key).getAsync(index))
    .map(Boolean.unbox)

  override def setBit(key: String, fromIndex: Long, toIndex: Long, value: Boolean): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).setAsync(fromIndex, toIndex, value))
    .unit

  override def setBit(key: String, fromIndex: Long, toIndex: Long): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).setAsync(fromIndex, toIndex))
    .unit

  override def setBit(key: String, index: Long): Task[Boolean] = ZIO
    .fromCompletionStage(bitSet(key).setAsync(index))
    .map(Boolean.unbox)

  override def setBit(key: String, index: Long, value: Boolean): Task[Boolean] = ZIO
    .fromCompletionStage(bitSet(key).setAsync(index, value))
    .map(Boolean.unbox)

  override def setBit(key: String, indexes: Iterable[Long], value: Boolean): Task[Unit] = ZIO
    .fromCompletionStage(bitSet(key).setAsync(indexes.toArray, value))
    .unit

  override def clearBit(key: String, index: Long): Task[Boolean] = ZIO
    .fromCompletionStage(bitSet(key).clearAsync(index))
    .map(Boolean.unbox)

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
    ZLayer.fromFunction(RedisBitmapOperationsLive.apply _)

}
