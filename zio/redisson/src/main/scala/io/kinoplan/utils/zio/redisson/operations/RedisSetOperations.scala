package io.kinoplan.utils.zio.redisson.operations

import org.redisson.api.{RSet, RedissonClient}
import zio.{Task, URLayer, ZIO, ZLayer}
import zio.stream.ZStream

import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.codec.base.{BaseRedisDecoder, BaseRedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.operations.base.ResultBuilder._
import io.kinoplan.utils.zio.redisson.utils.JavaDecoders

/** Interface representing operations that can be performed on Redis set data.
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
  def sAdd[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Add multiple members to a set.
    *
    * Similar to the SADD command.
    *
    * @param key
    *   The key of the set.
    * @param members
    *   The members to add.
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
  def sAdd[T, V](key: String, members: Iterable[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
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
    codec: RCodec[_, _]
  ): Task[Int]

  /** Subtract multiple sets from the first set and return the difference.
    *
    * Similar to the SDIFF command.
    *
    * @param key
    *   The key of the set to subtract from.
    * @param keys
    *   The keys of the sets to be subtracted.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The resulting set of the difference.
    */
  def sDiff[V](key: String, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V]

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
    codec: RCodec[_, _]
  ): Task[Int]

  /** Intersect multiple sets and return the result.
    *
    * Similar to the SINTER command.
    *
    * @param key
    *   The key of the first set.
    * @param keys
    *   The keys of the other sets.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The resulting set of the intersection.
    */
  def sInter[V](key: String, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V]

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
    codec: RCodec[_, _]
  ): Task[Int]

  /** Determine if a member is in a set.
    *
    * Similar to the SISMEMBER command.
    *
    * @param key
    *   The key of the set.
    * @param member
    *   The member to check.
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
  def sIsMember[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Get all the members in a set.
    *
    * Similar to the SMEMBERS command.
    *
    * @param key
    *   The key of the set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A set of all members in the set.
    */
  def sMembers[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V]

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
  def sMove[T, V](source: String, destination: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Remove and return a random member from a set.
    *
    * Similar to the SPOP command.
    *
    * @param key
    *   The key of the set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing the removed member or None if the set is empty.
    */
  def sPop[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Remove and return multiple random members from a set.
    *
    * Similar to the SPOP command.
    *
    * @param key
    *   The key of the set.
    * @param count
    *   The number of members to pop.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A set of removed members.
    */
  def sPop[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V]

  /** Get a random member from a set.
    *
    * Similar to the SRANDMEMBER command.
    *
    * @param key
    *   The key of the set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An Option containing a random member or None if the set is empty.
    */
  def sRandMember[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Get one or more random members from a set.
    *
    * Similar to the SRANDMEMBER command.
    *
    * @param key
    *   The key of the set.
    * @param count
    *   The number of members to return.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A set of random members.
    */
  def sRandMember[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V]

  /** Remove a member from a set.
    *
    * Similar to the SREM command.
    *
    * @param key
    *   The key of the set.
    * @param member
    *   The member to remove.
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
  def sRem[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Remove multiple members from a set.
    *
    * Similar to the SREM command.
    *
    * @param key
    *   The key of the set.
    * @param members
    *   The members to remove.
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
  def sRem[T, V](key: String, members: Iterable[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Incrementally iterate over set elements.
    *
    * Similar to the SSCAN command.
    *
    * @param key
    *   The key of the set.
    * @param count
    *   The number of elements returned at each iteration.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of set elements.
    */
  def sScan[V](key: String, count: Int = 10)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

  /** Incrementally iterate over set elements matching a pattern.
    *
    * Similar to the SSCAN command.
    *
    * @param key
    *   The key of the set.
    * @param pattern
    *   The pattern that elements should match.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of set elements.
    */
  def sScan[V](key: String, pattern: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

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
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of set elements.
    */
  def sScan[V](key: String, pattern: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

  /** Add multiple sets and return the union.
    *
    * Similar to the SUNION command.
    *
    * @param key
    *   The key of the first set.
    * @param keys
    *   The keys of the other sets.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The resulting set of the union.
    */
  def sUnion[V](key: String, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V]

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
    codec: RCodec[_, _]
  ): Task[Int]

}

trait RedisSetOperationsImpl extends RedisSetOperations {
  protected val redissonClient: RedissonClient

  private def set[V](key: String)(implicit
    codec: RCodec[_, V]
  ): RSet[V] = codec
    .underlying
    .map(redissonClient.getSet[V](key, _))
    .getOrElse(redissonClient.getSet[V](key))

  override def sAdd[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).addAsync(codec.encode(member)))
    .map(Boolean.unbox)

  override def sAdd[T, V](key: String, members: Iterable[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).addAllAsync(members.map(codec.encode(_)).asJavaCollection))
    .map(Boolean.unbox)

  override def sCard(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO.fromCompletionStage(set(key).sizeAsync()).map(_.toInt)

  override def sDiff[V](key: String, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V] = new ResultBuilder14[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Set[T]] = ZIO
      .fromCompletionStage(set(key).readDiffAsync(keys: _*))
      .flatMap(JavaDecoders.fromSet(_))
  }

  override def sDiffStore(destination: String, keys: Seq[String])(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO.fromCompletionStage(set(destination).diffAsync(keys: _*)).map(_.toInt)

  override def sInter[V](key: String, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V] = new ResultBuilder14[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Set[T]] = ZIO
      .fromCompletionStage(set(key).readIntersectionAsync(keys: _*))
      .flatMap(JavaDecoders.fromSet(_))
  }

  override def sInterStore(destination: String, keys: Seq[String])(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO.fromCompletionStage(set(destination).intersectionAsync(keys: _*)).map(_.toInt)

  override def sIsMember[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).containsAsync(codec.encode(member)))
    .map(Boolean.unbox)

  override def sMembers[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V] = new ResultBuilder14[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Set[T]] = ZIO
      .fromCompletionStage(set(key).readAllAsync())
      .flatMap(JavaDecoders.fromSet(_))
  }

  override def sMove[T, V](source: String, destination: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(source).moveAsync(destination, codec.encode(member)))
    .map(Boolean.unbox)

  override def sPop[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(set(key).removeRandomAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def sPop[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V] = new ResultBuilder14[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Set[T]] = ZIO
      .fromCompletionStage(set(key).removeRandomAsync(count))
      .flatMap(JavaDecoders.fromSet(_))
  }

  override def sRandMember[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(set(key).randomAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def sRandMember[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V] = new ResultBuilder14[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Set[T]] = ZIO
      .fromCompletionStage(set(key).randomAsync(count))
      .flatMap(JavaDecoders.fromSet(_))
  }

  override def sRem[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).removeAsync(codec.encode(member)))
    .map(Boolean.unbox)

  override def sRem[T, V](key: String, members: Iterable[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(set(key).removeAllAsync(members.map(codec.encode(_)).asJavaCollection))
    .map(Boolean.unbox)

  override def sScan[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(set(key).stream(count))
      .mapZIO(JavaDecoders.fromValue(_))
  }

  override def sScan[V](key: String, pattern: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(set(key).stream(pattern))
      .mapZIO(JavaDecoders.fromValue(_))
  }

  override def sScan[V](key: String, pattern: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(set(key).stream(pattern, count))
      .mapZIO(JavaDecoders.fromValue(_))
  }

  override def sUnion[V](key: String, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder14[V] = new ResultBuilder14[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Set[T]] = ZIO
      .fromCompletionStage(set(key).readUnionAsync(keys: _*))
      .flatMap(JavaDecoders.fromSet(_))
  }

  override def sUnionStore(destination: String, keys: Seq[String])(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO.fromCompletionStage(set(destination).unionAsync(keys: _*)).map(_.toInt)

}

case class RedisSetOperationsLive(redissonClient: RedissonClient) extends RedisSetOperationsImpl

object RedisSetOperations {

  val live: URLayer[RedissonClient, RedisSetOperations] =
    ZLayer.fromFunction(RedisSetOperationsLive.apply _)

}
