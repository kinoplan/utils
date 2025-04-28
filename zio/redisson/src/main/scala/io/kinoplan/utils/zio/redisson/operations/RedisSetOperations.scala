package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{RSet, RedissonClient}
import zio.{Task, URLayer, ZIO, ZLayer}
import io.kinoplan.utils.redisson.codec.{RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders
import zio.stream.ZStream

import scala.jdk.CollectionConverters.IterableHasAsJava

/** Interface representing operations that can be performed on Redis set keys.
  */
trait RedisSetOperations {

  /** Add a member to a set.
    *
    * Similar to the SADD command.
    *
    * @param key
    *   The key of the set.
    * @param member
    *   The member to add.
    * @tparam T
    *   Type of the member, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def sAdd[T: RedisEncoder](key: String, member: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Add multiple members to a set.
    *
    * Similar to the SADD command.
    *
    * @param key
    *   The key of the set.
    * @param members
    *   The members to add.
    * @tparam T
    *   Type of the members, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def sAdd[T: RedisEncoder](key: String, members: Iterable[T])(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Get the number of members in a set.
    *
    * Similar to the SCARD command.
    *
    * @param key
    *   The key of the set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of members in the set.
    */
  def sCard(key: String)(implicit
    codec: RCodec
  ): Task[Int]

  /** Subtract multiple sets from the first set and return the difference.
    *
    * Similar to the SDIFF command.
    *
    * @param key
    *   The key of the set to subtract from.
    * @param keys
    *   The keys of the sets to be subtracted.
    * @tparam T
    *   Type of the members, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The resulting set of the difference.
    */
  def sDiff[T: RedisDecoder](key: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Set[T]]

  /** Subtract multiple sets from the first set and store the result.
    *
    * Similar to the SDIFFSTORE command.
    *
    * @param destination
    *   The key where the result is stored.
    * @param keys
    *   The keys of the sets to be subtracted.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements in the resulting set.
    */
  def sDiffStore(destination: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Int]

  /** Intersect multiple sets and return the result.
    *
    * Similar to the SINTER command.
    *
    * @param key
    *   The key of the first set.
    * @param keys
    *   The keys of the other sets.
    * @tparam T
    *   Type of the members, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The resulting set of the intersection.
    */
  def sInter[T: RedisDecoder](key: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Set[T]]

  /** Intersect multiple sets and store the result.
    *
    * Similar to the SINTERSTORE command.
    *
    * @param destination
    *   The key where the result is stored.
    * @param keys
    *   The keys of the sets to intersect.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements in the resulting set.
    */
  def sInterStore(destination: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Int]

  /** Determine if a member is in a set.
    *
    * Similar to the SISMEMBER command.
    *
    * @param key
    *   The key of the set.
    * @param member
    *   The member to check.
    * @tparam T
    *   Type of the member, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def sIsMember[T: RedisEncoder](key: String, member: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Get all the members in a set.
    *
    * Similar to the SMEMBERS command.
    *
    * @param key
    *   The key of the set.
    * @tparam T
    *   Type of the members, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A set of all members in the set.
    */
  def sMembers[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Set[T]]

  /** Move a member from one set to another.
    *
    * Similar to the SMOVE command.
    *
    * @param source
    *   The key of the source set.
    * @param destination
    *   The key of the destination set.
    * @param member
    *   The member to move.
    * @tparam T
    *   Type of the member, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def sMove[T: RedisEncoder](source: String, destination: String, member: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Remove and return a random member from a set.
    *
    * Similar to the SPOP command.
    *
    * @param key
    *   The key of the set.
    * @tparam T
    *   Type of the member, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing the removed member or None if the set is empty.
    */
  def sPop[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Remove and return multiple random members from a set.
    *
    * Similar to the SPOP command.
    *
    * @param key
    *   The key of the set.
    * @param count
    *   The number of members to pop.
    * @tparam T
    *   Type of the members, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A set of removed members.
    */
  def sPop[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Set[T]]

  /** Get a random member from a set.
    *
    * Similar to the SRANDMEMBER command.
    *
    * @param key
    *   The key of the set.
    * @tparam T
    *   Type of the member, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   An Option containing a random member or None if the set is empty.
    */
  def sRandMember[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]]

  /** Get one or more random members from a set.
    *
    * Similar to the SRANDMEMBER command.
    *
    * @param key
    *   The key of the set.
    * @param count
    *   The number of members to return.
    * @tparam T
    *   Type of the members, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A set of random members.
    */
  def sRandMember[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Set[T]]

  /** Remove a member from a set.
    *
    * Similar to the SREM command.
    *
    * @param key
    *   The key of the set.
    * @param member
    *   The member to remove.
    * @tparam T
    *   Type of the member, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def sRem[T: RedisEncoder](key: String, member: T)(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Remove multiple members from a set.
    *
    * Similar to the SREM command.
    *
    * @param key
    *   The key of the set.
    * @param members
    *   The members to remove.
    * @tparam T
    *   Type of the members, requires RedisEncoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   Boolean indicating if the operation was successful.
    */
  def sRem[T: RedisEncoder](key: String, members: Iterable[T])(implicit
    codec: RCodec
  ): Task[Boolean]

  /** Incrementally iterate over set elements.
    *
    * Similar to the SSCAN command.
    *
    * @param key
    *   The key of the set.
    * @param count
    *   The number of elements returned at each iteration.
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of set elements.
    */
  def sScan[T: RedisDecoder](key: String, count: Int = 10)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  /** Incrementally iterate over set elements matching a pattern.
    *
    * Similar to the SSCAN command.
    *
    * @param key
    *   The key of the set.
    * @param pattern
    *   The pattern that elements should match.
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of set elements.
    */
  def sScan[T: RedisDecoder](key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  /** Incrementally iterate over set elements matching a pattern and limit the count.
    *
    * Similar to the SSCAN command.
    *
    * @param key
    *   The key of the set.
    * @param pattern
    *   The pattern that elements should match.
    * @param count
    *   The number of elements returned at each iteration.
    * @tparam T
    *   Type of the elements, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   A stream of set elements.
    */
  def sScan[T: RedisDecoder](key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T]

  /** Add multiple sets and return the union.
    *
    * Similar to the SUNION command.
    *
    * @param key
    *   The key of the first set.
    * @param keys
    *   The keys of the other sets.
    * @tparam T
    *   Type of the members, requires RedisDecoder[T].
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The resulting set of the union.
    */
  def sUnion[T: RedisDecoder](key: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Set[T]]

  /** Add multiple sets and store the union.
    *
    * Similar to the SUNIONSTORE command.
    *
    * @param destination
    *   The key where the result is stored.
    * @param keys
    *   The keys of the sets to union.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements in the resulting set.
    */
  def sUnionStore(destination: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Int]

}

trait RedisSetOperationsImpl extends RedisSetOperations {
  protected val redissonClient: RedissonClient

  private def set(key: String)(implicit
    codec: RCodec
  ): RSet[String] = codec
    .underlying
    .map(redissonClient.getSet[String](key, _))
    .getOrElse(redissonClient.getSet[String](key))

  override def sAdd[T: RedisEncoder](key: String, member: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).addAsync(RedisEncoder[T].encode(member)))
    .map(Boolean.unbox)

  override def sAdd[T: RedisEncoder](key: String, members: Iterable[T])(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(
      set(key).addAllAsync(members.map(RedisEncoder[T].encode(_)).asJavaCollection)
    )
    .map(Boolean.unbox)

  override def sCard(key: String)(implicit
    codec: RCodec
  ): Task[Int] = ZIO.fromCompletionStage(set(key).sizeAsync()).map(_.toInt)

  override def sDiff[T: RedisDecoder](key: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).readDiffAsync(keys: _*))
    .flatMap(JavaDecoders.fromSet(_))

  override def sDiffStore(destination: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Int] = ZIO.fromCompletionStage(set(destination).diffAsync(keys: _*)).map(_.toInt)

  override def sInter[T: RedisDecoder](key: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).readIntersectionAsync(keys: _*))
    .flatMap(JavaDecoders.fromSet(_))

  override def sInterStore(destination: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Int] = ZIO.fromCompletionStage(set(destination).intersectionAsync(keys: _*)).map(_.toInt)

  override def sIsMember[T: RedisEncoder](key: String, member: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).containsAsync(RedisEncoder[T].encode(member)))
    .map(Boolean.unbox)

  override def sMembers[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Set[T]] = ZIO.fromCompletionStage(set(key).readAllAsync()).flatMap(JavaDecoders.fromSet(_))

  override def sMove[T: RedisEncoder](source: String, destination: String, member: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(source).moveAsync(destination, RedisEncoder[T].encode(member)))
    .map(Boolean.unbox)

  override def sPop[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(set(key).removeRandomAsync())
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def sPop[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).removeRandomAsync(count))
    .flatMap(JavaDecoders.fromSet(_))

  override def sRandMember[T: RedisDecoder](key: String)(implicit
    codec: RCodec
  ): Task[Option[T]] = ZIO
    .fromCompletionStage(set(key).randomAsync())
    .flatMap(JavaDecoders.fromNullableValue(_))

  override def sRandMember[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).randomAsync(count))
    .flatMap(JavaDecoders.fromSet(_))

  override def sRem[T: RedisEncoder](key: String, member: T)(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).removeAsync(RedisEncoder[T].encode(member)))
    .map(Boolean.unbox)

  override def sRem[T: RedisEncoder](key: String, members: Iterable[T])(implicit
    codec: RCodec
  ): Task[Boolean] = ZIO
    .fromCompletionStage(
      set(key).removeAllAsync(members.map(RedisEncoder[T].encode(_)).asJavaCollection)
    )
    .map(Boolean.unbox)

  override def sScan[T: RedisDecoder](key: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(set(key).stream(count))
    .mapZIO(JavaDecoders.fromValue(_))

  override def sScan[T: RedisDecoder](key: String, pattern: String)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(set(key).stream(pattern))
    .mapZIO(JavaDecoders.fromValue(_))

  override def sScan[T: RedisDecoder](key: String, pattern: String, count: Int)(implicit
    codec: RCodec
  ): ZStream[Any, Throwable, T] = ZStream
    .fromJavaStream(set(key).stream(pattern, count))
    .mapZIO(JavaDecoders.fromValue(_))

  override def sUnion[T: RedisDecoder](key: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Set[T]] = ZIO
    .fromCompletionStage(set(key).readUnionAsync(keys: _*))
    .flatMap(JavaDecoders.fromSet(_))

  override def sUnionStore(destination: String, keys: Seq[String])(implicit
    codec: RCodec
  ): Task[Int] = ZIO.fromCompletionStage(set(destination).unionAsync(keys: _*)).map(_.toInt)

}

case class RedisSetOperationsLive(redissonClient: RedissonClient) extends RedisSetOperationsImpl

object RedisSetOperations {

  val live: URLayer[RedissonClient, RedisSetOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisSetOperationsLive))

}
