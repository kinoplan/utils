package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.RedisEncoder
import io.kinoplan.utils.zio.redisson.codec.RCodec
import org.redisson.api.{RHyperLogLog, RedissonClient}
import zio.{Task, URLayer, ZIO, ZLayer}

import scala.jdk.CollectionConverters.IterableHasAsJava

/** Interface representing operations that can be performed on Redis HyperLogLog data.
  */
trait RedisHyperLogLogOperations {

  /** Adds an element to the HyperLogLog data structure.
    *
    * Similar to the PFADD command.
    *
    * @param key
    *   The key of the HyperLogLog data structure.
    * @param element
    *   The element to add.
    * @tparam T
    *   Type of the element, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def pfAdd[T: RedisEncoder](key: String, element: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Adds multiple elements to the HyperLogLog data structure.
    *
    * Similar to the PFADD command.
    *
    * @param key
    *   The key of the HyperLogLog data structure.
    * @param elements
    *   The elements to add.
    * @tparam T
    *   Type of the elements, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def pfAdd[T: RedisEncoder](key: String, elements: Iterable[T])(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Returns the approximated cardinality of the set(s) observed by the HyperLogLog at key.
    *
    * Similar to the PFCOUNT command.
    *
    * @param key
    *   The key of the HyperLogLog data structure.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The approximated number of unique elements observed.
    */
  def pfCount(key: String)(implicit
    codec: RCodec
  ): Task[Long]

  /** Returns the approximated cardinality of the union of the observed sets of the provided
    * HyperLogLogs.
    *
    * Similar to the PFCOUNT command.
    *
    * @param key
    *   The primary key of the HyperLogLog data structure.
    * @param keys
    *   Additional keys of HyperLogLogs to include in the cardinality count.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The approximated number of unique elements observed across the specified HyperLogLogs.
    */
  def pfCount(key: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Long]

  /** Merges multiple HyperLogLog data structures into a single one.
    *
    * Similar to the PFMERGE command.
    *
    * @param key
    *   The destination key where the result of the merge is stored.
    * @param keys
    *   The keys of the HyperLogLog data structures to merge.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def pfMerge(key: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Unit]

}

trait RedisHyperLogLogOperationsImpl extends RedisHyperLogLogOperations {

  protected val redissonClient: RedissonClient

  private def hyperLogLog(key: String)(implicit
    codec: RCodec
  ): RHyperLogLog[String] = codec
    .underlying
    .map(redissonClient.getHyperLogLog[String](key, _))
    .getOrElse(redissonClient.getHyperLogLog[String](key))

  override def pfAdd[T: RedisEncoder](key: String, element: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(hyperLogLog(key).addAsync(RedisEncoder[T].encode(element)))
    .map(Boolean.unbox)

  override def pfAdd[T: RedisEncoder](key: String, elements: Iterable[T])(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(
      hyperLogLog(key).addAllAsync(elements.map(RedisEncoder[T].encode).asJavaCollection)
    )
    .map(Boolean.unbox)

  override def pfCount(key: String)(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(hyperLogLog(key).countAsync()).map(_.toLong)

  override def pfCount(key: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Long] = ZIO.fromCompletionStage(hyperLogLog(key).countWithAsync(keys: _*)).map(_.toLong)

  override def pfMerge(key: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Unit] = ZIO.fromCompletionStage(hyperLogLog(key).mergeWithAsync(keys: _*)).unit

}

case class RedisHyperLogLogOperationsLive(redissonClient: RedissonClient)
    extends RedisHyperLogLogOperationsImpl

object RedisHyperLogLogOperations {

  val live: URLayer[RedissonClient, RedisHyperLogLogOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisHyperLogLogOperationsLive))

}
