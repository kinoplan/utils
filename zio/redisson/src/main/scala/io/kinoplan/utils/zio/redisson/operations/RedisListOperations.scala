package io.kinoplan.utils.zio.redisson.operations

import java.util.concurrent.TimeUnit

import org.redisson.api.{RBlockingDeque, RBlockingQueue, RDeque, RList, RQueue, RedissonClient}
import org.redisson.api.queue.DequeMoveArgs
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

import io.kinoplan.utils.redisson.codec.base.{BaseRedisDecoder, BaseRedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.operations.base.ResultBuilder._
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders

/** Interface representing operations that can be performed on Redis list data.
  */
trait RedisListOperations {

  /** Atomically returns and removes the first/last element of a list, or block until one is
    * available.
    *
    * Similar to the BLMOVE command.
    *
    * @param source
    *   The source list.
    * @param timeout
    *   Duration to block if no element is present.
    * @param args
    *   Arguments for move operation, specifying source-side and destination-side.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The moved element.
    */
  def blMove[V](source: String, timeout: Duration, args: DequeMoveArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder11[V]

  /** Pop elements from the left of one or multiple lists and block until one is available.
    *
    * Similar to the BLMPOP command.
    *
    * @param key
    *   The main key to pop elements from.
    * @param timeout
    *   Duration to block if no element is present.
    * @param keys
    *   Additional keys to pop elements from.
    * @param count
    *   Number of elements to pop.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Map of keys to their corresponding popped elements.
    */
  def blmPopLeft[V](key: String, timeout: Duration, keys: Seq[String] = Seq.empty, count: Int = 1)(
    implicit
    codec: RCodec[_, V]
  ): ResultBuilder12[V]

  /** Pop elements from the right of one or multiple lists and block until one is available.
    *
    * Similar to the BLMPOP command with RIGHT option.
    *
    * @param key
    *   The main key to pop elements from.
    * @param timeout
    *   Duration to block if no element is present.
    * @param keys
    *   Additional keys to pop elements from.
    * @param count
    *   Number of elements to pop.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Map of keys to their corresponding popped elements.
    */
  def blmPopRight[V](key: String, timeout: Duration, keys: Seq[String] = Seq.empty, count: Int = 1)(
    implicit
    codec: RCodec[_, V]
  ): ResultBuilder12[V]

  /** Pop an element from the left of a list and block until one is available.
    *
    * Similar to the BLPOP command.
    *
    * @param key
    *   The key of the list.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def blPop[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Pop an element from the left of a list and block until one is available or timeout is reached.
    *
    * Similar to the BLPOP command.
    *
    * @param key
    *   The key of the list.
    * @param timeout
    *   Duration to block if no element is present.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def blPop[V](key: String, timeout: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Pop an element from the left of one of multiple lists and block until one is available or
    * timeout is reached.
    *
    * Similar to the BLPOP command.
    *
    * @param key
    *   The primary key of the list.
    * @param timeout
    *   Duration to block if no element is present.
    * @param keys
    *   Additional keys to pop elements from.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def blPop[V](key: String, timeout: Duration, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Pop an element from the right of a list and block until one is available.
    *
    * Similar to the BRPOP command.
    *
    * @param key
    *   The key of the list.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def brPop[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Pop an element from the right of a list and block until one is available or timeout is
    * reached.
    *
    * Similar to the BRPOP command.
    *
    * @param key
    *   The key of the list.
    * @param timeout
    *   Duration to block if no element is present.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def brPop[V](key: String, timeout: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Pop an element from the right of one of multiple lists and block until one is available or
    * timeout is reached.
    *
    * Similar to the BRPOP command.
    *
    * @param key
    *   The primary key of the list.
    * @param timeout
    *   Duration to block if no element is present.
    * @param keys
    *   Additional keys to pop elements from.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def brPop[V](key: String, timeout: Duration, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Pop an element from the right of a list and push it to another list. Block until an element is
    * available or timeout.
    *
    * Similar to the BRPOPLPUSH command.
    *
    * @param source
    *   The source list.
    * @param destination
    *   The destination list.
    * @param timeout
    *   Duration to block if no element is present.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the element moved.
    */
  def brPopLPush[V](source: String, destination: String, timeout: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Get an element from a list by its index.
    *
    * Similar to the LINDEX command.
    *
    * @param key
    *   The key of the list.
    * @param index
    *   The index of the element.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the element, or None if the index is out of range.
    */
  def lIndex[V](key: String, index: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Get elements from a list by their indexes.
    *
    * Similar to multiple LINDEX commands.
    *
    * @param key
    *   The key of the list.
    * @param indexes
    *   The indexes of the elements.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Iterable containing the elements found.
    */
  def lIndex[V](key: String, indexes: Seq[Int])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder13[V]

  /** Insert an element after a specified pivot in the list.
    *
    * Similar to the LINSERT command with AFTER option.
    *
    * @param key
    *   The key of the list.
    * @param pivot
    *   The pivot element.
    * @param element
    *   The element to add after the pivot.
    * @tparam A
    *   Type of the pivot element, requires BaseRedisEncoder[A, V].
    * @tparam B
    *   Type of the element, requires BaseRedisEncoder[B, V].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The length of the list after the insertion.
    */
  def lInsertAfter[A, B, V](key: String, pivot: A, element: B)(implicit
    codec: RCodec[_, V],
    encoderA: BaseRedisEncoder[A, V],
    encoderB: BaseRedisEncoder[B, V]
  ): Task[Int]

  /** Insert an element before a specified pivot in the list.
    *
    * Similar to the LINSERT command with BEFORE option.
    *
    * @param key
    *   The key of the list.
    * @param pivot
    *   The pivot element.
    * @param element
    *   The element to add before the pivot.
    * @tparam A
    *   Type of the pivot element, requires BaseRedisEncoder[A, V].
    * @tparam B
    *   Type of the element, requires BaseRedisEncoder[B, V].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The length of the list after the insertion.
    */
  def lInsertBefore[A, B, V](key: String, pivot: A, element: B)(implicit
    codec: RCodec[_, V],
    encoderA: BaseRedisEncoder[A, V],
    encoderB: BaseRedisEncoder[B, V]
  ): Task[Int]

  /** Get the length of a list.
    *
    * Similar to the LLEN command.
    *
    * @param key
    *   The key of the list.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the list.
    */
  def lLen(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[Int]

  /** Move an element from one list to another.
    *
    * Similar to the LMOVE command.
    *
    * @param source
    *   The source list.
    * @param args
    *   Arguments specifying the source-side and destination-side.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The moved element.
    */
  def lMove[V](source: String, args: DequeMoveArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder11[V]

  /** Pop an element from the left of a list.
    *
    * Similar to the LPOP command.
    *
    * @param key
    *   The key of the list.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def lPop[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Pop multiple elements from the left of a list.
    *
    * Similar to the LPOP command with COUNT option.
    *
    * @param key
    *   The key of the list.
    * @param count
    *   Number of elements to pop.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Iterable of the popped elements.
    */
  def lPop[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Push an element to the front of a list.
    *
    * Similar to the LPUSH command.
    *
    * @param key
    *   The key of the list.
    * @param element
    *   The element to push.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def lPush[T, V](key: String, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit]

  /** Push multiple elements to the front of a list.
    *
    * Similar to the LPUSH command.
    *
    * @param key
    *   The key of the list.
    * @param elements
    *   The elements to push.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The length of the list after the push.
    */
  def lPush[T, V](key: String, elements: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int]

  /** Push an element to the front of a list, only if the list exists.
    *
    * Similar to the LPUSHX command.
    *
    * @param key
    *   The key of the list.
    * @param element
    *   The element to push.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The length of the list after the push.
    */
  def lPushX[T, V](key: String, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int]

  /** Push multiple elements to the front of a list, only if the list exists.
    *
    * Similar to the LPUSHX command.
    *
    * @param key
    *   The key of the list.
    * @param elements
    *   The elements to push.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The length of the list after the push.
    */
  def lPushX[T, V](key: String, elements: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int]

  /** Return a range of elements from a list.
    *
    * Similar to the LRANGE command.
    *
    * @param key
    *   The key of the list.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Iterable of the list's elements.
    */
  def lRange[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Return a range of elements from a list starting from the first element up to the specified
    * stop.
    *
    * Similar to the LRANGE command.
    *
    * @param key
    *   The key of the list.
    * @param stop
    *   The stop index.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Iterable of the list's elements.
    */
  def lRange[V](key: String, stop: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Return a range of elements from a list between the specified start and stop indexes.
    *
    * Similar to the LRANGE command.
    *
    * @param key
    *   The key of the list.
    * @param start
    *   The start index.
    * @param stop
    *   The stop index.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Iterable of the list's elements.
    */
  def lRange[V](key: String, start: Int, stop: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Remove an element by its index.
    *
    * Similar to custom LREM logic.
    *
    * @param key
    *   The key of the list.
    * @param index
    *   The index of the element to remove.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def lRem(key: String, index: Int)(implicit
    codec: RCodec[_, _]
  ): Task[Unit]

  /** Remove elements equal to the specified element from the list.
    *
    * Similar to the LREM command.
    *
    * @param key
    *   The key of the list.
    * @param count
    *   The number of elements to remove.
    * @param element
    *   The element to remove.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def lRem[T, V](key: String, count: Int, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Remove an element by its index and return it.
    *
    * Similar to custom LREM logic with index.
    *
    * @param key
    *   The key of the list.
    * @param index
    *   The index of the element to remove.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the removed element, or None if the index is out of range.
    */
  def lRemAndReturn[V](key: String, index: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Set the element at the specified index of the list.
    *
    * Similar to the LSET command.
    *
    * @param key
    *   The key of the list.
    * @param index
    *   The index to set.
    * @param element
    *   The element to store.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def lSet[T, V](key: String, index: Int, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit]

  /** Set the element at the specified index and return the previous element.
    *
    * Similar to a combination of LSET and retrieval.
    *
    * @param key
    *   The key of the list.
    * @param index
    *   The index of the element to set.
    * @param element
    *   The new element to set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @param decoder
    *   The decoder instance that converts `V` to `T`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the old element, or None if the index is out of range.
    */
  def lSetAndReturn[T, V](key: String, index: Int, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Option[T]]

  /** Trim a list to a specified range.
    *
    * Similar to the LTRIM command.
    *
    * @param key
    *   The key of the list.
    * @param start
    *   The start index.
    * @param stop
    *   The stop index.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def lTrim(key: String, start: Int, stop: Int)(implicit
    codec: RCodec[_, _]
  ): Task[Unit]

  /** Pop an element from the right of a list.
    *
    * Similar to the RPOP command.
    *
    * @param key
    *   The key of the list.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def rPop[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Pop multiple elements from the right of a list.
    *
    * Similar to the RPOP command with COUNT option.
    *
    * @param key
    *   The key of the list.
    * @param count
    *   Number of elements to pop.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   Iterable of the popped elements.
    */
  def rPop[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Pop an element from the right of a list and push it to another list.
    *
    * Similar to the RPOPLPUSH command.
    *
    * @param source
    *   The source list.
    * @param destination
    *   The destination list.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the element moved.
    */
  def rPopLPush[V](source: String, destination: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Push an element to the end of a list.
    *
    * Similar to the RPUSH command.
    *
    * @param key
    *   The key of the list.
    * @param element
    *   The element to push.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    */
  def rPush[T, V](key: String, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit]

  /** Push multiple elements to the end of a list.
    *
    * Similar to the RPUSH command.
    *
    * @param key
    *   The key of the list.
    * @param elements
    *   The elements to push.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The length of the list after the push.
    */
  def rPush[T, V](key: String, elements: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int]

  /** Push an element to the end of a list, only if the list exists.
    *
    * Similar to the RPUSHX command.
    *
    * @param key
    *   The key of the list.
    * @param element
    *   The element to push.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The length of the list after the push.
    */
  def rPushX[T, V](key: String, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int]

  /** Push multiple elements to the end of a list, only if the list exists.
    *
    * Similar to the RPUSHX command.
    *
    * @param key
    *   The key of the list.
    * @param elements
    *   The elements to push.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The length of the list after the push.
    */
  def rPushX[T, V](key: String, elements: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int]

}

trait RedisListOperationsImpl extends RedisListOperations {
  protected val redissonClient: RedissonClient

  private def blockingQueue[V](key: String)(implicit
    codec: RCodec[_, V]
  ): RBlockingQueue[V] = codec
    .underlying
    .map(redissonClient.getBlockingQueue[V](key, _))
    .getOrElse(redissonClient.getBlockingQueue[V](key))

  private def blockingDequeue[V](key: String)(implicit
    codec: RCodec[_, V]
  ): RBlockingDeque[V] = codec
    .underlying
    .map(redissonClient.getBlockingDeque[V](key, _))
    .getOrElse(redissonClient.getBlockingDeque[V](key))

  private def queue[V](key: String)(implicit
    codec: RCodec[_, V]
  ): RQueue[V] = codec
    .underlying
    .map(redissonClient.getQueue[V](key, _))
    .getOrElse(redissonClient.getQueue[V](key))

  private def deque[V](key: String)(implicit
    codec: RCodec[_, V]
  ): RDeque[V] = codec
    .underlying
    .map(redissonClient.getDeque[V](key, _))
    .getOrElse(redissonClient.getDeque[V](key))

  private def list[V](key: String)(implicit
    codec: RCodec[_, V]
  ): RList[V] = codec
    .underlying
    .map(redissonClient.getList[V](key, _))
    .getOrElse(redissonClient.getList[V](key))

  override def blMove[V](source: String, timeout: Duration, args: DequeMoveArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder11[V] = new ResultBuilder11[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[T] = ZIO
      .fromCompletionStage(blockingDequeue(source).moveAsync(timeout, args))
      .flatMap(JavaDecoders.fromValue(_))
  }

  override def blmPopLeft[V](key: String, timeout: Duration, keys: Seq[String], count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder12[V] = new ResultBuilder12[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, List[T]]] = ZIO
      .fromCompletionStage(blockingQueue(key).pollFirstFromAnyAsync(timeout, count, keys: _*))
      .flatMap(JavaDecoders.fromMapListValue(_))
  }

  override def blmPopRight[V](key: String, timeout: Duration, keys: Seq[String], count: Int)(
    implicit
    codec: RCodec[_, V]
  ): ResultBuilder12[V] = new ResultBuilder12[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, List[T]]] = ZIO
      .fromCompletionStage(blockingQueue(key).pollLastFromAnyAsync(timeout, count, keys: _*))
      .flatMap(JavaDecoders.fromMapListValue(_))
  }

  override def blPop[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(blockingQueue(key).takeAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def blPop[V](key: String, timeout: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(blockingQueue(key).pollAsync(timeout.toMillis, TimeUnit.MILLISECONDS))
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def blPop[V](key: String, timeout: Duration, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(
        blockingQueue(key).pollFromAnyAsync(timeout.toMillis, TimeUnit.MILLISECONDS, keys: _*)
      )
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def brPop[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(blockingDequeue(key).takeLastAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def brPop[V](key: String, timeout: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(
        blockingDequeue(key).pollLastAsync(timeout.toMillis, TimeUnit.MILLISECONDS)
      )
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def brPop[V](key: String, timeout: Duration, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(
        blockingDequeue(key).pollLastFromAnyAsync(timeout.toMillis, TimeUnit.MILLISECONDS, keys: _*)
      )
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def brPopLPush[V](source: String, destination: String, timeout: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(
        blockingQueue(source).pollLastAndOfferFirstToAsync(
          destination,
          timeout.toMillis,
          TimeUnit.MILLISECONDS
        )
      )
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def lIndex[V](key: String, index: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(list(key).getAsync(index))
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def lIndex[V](key: String, indexes: Seq[Int])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder13[V] = new ResultBuilder13[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[Option[T]]] = ZIO
      .fromCompletionStage(list(key).getAsync(indexes: _*))
      .flatMap(JavaDecoders.fromCollectionNullable(_))
  }

  override def lInsertAfter[A, B, V](key: String, pivot: A, element: B)(implicit
    codec: RCodec[_, V],
    encoderA: BaseRedisEncoder[A, V],
    encoderB: BaseRedisEncoder[B, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(list(key).addAfterAsync(codec.encode(pivot), codec.encode(element)))
    .map(_.toInt)

  override def lInsertBefore[A, B, V](key: String, pivot: A, element: B)(implicit
    codec: RCodec[_, V],
    encoderA: BaseRedisEncoder[A, V],
    encoderB: BaseRedisEncoder[B, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(list(key).addBeforeAsync(codec.encode(pivot), codec.encode(element)))
    .map(_.toInt)

  override def lLen(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO.fromCompletionStage(list(key).sizeAsync()).map(_.toInt)

  override def lMove[V](source: String, args: DequeMoveArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder11[V] = new ResultBuilder11[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[T] = ZIO
      .fromCompletionStage(deque(source).moveAsync(args))
      .flatMap(JavaDecoders.fromValue(_))
  }

  override def lPop[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(queue(key).pollAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def lPop[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(queue(key).pollAsync(count))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def lPush[T, V](key: String, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = ZIO.fromCompletionStage(deque(key).addFirstAsync(codec.encode(element))).unit

  override def lPush[T, V](key: String, elements: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addFirstAsync(elements.map(codec.encode(_)): _*))
    .map(_.toInt)

  override def lPushX[T, V](key: String, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addFirstIfExistsAsync(codec.encode(element)))
    .map(_.toInt)

  override def lPushX[T, V](key: String, elements: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addFirstIfExistsAsync(elements.map(codec.encode(_)): _*))
    .map(_.toInt)

  override def lRange[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(list(key).readAllAsync())
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def lRange[V](key: String, stop: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(list(key).rangeAsync(stop))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def lRange[V](key: String, start: Int, stop: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(list(key).rangeAsync(start, stop))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def lRem(key: String, index: Int)(implicit
    codec: RCodec[_, _]
  ): Task[Unit] = ZIO.fromCompletionStage(list(key).fastRemoveAsync(index)).unit

  override def lRem[T, V](key: String, count: Int, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(list(key).removeAsync(codec.encode(element), count))
    .map(Boolean.unbox)

  override def lRemAndReturn[V](key: String, index: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(list(key).removeAsync(index))
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def lSet[T, V](key: String, index: Int, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = ZIO.fromCompletionStage(list(key).fastSetAsync(index, codec.encode(element))).unit

  override def lSetAndReturn[T, V](key: String, index: Int, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(list(key).setAsync(index, codec.encode(element)))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def lTrim(key: String, start: Int, stop: Int)(implicit
    codec: RCodec[_, _]
  ): Task[Unit] = ZIO.fromCompletionStage(list(key).trimAsync(start, stop)).unit

  override def rPop[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(deque(key).removeLastAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def rPop[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(deque(key).pollLastAsync(count))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def rPopLPush[V](source: String, destination: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(deque(source).pollLastAndOfferFirstToAsync(destination))
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def rPush[T, V](key: String, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Unit] = ZIO.fromCompletionStage(deque(key).addLastAsync(codec.encode(element))).unit

  override def rPush[T, V](key: String, elements: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addLastAsync(elements.map(codec.encode(_)): _*))
    .map(_.toInt)

  override def rPushX[T, V](key: String, element: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addLastIfExistsAsync(codec.encode(element)))
    .map(_.toInt)

  override def rPushX[T, V](key: String, elements: Seq[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addLastIfExistsAsync(elements.map(codec.encode(_)): _*))
    .map(_.toInt)

}

case class RedisListOperationsLive(redissonClient: RedissonClient) extends RedisListOperationsImpl

object RedisListOperations {

  val live: URLayer[RedissonClient, RedisListOperations] =
    ZLayer.fromFunction(RedisListOperationsLive.apply _)

}
