package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{RSet, RedissonClient}
import org.redisson.client.codec.StringCodec
import zio.{Task, URLayer, ZIO, ZLayer}
import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders

import scala.jdk.CollectionConverters.IterableHasAsJava

trait RedisSetOperations {

  def sAdd[T: RedisEncoder](key: String, member: T): Task[Boolean]

  def sAdd[T: RedisEncoder](key: String, members: Iterable[T]): Task[Boolean]

  def sCard(key: String): Task[Int]

  def sDiff[T: RedisDecoder](key: String, keys: Seq[String]): Task[Set[T]]

  def sDiffStore[T: RedisDecoder](destination: String, keys: Seq[String]): Task[Int]

  def sInter[T: RedisDecoder](key: String, keys: Seq[String]): Task[Set[T]]

  def sInterStore[T: RedisEncoder](destination: String, keys: Seq[String]): Task[Int]

  def sIsMember[T: RedisEncoder](key: String, member: T): Task[Boolean]

  def sMembers[T: RedisDecoder](key: String): Task[Set[T]]

  def sMove[T: RedisEncoder](source: String, destination: String, member: T): Task[Boolean]

  def sPop[T: RedisDecoder](key: String): Task[Option[T]]

  def sPop[T: RedisDecoder](key: String, count: Int): Task[Set[T]]

  def sRandMember[T: RedisDecoder](key: String): Task[Option[T]]

  def sRandMember[T: RedisDecoder](key: String, count: Int): Task[Set[T]]

  def sRem[T: RedisEncoder](key: String, member: T): Task[Boolean]

  def sRem[T: RedisEncoder](key: String, members: Iterable[T]): Task[Boolean]

  def sScan[T: RedisDecoder](key: String, count: Int): Task[Iterator[T]]

  def sScan[T: RedisDecoder](key: String, pattern: String): Task[Iterator[T]]

  def sScan[T: RedisDecoder](key: String, pattern: String, count: Int): Task[Iterator[T]]

  def sUnion[T: RedisDecoder](key: String, keys: Seq[String]): Task[Set[T]]

  def sUnionStore[T: RedisDecoder](destination: String, keys: Seq[String]): Task[Int]

}

trait RedisSetOperationsImpl extends RedisSetOperations {
  protected val redissonClient: RedissonClient

  private lazy val set: String => RSet[String] =
    redissonClient.getSet[String](_, StringCodec.INSTANCE)

  override def sAdd[T: RedisEncoder](key: String, member: T): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).addAsync(RedisEncoder[T].encode(member)))
    .map(_.booleanValue())

  override def sAdd[T: RedisEncoder](key: String, members: Iterable[T]): Task[Boolean] = ZIO
    .fromCompletionStage(
      set(key).addAllAsync(members.map(RedisEncoder[T].encode(_)).asJavaCollection)
    )
    .map(_.booleanValue())

  override def sCard(key: String): Task[Int] = ZIO
    .fromCompletionStage(set(key).sizeAsync())
    .map(_.intValue())

  override def sDiff[T: RedisDecoder](key: String, keys: Seq[String]): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).readDiffAsync(keys: _*))
    .flatMap(JavaDecoders.decodeSet(_))

  override def sDiffStore[T: RedisDecoder](destination: String, keys: Seq[String]): Task[Int] = ZIO
    .fromCompletionStage(set(destination).diffAsync(keys: _*))
    .map(_.intValue())

  override def sInter[T: RedisDecoder](key: String, keys: Seq[String]): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).readIntersectionAsync(keys: _*))
    .flatMap(JavaDecoders.decodeSet(_))

  override def sInterStore[T: RedisEncoder](destination: String, keys: Seq[String]): Task[Int] = ZIO
    .fromCompletionStage(set(destination).intersectionAsync(keys: _*))
    .map(_.intValue())

  override def sIsMember[T: RedisEncoder](key: String, member: T): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).containsAsync(RedisEncoder[T].encode(member)))
    .map(_.booleanValue())

  override def sMembers[T: RedisDecoder](key: String): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).readAllAsync())
    .flatMap(JavaDecoders.decodeSet(_))

  override def sMove[T: RedisEncoder](
    source: String,
    destination: String,
    member: T
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(source).moveAsync(destination, RedisEncoder[T].encode(member)))
    .map(_.booleanValue())

  override def sPop[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(set(key).removeRandomAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def sPop[T: RedisDecoder](key: String, count: Int): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).removeRandomAsync(count))
    .flatMap(JavaDecoders.decodeSet(_))

  override def sRandMember[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(set(key).randomAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def sRandMember[T: RedisDecoder](key: String, count: Int): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).randomAsync(count))
    .flatMap(JavaDecoders.decodeSet(_))

  override def sRem[T: RedisEncoder](key: String, member: T): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).removeAsync(RedisEncoder[T].encode(member)))
    .map(_.booleanValue())

  override def sRem[T: RedisEncoder](key: String, members: Iterable[T]): Task[Boolean] = ZIO
    .fromCompletionStage(
      set(key).removeAllAsync(members.map(RedisEncoder[T].encode(_)).asJavaCollection)
    )
    .map(_.booleanValue())

  override def sScan[T: RedisDecoder](key: String, count: Int): Task[Iterator[T]] = ZIO
    .attempt(set(key).iterator(count))
    .flatMap(JavaDecoders.decodeIterator(_))

  override def sScan[T: RedisDecoder](key: String, pattern: String): Task[Iterator[T]] = ZIO
    .attempt(set(key).iterator(pattern))
    .flatMap(JavaDecoders.decodeIterator(_))

  override def sScan[T: RedisDecoder](key: String, pattern: String, count: Int): Task[Iterator[T]] =
    ZIO.attempt(set(key).iterator(pattern, count)).flatMap(JavaDecoders.decodeIterator(_))

  override def sUnion[T: RedisDecoder](key: String, keys: Seq[String]): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).readUnionAsync(keys: _*))
    .flatMap(JavaDecoders.decodeSet(_))

  override def sUnionStore[T: RedisDecoder](destination: String, keys: Seq[String]): Task[Int] = ZIO
    .fromCompletionStage(set(destination).unionAsync(keys: _*))
    .map(_.intValue())

}

case class RedisSetOperationsLive(redissonClient: RedissonClient) extends RedisSetOperationsImpl

object RedisSetOperations {

  val live: URLayer[RedissonClient, RedisSetOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisSetOperationsLive))

}
