package io.kinoplan.utils.zio.redisson.operations

import java.util.concurrent.TimeUnit
import org.redisson.api.{RBlockingDeque, RBlockingQueue, RDeque, RList, RQueue, RedissonClient}
import org.redisson.api.queue.DequeMoveArgs
import org.redisson.client.codec.StringCodec
import zio.{Duration, Task, URLayer, ZIO, ZLayer}
import zio.macros.accessible
import io.kinoplan.utils.redisson.base.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.redisson.crossCollectionConverters._
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders

@accessible
trait RedisListOperations {
  def blMove[T: RedisDecoder](source: String, timeout: Duration, args: DequeMoveArgs): Task[T]

  def blmPopLeft[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    keys: String*
  ): Task[Map[String, List[T]]]

  def blmPopLeft[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    keys: Set[String]
  ): Task[Map[String, List[T]]]

  def blmPopRight[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    keys: String*
  ): Task[Map[String, List[T]]]

  def blmPopRight[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    keys: Set[String]
  ): Task[Map[String, List[T]]]

  def blPop[T: RedisDecoder](key: String): Task[Option[T]]

  def blPop[T: RedisDecoder](key: String, timeout: Duration): Task[Option[T]]

  def blPop[T: RedisDecoder](key: String, timeout: Duration, keys: String*): Task[Option[T]]

  def blPop[T: RedisDecoder](key: String, timeout: Duration, keys: Set[String]): Task[Option[T]]

  def brPop[T: RedisDecoder](key: String): Task[Option[T]]

  def brPopLPush[T: RedisDecoder](source: String, destination: String): Task[Option[T]]

  def lIndex[T: RedisDecoder](key: String, index: Int): Task[Option[T]]

  def lIndex[T: RedisDecoder](key: String, indexes: Int*): Task[Iterable[T]]

  def lIndex[T: RedisDecoder](key: String, indexes: Set[Int]): Task[Iterable[T]]

  def lInsertAfter[A: RedisEncoder, B: RedisEncoder](key: String, pivot: A, element: B): Task[Int]

  def lInsertBefore[A: RedisEncoder, B: RedisEncoder](key: String, pivot: A, element: B): Task[Int]

  def lLen(key: String): Task[Int]

  def lMove[T: RedisDecoder](source: String, args: DequeMoveArgs): Task[T]

  def lPop[T: RedisDecoder](key: String): Task[Option[T]]

  def lPop[T: RedisDecoder](key: String, limit: Int): Task[Iterable[T]]

  def lPush[T: RedisEncoder](key: String, elements: T*): Task[Int]

  def lPush[T: RedisEncoder](key: String, elements: List[T]): Task[Int]

  def lPushX[T: RedisEncoder](key: String, elements: T*): Task[Int]

  def lPushX[T: RedisEncoder](key: String, elements: List[T]): Task[Int]

  def lRange[T: RedisDecoder](key: String): Task[Iterable[T]]

  def lRange[T: RedisDecoder](key: String, stop: Int): Task[Iterable[T]]

  def lRange[T: RedisDecoder](key: String, start: Int, stop: Int): Task[Iterable[T]]

  def lRem(key: String, index: Int): Task[Unit]

  def lRem[T: RedisEncoder](key: String, count: Int, element: T): Task[Boolean]

  def lRemAndReturn[T: RedisDecoder](key: String, index: Int): Task[Option[T]]

  def lSet[T: RedisEncoder](key: String, index: Int, element: T): Task[Unit]

  def lSetAndReturnPrevious[T: RedisEncoder: RedisDecoder](
    key: String,
    index: Int,
    element: T
  ): Task[Option[T]]

  def lTrim(key: String, start: Int, stop: Int): Task[Unit]

  def rPop[T: RedisDecoder](key: String): Task[Option[T]]

  def rPop[T: RedisDecoder](key: String, count: Int): Task[Iterable[T]]

  def rPopLPush[T: RedisDecoder](source: String, destination: String): Task[Option[T]]

  def rPush[T: RedisEncoder](key: String, index: Int, element: T): Task[Boolean]

  def rPush[T: RedisEncoder](key: String, index: Int, elements: T*): Task[Boolean]

  def rPush[T: RedisEncoder](key: String, index: Int, elements: List[T]): Task[Boolean]

  def rPush[T: RedisEncoder](key: String, elements: T*): Task[Boolean]

  def rPush[T: RedisEncoder](key: String, elements: List[T]): Task[Boolean]

  def rPushX[T: RedisEncoder](key: String, elements: T*): Task[Int]

  def rPushX[T: RedisEncoder](key: String, elements: List[T]): Task[Int]
}

trait RedisListOperationsImpl extends RedisListOperations {
  protected val redissonClient: RedissonClient

  private lazy val blockingQueue: String => RBlockingQueue[String] =
    redissonClient.getBlockingQueue[String](_, StringCodec.INSTANCE)

  private lazy val blockingDequeue: String => RBlockingDeque[String] =
    redissonClient.getBlockingDeque[String](_, StringCodec.INSTANCE)

  private lazy val queue: String => RQueue[String] =
    redissonClient.getQueue[String](_, StringCodec.INSTANCE)

  private lazy val deque: String => RDeque[String] =
    redissonClient.getDeque[String](_, StringCodec.INSTANCE)

  private lazy val list: String => RList[String] =
    redissonClient.getList[String](_, StringCodec.INSTANCE)

  override def blMove[T: RedisDecoder](
    source: String,
    timeout: Duration,
    args: DequeMoveArgs
  ): Task[T] = ZIO
    .fromCompletionStage(blockingDequeue(source).moveAsync(timeout, args))
    .flatMap(JavaDecoders.decodeValue(_))

  override def blmPopLeft[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    keys: String*
  ): Task[Map[String, List[T]]] = blmPopLeft(key, timeout, count, keys.toSet)

  override def blmPopLeft[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    keys: Set[String]
  ): Task[Map[String, List[T]]] = ZIO
    .fromCompletionStage(blockingQueue(key).pollFirstFromAnyAsync(timeout, count, keys.toSeq: _*))
    .flatMap(JavaDecoders.decodeMapListValue(_))

  override def blmPopRight[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    keys: String*
  ): Task[Map[String, List[T]]] = blmPopRight(key, timeout, count, keys.toSet)

  override def blmPopRight[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    keys: Set[String]
  ): Task[Map[String, List[T]]] = ZIO
    .fromCompletionStage(blockingQueue(key).pollLastFromAnyAsync(timeout, count, keys.toSeq: _*))
    .flatMap(JavaDecoders.decodeMapListValue(_))

  override def blPop[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(blockingQueue(key).takeAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def blPop[T: RedisDecoder](key: String, timeout: Duration): Task[Option[T]] = ZIO
    .fromCompletionStage(blockingQueue(key).pollAsync(timeout.toMillis, TimeUnit.MILLISECONDS))
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def blPop[T: RedisDecoder](key: String, timeout: Duration, keys: String*): Task[Option[T]] =
    blPop(key, timeout, keys.toSet)

  override def blPop[T: RedisDecoder](
    key: String,
    timeout: Duration,
    keys: Set[String]
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(
      blockingQueue(key).pollFromAnyAsync(timeout.toMillis, TimeUnit.MILLISECONDS, keys.toSeq: _*)
    )
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def brPop[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(blockingDequeue(key).takeLastAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def brPopLPush[T: RedisDecoder](source: String, destination: String): Task[Option[T]] =
    ZIO
      .fromCompletionStage(blockingQueue(source).pollLastAndOfferFirstToAsync(destination))
      .flatMap(JavaDecoders.decodeNullableValue(_))

  override def lIndex[T: RedisDecoder](key: String, index: Int): Task[Option[T]] = ZIO
    .fromCompletionStage(list(key).getAsync(index))
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def lIndex[T: RedisDecoder](key: String, indexes: Int*): Task[Iterable[T]] =
    lIndex(key, indexes.toSet)

  override def lIndex[T: RedisDecoder](key: String, indexes: Set[Int]): Task[Iterable[T]] = ZIO
    .fromCompletionStage(list(key).getAsync(indexes.toSeq: _*))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def lInsertAfter[A: RedisEncoder, B: RedisEncoder](
    key: String,
    pivot: A,
    element: B
  ): Task[Int] = ZIO
    .fromCompletionStage(
      list(key).addAfterAsync(RedisEncoder[A].encode(pivot), RedisEncoder[B].encode(element))
    )
    .map(_.toInt)

  override def lInsertBefore[A: RedisEncoder, B: RedisEncoder](
    key: String,
    pivot: A,
    element: B
  ): Task[Int] = ZIO
    .fromCompletionStage(
      list(key).addBeforeAsync(RedisEncoder[A].encode(pivot), RedisEncoder[B].encode(element))
    )
    .map(_.toInt)

  override def lLen(key: String): Task[Int] = ZIO
    .fromCompletionStage(list(key).sizeAsync())
    .map(_.toInt)

  override def lMove[T: RedisDecoder](source: String, args: DequeMoveArgs): Task[T] = ZIO
    .fromCompletionStage(deque(source).moveAsync(args))
    .flatMap(JavaDecoders.decodeValue(_))

  override def lPop[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(queue(key).pollAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def lPop[T: RedisDecoder](key: String, limit: Int): Task[Iterable[T]] = ZIO
    .fromCompletionStage(queue(key).pollAsync(limit))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def lPush[T: RedisEncoder](key: String, elements: T*): Task[Int] =
    lPush(key, elements.toList)

  override def lPush[T: RedisEncoder](key: String, elements: List[T]): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addFirstAsync(elements.map(RedisEncoder[T].encode): _*))
    .map(_.toInt)

  override def lPushX[T: RedisEncoder](key: String, elements: T*): Task[Int] =
    lPushX(key, elements.toList)

  override def lPushX[T: RedisEncoder](key: String, elements: List[T]): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addFirstIfExistsAsync(elements.map(RedisEncoder[T].encode): _*))
    .map(_.toInt)

  override def lRange[T: RedisDecoder](key: String): Task[Iterable[T]] = ZIO
    .fromCompletionStage(list(key).readAllAsync())
    .flatMap(JavaDecoders.decodeCollection(_))

  override def lRange[T: RedisDecoder](key: String, stop: Int): Task[Iterable[T]] = ZIO
    .fromCompletionStage(list(key).rangeAsync(stop))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def lRange[T: RedisDecoder](key: String, start: Int, stop: Int): Task[Iterable[T]] = ZIO
    .fromCompletionStage(list(key).rangeAsync(start, stop))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def lRem(key: String, index: Int): Task[Unit] = ZIO
    .fromCompletionStage(list(key).fastRemoveAsync(index))
    .unit

  override def lRem[T: RedisEncoder](key: String, count: Int, element: T): Task[Boolean] = ZIO
    .attempt(list(key).remove(RedisEncoder[T].encode(element), count))

  override def lRemAndReturn[T: RedisDecoder](key: String, index: Int): Task[Option[T]] = ZIO
    .fromCompletionStage(list(key).removeAsync(index))
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def lSet[T: RedisEncoder](key: String, index: Int, element: T): Task[Unit] = ZIO
    .fromCompletionStage(list(key).fastSetAsync(index, RedisEncoder[T].encode(element)))
    .unit

  override def lSetAndReturnPrevious[T: RedisEncoder: RedisDecoder](
    key: String,
    index: Int,
    element: T
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(list(key).setAsync(index, RedisEncoder[T].encode(element)))
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def lTrim(key: String, start: Int, stop: Int): Task[Unit] = ZIO
    .fromCompletionStage(list(key).trimAsync(start, stop))
    .unit

  override def rPop[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(deque(key).removeLastAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def rPop[T: RedisDecoder](key: String, count: Int): Task[Iterable[T]] = ZIO
    .fromCompletionStage(deque(key).pollLastAsync(count))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def rPopLPush[T: RedisDecoder](source: String, destination: String): Task[Option[T]] =
    ZIO
      .fromCompletionStage(deque(source).pollLastAndOfferFirstToAsync(destination))
      .flatMap(JavaDecoders.decodeNullableValue(_))

  override def rPush[T: RedisEncoder](key: String, index: Int, element: T): Task[Boolean] = ZIO
    .fromCompletionStage(list(key).addAsync(index, RedisEncoder[T].encode(element)))
    .map(_.booleanValue())

  override def rPush[T: RedisEncoder](key: String, index: Int, elements: T*): Task[Boolean] =
    rPush(key, index, elements.toList)

  override def rPush[T: RedisEncoder](key: String, index: Int, elements: List[T]): Task[Boolean] =
    ZIO
      .fromCompletionStage(list(key).addAllAsync(index, elements.map(RedisEncoder[T].encode).asJava))
      .map(_.booleanValue())

  override def rPush[T: RedisEncoder](key: String, elements: T*): Task[Boolean] =
    rPush(key, elements.toList)

  override def rPush[T: RedisEncoder](key: String, elements: List[T]): Task[Boolean] = ZIO
    .fromCompletionStage(list(key).addAllAsync(elements.map(RedisEncoder[T].encode).asJavaCollection))
    .map(_.booleanValue())

  override def rPushX[T: RedisEncoder](key: String, elements: T*): Task[Int] =
    rPushX(key, elements.toList)

  override def rPushX[T: RedisEncoder](key: String, elements: List[T]): Task[Int] = ZIO
    .fromCompletionStage(deque(key).addLastIfExistsAsync(elements.map(RedisEncoder[T].encode): _*))
    .map(_.toInt)

}

case class RedisListOperationsLive(redissonClient: RedissonClient) extends RedisListOperationsImpl

object RedisListOperations {

  val live: URLayer[RedissonClient, RedisListOperations] = ZLayer
    .fromZIO(ZIO.service[RedissonClient].map(RedisListOperationsLive))

}
