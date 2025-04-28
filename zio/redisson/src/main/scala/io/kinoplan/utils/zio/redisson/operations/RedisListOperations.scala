package io.kinoplan.utils.zio.redisson.operations

import java.util.concurrent.TimeUnit
import org.redisson.api.{RBlockingDeque, RBlockingQueue, RDeque, RList, RQueue, RedissonClient}
import org.redisson.api.queue.DequeMoveArgs
import zio.{Duration, Task, URLayer, ZIO, ZLayer}
import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders

/** Interface representing operations that can be performed on Redis list keys.
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
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The moved element.
    */
  def blMove[T: RedisDecoder](source: String, timeout: Duration, args: DequeMoveArgs)(implicit
    codec: RCodec
  ): Task[T]

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
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Map of keys to their corresponding popped elements.
    */
  def blmPopLeft[T: RedisDecoder](
    key: String,
    timeout: Duration,
    keys: Seq[String] = Seq.empty,
    count: Int = 1
  )(implicit
    codec: RCodec
  ): Task[Map[String, List[T]]]

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
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Map of keys to their corresponding popped elements.
    */
  def blmPopRight[T: RedisDecoder](
    key: String,
    timeout: Duration,
    keys: Seq[String] = Seq.empty,
    count: Int = 1
  )(implicit
    codec: RCodec
  ): Task[Map[String, List[T]]]

  /** Pop an element from the left of a list and block until one is available.
    *
    * Similar to the BLPOP command.
    *
    * @param key
    *   The key of the list.
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def blPop[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Pop an element from the left of a list and block until one is available or timeout is reached.
    *
    * Similar to the BLPOP command.
    *
    * @param key
    *   The key of the list.
    * @param timeout
    *   Duration to block if no element is present.
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def blPop[T: RedisDecoder](key: String, timeout: Duration)(implicit
    codec: RCodec
  ): Task[Option[T]]

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
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def blPop[T: RedisDecoder](key: String, timeout: Duration, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Pop an element from the right of a list and block until one is available.
    *
    * Similar to the BRPOP command.
    *
    * @param key
    *   The key of the list.
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def brPop[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Pop an element from the right of a list and block until one is available or timeout is
    * reached.
    *
    * Similar to the BRPOP command.
    *
    * @param key
    *   The key of the list.
    * @param timeout
    *   Duration to block if no element is present.
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def brPop[T: RedisDecoder](key: String, timeout: Duration)(implicit
    codec: RCodec
  ): Task[Option[T]]

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
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def brPop[T: RedisDecoder](key: String, timeout: Duration, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Option[T]]

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
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the element moved.
    */
  def brPopLPush[T: RedisDecoder](source: String, destination: String, timeout: Duration)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Get an element from a list by its index.
    *
    * Similar to the LINDEX command.
    *
    * @param key
    *   The key of the list.
    * @param index
    *   The index of the element.
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the element, or None if the index is out of range.
    */
  def lIndex[T: RedisDecoder](key: String, index: Int)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Get elements from a list by their indexes.
    *
    * Similar to multiple LINDEX commands.
    *
    * @param key
    *   The key of the list.
    * @param indexes
    *   The indexes of the elements.
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Iterable containing the elements found.
    */
  def lIndex[T: RedisDecoder](key: String, indexes: Seq[Int])(implicit
    codec: RCodec
  ): Task[Iterable[Option[T]]]

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
    *   Type of the pivot element, requires RedisEncoder[A].
    * @tparam B
    *   Type of the element, requires RedisEncoder[B].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the list after the insertion.
    */
  def lInsertAfter[A: RedisEncoder, B: RedisEncoder](key: String, pivot: A, element: B)(implicit
    codec: RCodec
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
    *   Type of the pivot element, requires RedisEncoder[A].
    * @tparam B
    *   Type of the element, requires RedisEncoder[B].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the list after the insertion.
    */
  def lInsertBefore[A: RedisEncoder, B: RedisEncoder](key: String, pivot: A, element: B)(implicit
    codec: RCodec
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
    codec: RCodec
  ): Task[Int]

  /** Move an element from one list to another.
    *
    * Similar to the LMOVE command.
    *
    * @param source
    *   The source list.
    * @param args
    *   Arguments specifying the source-side and destination-side.
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The moved element.
    */
  def lMove[T: RedisDecoder](source: String, args: DequeMoveArgs)(implicit
    codec: RCodec
  ): Task[T]

  /** Pop an element from the left of a list.
    *
    * Similar to the LPOP command.
    *
    * @param key
    *   The key of the list.
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def lPop[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Pop multiple elements from the left of a list.
    *
    * Similar to the LPOP command with COUNT option.
    *
    * @param key
    *   The key of the list.
    * @param count
    *   Number of elements to pop.
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Iterable of the popped elements.
    */
  def lPop[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Iterable[T]]

  /** Push an element to the front of a list.
    *
    * Similar to the LPUSH command.
    *
    * @param key
    *   The key of the list.
    * @param element
    *   The element to push.
    * @tparam T
    *   Type of the element, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def lPush[T: RedisEncoder](key: String, element: T)(implicit
    codec: RCodec
  ): Task[Unit]

  /** Push multiple elements to the front of a list.
    *
    * Similar to the LPUSH command.
    *
    * @param key
    *   The key of the list.
    * @param elements
    *   The elements to push.
    * @tparam T
    *   Type of the elements, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the list after the push.
    */
  def lPush[T: RedisEncoder](key: String, elements: Seq[T])(implicit
    codec: RCodec
  ): Task[Int]

  /** Push an element to the front of a list, only if the list exists.
    *
    * Similar to the LPUSHX command.
    *
    * @param key
    *   The key of the list.
    * @param element
    *   The element to push.
    * @tparam T
    *   Type of the element, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the list after the push.
    */
  def lPushX[T: RedisEncoder](key: String, element: T)(implicit
    codec: RCodec
  ): Task[Int]

  /** Push multiple elements to the front of a list, only if the list exists.
    *
    * Similar to the LPUSHX command.
    *
    * @param key
    *   The key of the list.
    * @param elements
    *   The elements to push.
    * @tparam T
    *   Type of the elements, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the list after the push.
    */
  def lPushX[T: RedisEncoder](key: String, elements: Seq[T])(implicit
    codec: RCodec
  ): Task[Int]

  /** Return a range of elements from a list.
    *
    * Similar to the LRANGE command.
    *
    * @param key
    *   The key of the list.
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Iterable of the list's elements.
    */
  def lRange[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Iterable[T]]

  /** Return a range of elements from a list starting from the first element up to the specified
    * stop.
    *
    * Similar to the LRANGE command.
    *
    * @param key
    *   The key of the list.
    * @param stop
    *   The stop index.
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Iterable of the list's elements.
    */
  def lRange[T: RedisDecoder](key: String, stop: Int)(implicit
    codec: RCodec
  ): Task[Iterable[T]]

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
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Iterable of the list's elements.
    */
  def lRange[T: RedisDecoder](key: String, start: Int, stop: Int)(implicit
    codec: RCodec
  ): Task[Iterable[T]]

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
    codec: RCodec
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
    * @tparam T
    *   Type of the element, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def lRem[T: RedisEncoder](key: String, count: Int, element: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Remove an element by its index and return it.
    *
    * Similar to custom LREM logic with index.
    *
    * @param key
    *   The key of the list.
    * @param index
    *   The index of the element to remove.
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the removed element, or None if the index is out of range.
    */
  def lRemAndReturn[T: RedisDecoder](key: String, index: Int)(implicit
    codec: RCodec
  ): Task[Option[T]]

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
    * @tparam T
    *   Type of the element, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def lSet[T: RedisEncoder](key: String, index: Int, element: T)(implicit
    codec: RCodec
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
    * @tparam T
    *   Type of the element, requires RedisEncoder[T] and RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the old element, or None if the index is out of range.
    */
  def lSetAndReturn[T: RedisEncoder: RedisDecoder](key: String, index: Int, element: T)(implicit
    codec: RCodec
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
    codec: RCodec
  ): Task[Unit]

  /** Pop an element from the right of a list.
    *
    * Similar to the RPOP command.
    *
    * @param key
    *   The key of the list.
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the popped element, or None if the key does not exist.
    */
  def rPop[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Pop multiple elements from the right of a list.
    *
    * Similar to the RPOP command with COUNT option.
    *
    * @param key
    *   The key of the list.
    * @param count
    *   Number of elements to pop.
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Iterable of the popped elements.
    */
  def rPop[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Iterable[T]]

  /** Pop an element from the right of a list and push it to another list.
    *
    * Similar to the RPOPLPUSH command.
    *
    * @param source
    *   The source list.
    * @param destination
    *   The destination list.
    * @tparam T
    *   Type of the element, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the element moved.
    */
  def rPopLPush[T: RedisDecoder](source: String, destination: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Push an element to the end of a list.
    *
    * Similar to the RPUSH command.
    *
    * @param key
    *   The key of the list.
    * @param element
    *   The element to push.
    * @tparam T
    *   Type of the element, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    */
  def rPush[T: RedisEncoder](key: String, element: T)(implicit
    codec: RCodec
  ): Task[Unit]

  /** Push multiple elements to the end of a list.
    *
    * Similar to the RPUSH command.
    *
    * @param key
    *   The key of the list.
    * @param elements
    *   The elements to push.
    * @tparam T
    *   Type of the elements, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the list after the push.
    */
  def rPush[T: RedisEncoder](key: String, elements: Seq[T])(implicit
    codec: RCodec
  ): Task[Int]

  /** Push an element to the end of a list, only if the list exists.
    *
    * Similar to the RPUSHX command.
    *
    * @param key
    *   The key of the list.
    * @param element
    *   The element to push.
    * @tparam T
    *   Type of the element, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the list after the push.
    */
  def rPushX[T: RedisEncoder](key: String, element: T)(implicit
    codec: RCodec
  ): Task[Int]

  /** Push multiple elements to the end of a list, only if the list exists.
    *
    * Similar to the RPUSHX command.
    *
    * @param key
    *   The key of the list.
    * @param elements
    *   The elements to push.
    * @tparam T
    *   Type of the elements, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The length of the list after the push.
    */
  def rPushX[T: RedisEncoder](key: String, elements: Seq[T])(implicit
    codec: RCodec
  ): Task[Int]

}

trait RedisListOperationsImpl extends RedisListOperations {
  protected val redissonClient: RedissonClient

  private def blockingQueue(key: String)(implicit
    codec: RCodec
  ): RBlockingQueue[String] = codec
    .underlying
    .map(redissonClient.getBlockingQueue[String](key, _))
    .getOrElse(redissonClient.getBlockingQueue[String](key))

  private def blockingDequeue(key: String)(implicit
    codec: RCodec
  ): RBlockingDeque[String] = codec
    .underlying
    .map(redissonClient.getBlockingDeque[String](key, _))
    .getOrElse(redissonClient.getBlockingDeque[String](key))

  private def queue(key: String)(implicit
    codec: RCodec
  ): RQueue[String] = codec
    .underlying
    .map(redissonClient.getQueue[String](key, _))
    .getOrElse(redissonClient.getQueue[String](key))

  private def deque(key: String)(implicit
    codec: RCodec
  ): RDeque[String] = codec
    .underlying
    .map(redissonClient.getDeque[String](key, _))
    .getOrElse(redissonClient.getDeque[String](key))

  private def list(key: String)(implicit
    codec: RCodec
  ): RList[String] = codec
    .underlying
    .map(redissonClient.getList[String](key, _))
    .getOrElse(redissonClient.getList[String](key))

  override def blMove[T: RedisDecoder](source: String, timeout: Duration, args: DequeMoveArgs)(
    implicit
    codec: RCodec
  ): Task[T] = ZIO
    .fromCompletionStage(blockingDequeue(source).moveAsync(timeout, args))
    .flatMap(JavaDecoders.fromValue(_))

  override def blmPopLeft[T: RedisDecoder](
    key: String,
    timeout: Duration,
    keys: Seq[String],
    count: Int
  )(implicit
    codec: RCodec
  ): Task[Map[String, List[T]]] = ZIO
    .fromCompletionStage(blockingQueue(key).pollFirstFromAnyAsync(timeout, count, keys: _*))
    .flatMap(JavaDecoders.fromMapListValue(_))

  override def blmPopRight[T: RedisDecoder](
    key: String,
    timeout: Duration,
    keys: Seq[String],
    count: Int
  )(implicit
    codec: RCodec
  ): Task[Map[String, List[T]]] = ZIO
    .fromCompletionStage(blockingQueue(key).pollLastFromAnyAsync(timeout, count, keys: _*))
    .flatMap(JavaDecoders.fromMapListValue(_))

  override def blPop[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(blockingQueue(key).takeAsync())
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def blPop[T: RedisDecoder](key: String, timeout: Duration)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(blockingQueue(key).pollAsync(timeout.toMillis, TimeUnit.MILLISECONDS))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def blPop[T: RedisDecoder](key: String, timeout: Duration, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(
      blockingQueue(key).pollFromAnyAsync(timeout.toMillis, TimeUnit.MILLISECONDS, keys: _*)
    )
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def brPop[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(blockingDequeue(key).takeLastAsync())
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def brPop[T: RedisDecoder](key: String, timeout: Duration)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(blockingDequeue(key).pollLastAsync(timeout.toMillis, TimeUnit.MILLISECONDS))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def brPop[T: RedisDecoder](key: String, timeout: Duration, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(
      blockingDequeue(key).pollLastFromAnyAsync(timeout.toMillis, TimeUnit.MILLISECONDS, keys: _*)
    )
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def brPopLPush[T: RedisDecoder](source: String, destination: String, timeout: Duration)(
    implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(
      blockingQueue(source).pollLastAndOfferFirstToAsync(
        destination,
        timeout.toMillis,
        TimeUnit.MILLISECONDS
      )
    )
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def lIndex[T: RedisDecoder](key: String, index: Int)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(list(key).getAsync(index))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def lIndex[T: RedisDecoder](key: String, indexes: Seq[Int])(implicit
    codec: RCodec
  ): Task[Iterable[Option[T]]] = ZIO
    .fromCompletionStage(list(key).getAsync(indexes: _*))
    .flatMap(JavaDecoders.fromCollectionNullable(_))

  override def lInsertAfter[A: RedisEncoder, B: RedisEncoder](key: String, pivot: A, element: B)(
    implicit
    codec: RCodec
  ): Task[Int] = ZIO
    .fromCompletionStage(
      list(key).addAfterAsync(RedisEncoder[A].encode(pivot), RedisEncoder[B].encode(element))
    )
    .map(_.toInt)

  override def lInsertBefore[A: RedisEncoder, B: RedisEncoder](key: String, pivot: A, element: B)(
    implicit
    codec: RCodec
  ): Task[Int] = ZIO
    .fromCompletionStage(
      list(key).addBeforeAsync(RedisEncoder[A].encode(pivot), RedisEncoder[B].encode(element))
    )
    .map(_.toInt)

  override def lLen(key: String)(implicit
    codec: RCodec
  ): Task[Int] = ZIO.fromCompletionStage(list(key).sizeAsync()).map(_.toInt)

  override def lMove[T: RedisDecoder](source: String, args: DequeMoveArgs)(implicit
    codec: RCodec
  ): Task[T] = ZIO
    .fromCompletionStage(deque(source).moveAsync(args))
    .flatMap(JavaDecoders.fromValue(_))

  override def lPop[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(queue(key).pollAsync())
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def lPop[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(queue(key).pollAsync(count))
    .flatMap(JavaDecoders.fromCollection(_))

  override def lPush[T: RedisEncoder](key: String, element: T)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(deque(key).addFirstAsync(RedisEncoder[T].encode(element)))
    .unit

  override def lPush[T: RedisEncoder](key: String, elements: Seq[T])(implicit
    codec: RCodec
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addFirstAsync(elements.map(RedisEncoder[T].encode): _*))
    .map(_.toInt)

  override def lPushX[T: RedisEncoder](key: String, element: T)(implicit
    codec: RCodec
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addFirstIfExistsAsync(RedisEncoder[T].encode(element)))
    .map(_.toInt)

  override def lPushX[T: RedisEncoder](key: String, elements: Seq[T])(implicit
    codec: RCodec
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addFirstIfExistsAsync(elements.map(RedisEncoder[T].encode): _*))
    .map(_.toInt)

  override def lRange[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(list(key).readAllAsync())
    .flatMap(JavaDecoders.fromCollection(_))

  override def lRange[T: RedisDecoder](key: String, stop: Int)(implicit
    codec: RCodec
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(list(key).rangeAsync(stop))
    .flatMap(JavaDecoders.fromCollection(_))

  override def lRange[T: RedisDecoder](key: String, start: Int, stop: Int)(implicit
    codec: RCodec
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(list(key).rangeAsync(start, stop))
    .flatMap(JavaDecoders.fromCollection(_))

  override def lRem(key: String, index: Int)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO.fromCompletionStage(list(key).fastRemoveAsync(index)).unit

  override def lRem[T: RedisEncoder](key: String, count: Int, element: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO.attempt(list(key).remove(RedisEncoder[T].encode(element), count))

  override def lRemAndReturn[T: RedisDecoder](key: String, index: Int)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(list(key).removeAsync(index))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def lSet[T: RedisEncoder](key: String, index: Int, element: T)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(list(key).fastSetAsync(index, RedisEncoder[T].encode(element)))
    .unit

  override def lSetAndReturn[T: RedisEncoder: RedisDecoder](key: String, index: Int, element: T)(
    implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(list(key).setAsync(index, RedisEncoder[T].encode(element)))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def lTrim(key: String, start: Int, stop: Int)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO.fromCompletionStage(list(key).trimAsync(start, stop)).unit

  override def rPop[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(deque(key).removeLastAsync())
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def rPop[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(deque(key).pollLastAsync(count))
    .flatMap(JavaDecoders.fromCollection(_))

  override def rPopLPush[T: RedisDecoder](source: String, destination: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(deque(source).pollLastAndOfferFirstToAsync(destination))
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def rPush[T: RedisEncoder](key: String, element: T)(implicit
    codec: RCodec
  ): Task[Unit] = ZIO
    .fromCompletionStage(deque(key).addLastAsync(RedisEncoder[T].encode(element)))
    .unit

  override def rPush[T: RedisEncoder](key: String, elements: Seq[T])(implicit
    codec: RCodec
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addLastAsync(elements.map(RedisEncoder[T].encode): _*))
    .map(_.toInt)

  override def rPushX[T: RedisEncoder](key: String, element: T)(implicit
    codec: RCodec
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addLastIfExistsAsync(RedisEncoder[T].encode(element)))
    .map(_.toInt)

  override def rPushX[T: RedisEncoder](key: String, elements: Seq[T])(implicit
    codec: RCodec
  ): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addLastIfExistsAsync(elements.map(RedisEncoder[T].encode): _*))
    .map(_.toInt)

}

case class RedisListOperationsLive(redissonClient: RedissonClient) extends RedisListOperationsImpl

object RedisListOperations {

  val live: URLayer[RedissonClient, RedisListOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisListOperationsLive))

}
