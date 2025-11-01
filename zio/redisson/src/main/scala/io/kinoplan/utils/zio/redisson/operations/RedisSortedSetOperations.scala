package io.kinoplan.utils.zio.redisson.operations

import java.util.concurrent.TimeUnit

import org.redisson.api._
import zio.{Duration, Task, URLayer, ZIO, ZLayer}
import zio.stream.ZStream

import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.codec.DefaultRedisEncoders
import io.kinoplan.utils.redisson.codec.base.{BaseRedisDecoder, BaseRedisEncoder}
import io.kinoplan.utils.zio.redisson.codec.RCodec
import io.kinoplan.utils.zio.redisson.operations.base.ResultBuilder._
import io.kinoplan.utils.zio.redisson.utils.{JavaDecoders, JavaEncoders}

/** Interface representing operations that can be performed on Redis sorted set data.
  */
trait RedisSortedSetOperations {

  /** Blocking version of zmPopMax that removes and returns the max score members.
    *
    * Similar to the BZPOPMAX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param timeout
    *   The timeout duration for blocking.
    * @param count
    *   The number of elements to pop.
    * @param keys
    *   Additional keys for the operation.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map with popped members and their scores grouped by key.
    */
  def bzmPopMax[V](key: String, timeout: Duration, count: Int = 1, keys: Seq[String] = Seq.empty)(
    implicit
    codec: RCodec[_, V]
  ): ResultBuilder15[V]

  /** Blocking version of zmPopMin that removes and returns the min score members.
    *
    * Similar to the BZPOPMIN command.
    *
    * @param key
    *   The key of the sorted set.
    * @param timeout
    *   The timeout duration for blocking.
    * @param count
    *   The number of elements to pop.
    * @param keys
    *   Additional keys for the operation.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map with popped members and their scores grouped by key.
    */
  def bzmPopMin[V](key: String, timeout: Duration, count: Int = 1, keys: Seq[String] = Seq.empty)(
    implicit
    codec: RCodec[_, V]
  ): ResultBuilder15[V]

  /** Pop the max score element in a sorted set with blocking.
    *
    * Similar to the BZPOPMAX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param timeout
    *   The timeout duration for blocking.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An optional element with the maximum score.
    */
  def bzPopMax[V](key: String, timeout: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Pop the min score element in a sorted set with blocking.
    *
    * Similar to the BZPOPMIN command.
    *
    * @param key
    *   The key of the sorted set.
    * @param timeout
    *   The timeout duration for blocking.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An optional element with the minimum score.
    */
  def bzPopMin[V](key: String, timeout: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Add a member with a score to a sorted set.
    *
    * Similar to the ZADD command.
    *
    * @param key
    *   The key of the sorted set.
    * @param score
    *   The score associated with the member.
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
  def zAdd[T, V](key: String, score: Double, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Add multiple members with associated scores to a sorted set.
    *
    * Similar to the ZADD command.
    *
    * @param key
    *   The key of the sorted set.
    * @param scoreMembers
    *   A map of members and their associated scores.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The number of elements added to the sorted set.
    */
  def zAdd[T, V](key: String, scoreMembers: Map[T, Double])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int]

  /** Get the number of members in a sorted set.
    *
    * Similar to the ZCARD command.
    *
    * @param key
    *   The key of the sorted set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The cardinality (number of elements) in the sorted set.
    */
  def zCard(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[Int]

  /** Count the members in a sorted set with scores within the given range.
    *
    * Similar to the ZCOUNT command.
    *
    * @param key
    *   The key of the sorted set.
    * @param startScore
    *   The minimum score.
    * @param startInclusive
    *   Whether the minimum score is inclusive.
    * @param endScore
    *   The maximum score.
    * @param endInclusive
    *   Whether the maximum score is inclusive.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The count of elements within the score range.
    */
  def zCount(
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean
  )(implicit
    codec: RCodec[_, _]
  ): Task[Int]

  /** Subtract multiple sorted sets and return the difference.
    *
    * Similar to the ZDIFF command.
    *
    * @param key
    *   The key of the initial sorted set.
    * @param names
    *   The keys of other sets to subtract.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The resulting iterable of the difference.
    */
  def zDiff[V](key: String, names: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Subtract multiple sorted sets and store the difference.
    *
    * Similar to the ZDIFFSTORE command.
    *
    * @param key
    *   The destination key where the result is stored.
    * @param names
    *   The keys of other sets to subtract.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements in the resulting set.
    */
  def zDiffStore(key: String, names: Seq[String])(implicit
    codec: RCodec[_, _]
  ): Task[Int]

  /** Increment the score of a member in a sorted set by the specified amount.
    *
    * Similar to the ZINCRBY command.
    *
    * @param key
    *   The key of the sorted set.
    * @param increment
    *   The amount to increment the score by.
    * @param member
    *   The member whose score will be incremented.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The new score of the member.
    */
  def zIncrBy[T, V](key: String, increment: Number, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Double]

  /** Intersect multiple sorted sets and return the result.
    *
    * Similar to the ZINTER command.
    *
    * @param key
    *   The key of the initial sorted set.
    * @param args
    *   Options for intersection, like additional keys, weights and aggregate.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The resulting iterable of the intersection.
    */
  def zInter[V](key: String, args: SetIntersectionArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Intersect multiple sorted sets and return the result.
    *
    * Similar to the ZINTER command.
    *
    * @param key
    *   The key of the initial sorted set.
    * @param args
    *   Options for intersection, like additional keys, weights and aggregate. Note: `SetReadArgs`
    *   is expected to extend or be derived from `SetIntersectionArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The resulting iterable of the intersection.
    */
  def zInter[V](key: String, args: SetReadArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Intersect multiple sorted sets and store the result.
    *
    * Similar to the ZINTERSTORE command.
    *
    * @param key
    *   The destination key where the result is stored.
    * @param args
    *   Options for intersection, like additional keys, weights and aggregate.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements in the resulting set.
    */
  def zInterStore(key: String, args: SetIntersectionArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Int]

  /** Intersect multiple sorted sets and store the result.
    *
    * Similar to the ZINTERSTORE command.
    *
    * @param key
    *   The destination key where the result is stored.
    * @param args
    *   Options for intersection, like additional keys, weights and aggregate. Note: `SetReadArgs`
    *   is expected to extend or be derived from `SetIntersectionArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements in the resulting set.
    */
  def zInterStore(key: String, args: SetReadArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Int]

  /** Count the number of elements in a sorted set with a Lexicographical range.
    *
    * Similar to the ZLEXCOUNT command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromElement
    *   The minimum element in the range.
    * @param fromInclusive
    *   Whether the minimum element is inclusive.
    * @param toElement
    *   The maximum element in the range.
    * @param toInclusive
    *   Whether the maximum element is inclusive.
    * @return
    *   The number of elements in the specified range.
    */
  def zLexCount(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): Task[Int]

  /** Count the number of elements in a sorted set where elements are lexicographically <=
    * toElement.
    *
    * Similar to the ZLEXCOUNT command.
    *
    * @param key
    *   The key of the sorted set.
    * @param toElement
    *   The maximum element in the range.
    * @param toInclusive
    *   Whether the maximum element is inclusive.
    * @return
    *   The count of elements in the specified range.
    */
  def zLexCountMin(key: String, toElement: String, toInclusive: Boolean): Task[Int]

  /** Count the number of elements in a sorted set where elements are lexicographically >=
    * fromElement.
    *
    * Similar to the ZLEXCOUNT command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromElement
    *   The minimum element in the range.
    * @param fromInclusive
    *   Whether the minimum element is inclusive.
    * @return
    *   The count of elements in the specified range.
    */
  def zLexCountMax(key: String, fromElement: String, fromInclusive: Boolean): Task[Int]

  /** Remove and return the max score members of sorted sets.
    *
    * Similar to the ZMPOP command with MAX option.
    *
    * @param key
    *   The key of the sorted set.
    * @param count
    *   The number of elements to pop.
    * @param names
    *   Additional keys if popping across multiple sorted sets.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map with popped members and their scores grouped by key.
    */
  def zmPopMax[V](key: String, count: Int = 1, names: Seq[String] = Seq.empty)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder15[V]

  /** Remove and return the min score members of sorted sets.
    *
    * Similar to the ZMPOP command with MIN option.
    *
    * @param key
    *   The key of the sorted set.
    * @param count
    *   The number of elements to pop.
    * @param names
    *   Additional keys if popping across multiple sorted sets.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map with popped members and their scores grouped by key.
    */
  def zmPopMin[V](key: String, count: Int = 1, names: Seq[String] = Seq.empty)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder15[V]

  /** Remove and return the member with the highest score in a sorted set.
    *
    * Similar to the ZPOPMAX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An optional element with the highest score.
    */
  def zPopMax[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Remove and return multiple members with the highest scores in a sorted set.
    *
    * Similar to the ZPOPMAX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param count
    *   The number of elements to pop.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of the elements with the highest scores.
    */
  def zPopMax[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Remove and return the member with the lowest score in a sorted set.
    *
    * Similar to the ZPOPMIN command.
    *
    * @param key
    *   The key of the sorted set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An optional element with the lowest score.
    */
  def zPopMin[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Remove and return multiple members with the lowest scores in a sorted set.
    *
    * Similar to the ZPOPMIN command.
    *
    * @param key
    *   The key of the sorted set.
    * @param count
    *   The number of elements to pop.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of the elements with the lowest scores.
    */
  def zPopMin[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Get a random member from a sorted set.
    *
    * Similar to the ZRANDMEMBER command.
    *
    * @param key
    *   The key of the sorted set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An optional member randomly selected from the sorted set.
    */
  def zRandMember[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V]

  /** Get one or more random members from a sorted set.
    *
    * Similar to the ZRANDMEMBER command.
    *
    * @param key
    *   The key of the sorted set.
    * @param count
    *   The number of members to return.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A collection of randomly selected members.
    */
  def zRandMember[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Return a range of members in a sorted set, by index.
    *
    * Similar to the ZRANGE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The starting index.
    * @param toScore
    *   The ending index.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A collection of members ordered by score.
    */
  def zRange[V](key: String, fromScore: Int = 0, toScore: Int = -1)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Return a range of members in a sorted set, by score.
    *
    * Similar to the ZRANGE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The minimum score.
    * @param fromInclusive
    *   Whether the minimum score is inclusive.
    * @param toScore
    *   The maximum score.
    * @param toInclusive
    *   Whether the maximum score is inclusive.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of members within the score range.
    */
  def zRange[V](
    key: String,
    fromScore: Int,
    fromInclusive: Boolean,
    toScore: Int,
    toInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Return a range of members in a sorted set, by score, with optional limit.
    *
    * Similar to the ZRANGE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The minimum score.
    * @param fromInclusive
    *   Whether the minimum score is inclusive.
    * @param toScore
    *   The maximum score.
    * @param toInclusive
    *   Whether the maximum score is inclusive.
    * @param offset
    *   The starting point for the range.
    * @param count
    *   The number of elements to return.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of members within the score range.
    */
  def zRange[V](
    key: String,
    fromScore: Int,
    fromInclusive: Boolean,
    toScore: Int,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Returns a range of members with their scores from a sorted set, by index.
    *
    * Similar to the ZRANGE command with the WITHSCORES option.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The starting index of the range.
    * @param toScore
    *   The ending index of the range.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map where each key is a member and its associated value is the score, representing the
    *   members in the specified index range alongside their scores.
    */
  def zRangeWithScores[V](key: String, fromScore: Int = 0, toScore: Int = -1)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V]

  /** Returns a range of members with their corresponding scores from a sorted set, based on score
    * criteria.
    *
    * Similar to the ZRANGE command with the WITHSCORES option.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The minimum score for the range.
    * @param fromInclusive
    *   Indicates whether the range includes elements with the exact `fromScore`.
    * @param toScore
    *   The maximum score for the range.
    * @param toInclusive
    *   Indicates whether the range includes elements with the exact `toScore`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map where each key is a member and its associated value is the score, representing the
    *   members within the specified score range.
    */
  def zRangeWithScores[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V]

  /** Returns a specified range of members with their scores from a sorted set, based on score
    * criteria, with support for pagination.
    *
    * Similar to the ZRANGE command with the WITHSCORES option.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The minimum score for the range.
    * @param fromInclusive
    *   Whether the range includes elements with the exact `fromScore`.
    * @param toScore
    *   The maximum score for the range.
    * @param toInclusive
    *   Whether the range includes elements with the exact `toScore`.
    * @param offset
    *   The starting point from where to return the elements, used for pagination.
    * @param count
    *   The maximum number of elements to return, used for pagination.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map where each key is a member and its associated value is the score, representing the
    *   members within the specified score range and pagination limits.
    */
  def zRangeWithScores[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V]

  /** Return members in a sorted set, by lexicographical order starting from an element.
    *
    * Similar to the ZRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param toElement
    *   The maximum element.
    * @param toInclusive
    *   Whether the maximum element is inclusive.
    * @return
    *   An iterable of members in lexicographical order.
    */
  def zRangeByLexMin(key: String, toElement: String, toInclusive: Boolean): ResultBuilder8[String]

  /** Return members in a sorted set, by lexicographical order with limit.
    *
    * Similar to the ZRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param toElement
    *   The maximum element.
    * @param toInclusive
    *   Whether the maximum element is inclusive.
    * @param offset
    *   The starting point for the range.
    * @param count
    *   The number of elements to return.
    * @return
    *   An iterable of members in lexicographical order.
    */
  def zRangeByLexMin(
    key: String,
    toElement: String,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  ): ResultBuilder8[String]

  /** Return members in a sorted set, by lexicographical order up to an element.
    *
    * Similar to the ZRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromElement
    *   The minimum element.
    * @param fromInclusive
    *   Whether the minimum element is inclusive.
    * @return
    *   An iterable of members in lexicographical order.
    */
  def zRangeByLexMax(
    key: String,
    fromElement: String,
    fromInclusive: Boolean
  ): ResultBuilder8[String]

  /** Return members in a sorted set, by lexicographical order with limit from an element.
    *
    * Similar to the ZRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromElement
    *   The minimum element.
    * @param fromInclusive
    *   Whether the minimum element is inclusive.
    * @param offset
    *   The starting point for the range.
    * @param count
    *   The number of elements to return.
    * @return
    *   An iterable of members in lexicographical order.
    */
  def zRangeByLexMax(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    offset: Int,
    count: Int
  ): ResultBuilder8[String]

  /** Return members in a sorted set, by lexicographical order.
    *
    * Similar to the ZRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param startIndex
    *   The starting index for the range.
    * @param endIndex
    *   The ending index for the range.
    * @return
    *   An iterable of members in lexicographical order.
    */
  def zRangeByLex(key: String, startIndex: Int = 0, endIndex: Int = -1): ResultBuilder8[String]

  /** Return members in a sorted set, by lexicographical range.
    *
    * Similar to the ZRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromElement
    *   The minimum element in the range.
    * @param fromInclusive
    *   Whether the minimum element is inclusive.
    * @param toElement
    *   The maximum element in the range.
    * @param toInclusive
    *   Whether the maximum element is inclusive.
    * @return
    *   An iterable of members within the lexicographical range.
    */
  def zRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): ResultBuilder8[String]

  /** Return members in a sorted set, by lexicographical range with offset and count.
    *
    * Similar to the ZRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromElement
    *   The minimum element in the range.
    * @param fromInclusive
    *   Whether the minimum element is inclusive.
    * @param toElement
    *   The maximum element in the range.
    * @param toInclusive
    *   Whether the maximum element is inclusive.
    * @param offset
    *   The starting point for the range.
    * @param count
    *   The number of elements to return.
    * @return
    *   An iterable of members within the lexicographical range.
    */
  def zRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  ): ResultBuilder8[String]

  /** Return members in a sorted set by score range.
    *
    * Similar to the ZRANGEBYSCORE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param startScore
    *   The minimum score.
    * @param startInclusive
    *   Whether the minimum score is inclusive.
    * @param endScore
    *   The maximum score.
    * @param endInclusive
    *   Whether the maximum score is inclusive.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of members within the score range.
    */
  def zRangeByScore[V](
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Return members in a sorted set by score range with offset and count.
    *
    * Similar to the ZRANGEBYSCORE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param startScore
    *   The minimum score.
    * @param startInclusive
    *   Whether the minimum score is inclusive.
    * @param endScore
    *   The maximum score.
    * @param endInclusive
    *   Whether the maximum score is inclusive.
    * @param offset
    *   The starting point for the range.
    * @param count
    *   The number of elements to return.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of members within the score range.
    */
  def zRangeByScore[V](
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Determine the index of a member in a sorted set.
    *
    * Similar to the ZRANK command.
    *
    * @param key
    *   The key of the sorted set.
    * @param member
    *   The member whose rank is to be found.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The rank of the member.
    */
  def zRank[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int]

  /** Remove one or more members from a sorted set.
    *
    * Similar to the ZREM command.
    *
    * @param key
    *   The key of the sorted set.
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
  def zRem[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Remove multiple members from a sorted set.
    *
    * Similar to the ZREM command.
    *
    * @param key
    *   The key of the sorted set.
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
  def zRem[T, V](key: String, members: Iterable[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean]

  /** Remove all elements in a sorted set between the given lexicographical range.
    *
    * Similar to the ZREMRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromElement
    *   The minimum element in the range.
    * @param fromInclusive
    *   Whether the minimum element is inclusive.
    * @param toElement
    *   The maximum element in the range.
    * @param toInclusive
    *   Whether the maximum element is inclusive.
    * @return
    *   The number of elements removed.
    */
  def zRemRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): Task[Int]

  /** Remove all elements in a sorted set from the given lexicographical element.
    *
    * Similar to the ZREMRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param toElement
    *   The maximum element in the range.
    * @param toInclusive
    *   Whether the maximum element is inclusive.
    * @return
    *   The number of elements removed.
    */
  def zRemRangeByLexMin(key: String, toElement: String, toInclusive: Boolean): Task[Int]

  /** Remove all elements in a sorted set from the given lexicographical element.
    *
    * Similar to the ZREMRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromElement
    *   The minimum element in the range.
    * @param fromInclusive
    *   Whether the minimum element is inclusive.
    * @return
    *   The number of elements removed.
    */
  def zRemRangeByLexMax(key: String, fromElement: String, fromInclusive: Boolean): Task[Int]

  /** Remove all elements in a sorted set between the given ranks.
    *
    * Similar to the ZREMRANGEBYRANK command.
    *
    * @param key
    *   The key of the sorted set.
    * @param startIndex
    *   The starting rank.
    * @param endIndex
    *   The ending rank.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements removed.
    */
  def zRemRangeByRank(key: String, startIndex: Int, endIndex: Int)(implicit
    codec: RCodec[_, _]
  ): Task[Int]

  /** Remove all elements in a sorted set within the given score range.
    *
    * Similar to the ZREMRANGEBYSCORE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param startScore
    *   The minimum score.
    * @param startInclusive
    *   Whether the minimum score is inclusive.
    * @param endScore
    *   The maximum score.
    * @param endInclusive
    *   Whether the maximum score is inclusive.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements removed.
    */
  def zRemRangeByScore(
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean
  )(implicit
    codec: RCodec[_, _]
  ): Task[Int]

  /** Return a range of members in a sorted set, in reverse order, by index.
    *
    * Similar to the ZREVRANGE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The starting index.
    * @param toScore
    *   The ending index.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of members ordered by score from high to low.
    */
  def zRevRange[V](key: String, fromScore: Int = 0, toScore: Int = -1)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Return a range of members in a sorted set, in reverse order, by score.
    *
    * Similar to the ZREVRANGE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The starting score.
    * @param fromInclusive
    *   Whether the starting score is inclusive.
    * @param toScore
    *   The ending score.
    * @param toInclusive
    *   Whether the ending score is inclusive.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of members within the reverse score range.
    */
  def zRevRange[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Return a range of members in a sorted set, in reverse order, by score with offset and count.
    *
    * Similar to the ZREVRANGE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The starting score.
    * @param fromInclusive
    *   Whether the starting score is inclusive.
    * @param toScore
    *   The ending score.
    * @param toInclusive
    *   Whether the ending score is inclusive.
    * @param offset
    *   The starting point for the range.
    * @param count
    *   The number of elements to return.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of members within the reverse score range.
    */
  def zRevRange[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Returns a range of members with their scores from a sorted set, in reverse order, by index.
    *
    * Similar to the ZREVRANGE command with the WITHSCORES option.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The starting index of the range.
    * @param toScore
    *   The ending index of the range.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map where each key is a member and its associated value is the score, representing the
    *   members in the specified index range alongside their scores.
    */
  def zRevRangeWithScores[V](key: String, fromScore: Int = 0, toScore: Int = -1)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V]

  /** Returns a range of members with their corresponding scores from a sorted set, in reverse
    * order, based on score criteria.
    *
    * Similar to the ZREVRANGE command with the WITHSCORES option.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The minimum score for the range.
    * @param fromInclusive
    *   Indicates whether the range includes elements with the exact `fromScore`.
    * @param toScore
    *   The maximum score for the range.
    * @param toInclusive
    *   Indicates whether the range includes elements with the exact `toScore`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map where each key is a member and its associated value is the score, representing the
    *   members within the specified score range.
    */
  def zRevRangeWithScores[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V]

  /** Returns a specified range of members with their scores from a sorted set, in reverse order,
    * based on score criteria, with support for pagination.
    *
    * Similar to the ZREVRANGE command with the WITHSCORES option.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromScore
    *   The minimum score for the range.
    * @param fromInclusive
    *   Whether the range includes elements with the exact `fromScore`.
    * @param toScore
    *   The maximum score for the range.
    * @param toInclusive
    *   Whether the range includes elements with the exact `toScore`.
    * @param offset
    *   The starting point from where to return the elements, used for pagination.
    * @param count
    *   The maximum number of elements to return, used for pagination.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A map where each key is a member and its associated value is the score, representing the
    *   members within the specified score range and pagination limits.
    */
  def zRevRangeWithScores[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V]

  /** Return members in a sorted set, ordered by score from high to low, by lexicographical range.
    *
    * Similar to the ZREVRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromElement
    *   The minimum element in lexicographical order.
    * @param fromInclusive
    *   Whether the minimum element is inclusive.
    * @param toElement
    *   The maximum element in lexicographical order.
    * @param toInclusive
    *   Whether the maximum element is inclusive.
    * @return
    *   An iterable of members within the specified lexicographical range, ordered from high to low.
    */
  def zRevRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): ResultBuilder8[String]

  /** Return members in a sorted set, ordered by score from high to low, by lexicographical range
    * with offset and count.
    *
    * Similar to the ZREVRANGEBYLEX command.
    *
    * @param key
    *   The key of the sorted set.
    * @param fromElement
    *   The minimum element in lexicographical order.
    * @param fromInclusive
    *   Whether the minimum element is inclusive.
    * @param toElement
    *   The maximum element in lexicographical order.
    * @param toInclusive
    *   Whether the maximum element is inclusive.
    * @param offset
    *   The starting point for the range.
    * @param count
    *   The number of elements to return.
    * @return
    *   An iterable of members within the specified lexicographical range, ordered from high to low.
    */
  def zRevRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  ): ResultBuilder8[String]

  /** Return members in a sorted set, by score, ordered from high to low.
    *
    * Similar to the ZREVRANGEBYSCORE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param startScore
    *   The maximum score.
    * @param startInclusive
    *   Whether the maximum score is inclusive.
    * @param endScore
    *   The minimum score.
    * @param endInclusive
    *   Whether the minimum score is inclusive.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of members within the score range, ordered from high to low.
    */
  def zRevRangeByScore[V](
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Return members in a sorted set, by score, ordered from high to low with offset and count.
    *
    * Similar to the ZREVRANGEBYSCORE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param startScore
    *   The maximum score.
    * @param startInclusive
    *   Whether the maximum score is inclusive.
    * @param endScore
    *   The minimum score.
    * @param endInclusive
    *   Whether the minimum score is inclusive.
    * @param offset
    *   The starting point for the range.
    * @param count
    *   The number of elements to return.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   An iterable of members within the score range, ordered from high to low.
    */
  def zRevRangeByScore[V](
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Determine the index of a member in a sorted set, ordered from high to low.
    *
    * Similar to the ZREVRANK command.
    *
    * @param key
    *   The key of the sorted set.
    * @param member
    *   The member whose rank is to be found.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The reverse rank of the member, or None if the member is not in the set.
    */
  def zRevRank[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int]

  /** Incrementally iterate over sorted set elements.
    *
    * Similar to the ZSCAN command.
    *
    * @param key
    *   The key of the sorted set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of set elements.
    */
  def zScan[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

  /** Incrementally iterate over sorted set elements that match a pattern.
    *
    * Similar to the ZSCAN command.
    *
    * @param key
    *   The key of the sorted set.
    * @param pattern
    *   The pattern that elements should match.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of set elements.
    */
  def zScan[V](key: String, pattern: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

  /** Incrementally iterate over sorted set elements with a limit on count.
    *
    * Similar to the ZSCAN command.
    *
    * @param key
    *   The key of the sorted set.
    * @param count
    *   The number of elements returned at each iteration.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A stream of set elements.
    */
  def zScan[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

  /** Incrementally iterate over sorted set elements that match a pattern and have a limit on count.
    *
    * Similar to the ZSCAN command.
    *
    * @param key
    *   The key of the sorted set.
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
  def zScan[V](key: String, pattern: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V]

  /** Get the score associated with the given member in a sorted set.
    *
    * Similar to the ZSCORE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param member
    *   The member whose score is to be returned.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The score of the member, or None if the member is not in the set.
    */
  def zScore[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Double]

  /** Get the scores associated with the given members in a sorted set.
    *
    * Similar to the ZSCORE command.
    *
    * @param key
    *   The key of the sorted set.
    * @param members
    *   The members whose scores are to be returned.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @param encoder
    *   The encoder instance that converts `T` to `V`.
    * @tparam T
    *   Type of the encoded/decoded value.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   A list of scores corresponding to the members.
    */
  def zScore[T, V](key: String, members: Iterable[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[List[Double]]

  /** Add multiple sorted sets and return the union.
    *
    * Similar to the ZUNION command.
    *
    * @param key
    *   The key of the initial sorted set.
    * @param args
    *   Options for the union, such as additional keys and weights for each set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The resulting iterable of the union.
    */
  def zUnion[V](key: String, args: SetUnionArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Add multiple sorted sets and return the union.
    *
    * Similar to the ZUNION command.
    *
    * @param key
    *   The key of the initial sorted set.
    * @param args
    *   Options for the union, such as additional keys and weights for each set. Note: `SetReadArgs`
    *   is expected to extend or be derived from `SetUnionArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @tparam V
    *   Type of the values stored in Redisson.
    * @return
    *   The resulting iterable of the union.
    */
  def zUnion[V](key: String, args: SetReadArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V]

  /** Add multiple sorted sets and store the union.
    *
    * Similar to the ZUNIONSTORE command.
    *
    * @param key
    *   The destination key where the result is stored.
    * @param args
    *   Options for the union, such as additional keys and weights for each set.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements in the resulting set.
    */
  def zUnionStore(key: String, args: SetUnionArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Int]

  /** Add multiple sorted sets and store the union.
    *
    * Similar to the ZUNIONSTORE command.
    *
    * @param key
    *   The destination key where the result is stored.
    * @param args
    *   Options for the union, such as additional keys and weights for each set. Note: `SetReadArgs`
    *   is expected to extend or be derived from `SetUnionArgs`.
    * @param codec
    *   Wrapper around Redisson codec. Default: taken from config.
    * @return
    *   The number of elements in the resulting set.
    */
  def zUnionStore(key: String, args: SetReadArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Int]

}

trait RedisSortedSetOperationsImpl extends RedisSortedSetOperations with DefaultRedisEncoders {
  protected val redissonClient: RedissonClient

  private def scoredSortedSet[V](key: String)(implicit
    codec: RCodec[_, V]
  ): RScoredSortedSet[V] = codec
    .underlying
    .map(redissonClient.getScoredSortedSet[V](key, _))
    .getOrElse(redissonClient.getScoredSortedSet[V](key))

  private def lexSortedSet(key: String): RLexSortedSet = redissonClient.getLexSortedSet(key)

  override def bzmPopMax[V](key: String, timeout: Duration, count: Int, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder15[V] = new ResultBuilder15[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, Map[T, Double]]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).pollLastEntriesFromAnyAsync(timeout, count, keys: _*))
      .flatMap(JavaDecoders.fromMapScoredValue(_))
  }

  override def bzmPopMin[V](key: String, timeout: Duration, count: Int, keys: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder15[V] = new ResultBuilder15[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, Map[T, Double]]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).pollFirstEntriesFromAnyAsync(timeout, count, keys: _*)
      )
      .flatMap(JavaDecoders.fromMapScoredValue(_))
  }

  override def bzPopMax[V](key: String, timeout: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).pollLastAsync(timeout.toMillis, TimeUnit.MILLISECONDS)
      )
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def bzPopMin[V](key: String, timeout: Duration)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).pollFirstAsync(timeout.toMillis, TimeUnit.MILLISECONDS)
      )
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def zAdd[T, V](key: String, score: Double, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(scoredSortedSet(key).addAsync(score, codec.encode(member)))
    .map(Boolean.unbox)

  override def zAdd[T, V](key: String, scoreMembers: Map[T, Double])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).addAllAsync(JavaEncoders.fromMapWithWeight(scoreMembers))
    )
    .map(_.toInt)

  override def zCard(key: String)(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO.fromCompletionStage(scoredSortedSet(key).sizeAsync()).map(_.toInt)

  override def zCount(
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean
  )(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).countAsync(startScore, startInclusive, endScore, endInclusive)
    )
    .map(_.toInt)

  override def zDiff[V](key: String, names: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).readDiffAsync(names: _*))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zDiffStore(key: String, names: Seq[String])(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO.fromCompletionStage(scoredSortedSet(key).diffAsync(names: _*)).map(_.toInt)

  override def zIncrBy[T, V](key: String, increment: Number, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Double] = ZIO
    .fromCompletionStage(scoredSortedSet(key).addScoreAsync(codec.encode(member), increment))
    .map(_.toDouble)

  override def zInter[V](key: String, args: SetIntersectionArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).readIntersectionAsync(args))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zInter[V](key: String, args: SetReadArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = zInter[V](key, args.asInstanceOf[SetIntersectionArgs])

  override def zInterStore(key: String, args: SetIntersectionArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO.fromCompletionStage(scoredSortedSet(key).intersectionAsync(args)).map(_.toInt)

  override def zInterStore(key: String, args: SetReadArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Int] = zInterStore(key, args.asInstanceOf[SetIntersectionArgs])

  override def zLexCount(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): Task[Int] = ZIO
    .fromCompletionStage(
      lexSortedSet(key).countAsync(fromElement, fromInclusive, toElement, toInclusive)
    )
    .map(_.toInt)

  override def zLexCountMin(key: String, toElement: String, toInclusive: Boolean): Task[Int] = ZIO
    .fromCompletionStage(lexSortedSet(key).countHeadAsync(toElement, toInclusive))
    .map(_.toInt)

  override def zLexCountMax(key: String, fromElement: String, fromInclusive: Boolean): Task[Int] =
    ZIO
      .fromCompletionStage(lexSortedSet(key).countTailAsync(fromElement, fromInclusive))
      .map(_.toInt)

  override def zmPopMax[V](key: String, count: Int, names: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder15[V] = new ResultBuilder15[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, Map[T, Double]]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).pollLastEntriesFromAnyAsync(count, names: _*))
      .flatMap(JavaDecoders.fromMapScoredValue(_))
  }

  override def zmPopMin[V](key: String, count: Int, names: Seq[String])(implicit
    codec: RCodec[_, V]
  ): ResultBuilder15[V] = new ResultBuilder15[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[String, Map[T, Double]]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).pollFirstEntriesFromAnyAsync(count, names: _*))
      .flatMap(JavaDecoders.fromMapScoredValue(_))
  }

  override def zPopMax[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).pollLastAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def zPopMax[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).pollLastAsync(count))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zPopMin[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).pollFirstAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def zPopMin[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).pollFirstAsync(count))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRandMember[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder1[V] = new ResultBuilder1[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Option[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).randomAsync())
      .flatMap(JavaDecoders.fromNullableValue(_))
  }

  override def zRandMember[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).randomAsync(count))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRange[V](key: String, fromScore: Int, toScore: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).valueRangeAsync(fromScore, toScore))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRange[V](
    key: String,
    fromScore: Int,
    fromInclusive: Boolean,
    toScore: Int,
    toInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).valueRangeAsync(
          fromScore.toDouble,
          fromInclusive,
          toScore.toDouble,
          toInclusive
        )
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRange[V](
    key: String,
    fromScore: Int,
    fromInclusive: Boolean,
    toScore: Int,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).valueRangeAsync(
          fromScore.toDouble,
          fromInclusive,
          toScore.toDouble,
          toInclusive,
          offset,
          count
        )
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRangeWithScores[V](key: String, fromScore: Int, toScore: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V] = new ResultBuilder5[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[T, Double]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).entryRangeAsync(fromScore, toScore))
      .flatMap(JavaDecoders.fromCollectionScored(_))
  }

  override def zRangeWithScores[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V] = new ResultBuilder5[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[T, Double]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).entryRangeAsync(fromScore, fromInclusive, toScore, toInclusive)
      )
      .flatMap(JavaDecoders.fromCollectionScored(_))
  }

  override def zRangeWithScores[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V] = new ResultBuilder5[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[T, Double]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).entryRangeAsync(
          fromScore,
          fromInclusive,
          toScore,
          toInclusive,
          offset,
          count
        )
      )
      .flatMap(JavaDecoders.fromCollectionScored(_))
  }

  override def zRangeByLexMin(
    key: String,
    toElement: String,
    toInclusive: Boolean
  ): ResultBuilder8[String] = new ResultBuilder8[String] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[String, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(lexSortedSet(key).rangeHeadAsync(toElement, toInclusive))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRangeByLexMin(
    key: String,
    toElement: String,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  ): ResultBuilder8[String] = new ResultBuilder8[String] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[String, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(lexSortedSet(key).rangeHeadAsync(toElement, toInclusive, offset, count))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRangeByLexMax(
    key: String,
    fromElement: String,
    fromInclusive: Boolean
  ): ResultBuilder8[String] = new ResultBuilder8[String] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[String, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(lexSortedSet(key).rangeTailAsync(fromElement, fromInclusive))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRangeByLexMax(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    offset: Int,
    count: Int
  ): ResultBuilder8[String] = new ResultBuilder8[String] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[String, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        lexSortedSet(key).rangeTailAsync(fromElement, fromInclusive, offset, count)
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRangeByLex(key: String, startIndex: Int, endIndex: Int): ResultBuilder8[String] =
    new ResultBuilder8[String] {
      override def as[T](implicit
        decoder: BaseRedisDecoder[String, T]
      ): Task[Iterable[T]] = ZIO
        .fromCompletionStage(lexSortedSet(key).rangeAsync(startIndex, endIndex))
        .flatMap(JavaDecoders.fromCollection(_))
    }

  override def zRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): ResultBuilder8[String] = new ResultBuilder8[String] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[String, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        lexSortedSet(key).rangeAsync(fromElement, fromInclusive, toElement, toInclusive)
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRangeByScore[V](
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).valueRangeAsync(startScore, startInclusive, endScore, endInclusive)
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRangeByScore[V](
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).valueRangeAsync(
          startScore,
          startInclusive,
          endScore,
          endInclusive,
          offset,
          count
        )
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRank[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).rankAsync(codec.encode(member)))
    .map(_.toInt)

  override def zRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  ): ResultBuilder8[String] = new ResultBuilder8[String] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[String, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        lexSortedSet(key).rangeAsync(
          fromElement,
          fromInclusive,
          toElement,
          toInclusive,
          offset,
          count
        )
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRem[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(scoredSortedSet(key).removeAsync(codec.encode(member)))
    .map(Boolean.unbox)

  override def zRem[T, V](key: String, members: Iterable[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Boolean] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).removeAllAsync(members.map(codec.encode(_)).asJavaCollection)
    )
    .map(Boolean.unbox)

  override def zRemRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): Task[Int] = ZIO
    .fromCompletionStage(
      lexSortedSet(key).removeRangeAsync(fromElement, fromInclusive, toElement, toInclusive)
    )
    .map(_.toInt)

  override def zRemRangeByLexMin(key: String, toElement: String, toInclusive: Boolean): Task[Int] =
    ZIO
      .fromCompletionStage(lexSortedSet(key).removeRangeHeadAsync(toElement, toInclusive))
      .map(_.toInt)

  override def zRemRangeByLexMax(
    key: String,
    fromElement: String,
    fromInclusive: Boolean
  ): Task[Int] = ZIO
    .fromCompletionStage(lexSortedSet(key).removeRangeTailAsync(fromElement, fromInclusive))
    .map(_.toInt)

  override def zRemRangeByRank(key: String, startIndex: Int, endIndex: Int)(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).removeRangeByRankAsync(startIndex, endIndex))
    .map(_.toInt)

  override def zRemRangeByScore(
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean
  )(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).removeRangeByScoreAsync(
        startScore,
        startInclusive,
        endScore,
        endInclusive
      )
    )
    .map(_.toInt)

  override def zRevRange[V](key: String, fromScore: Int, toScore: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).valueRangeReversedAsync(fromScore, toScore))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRevRange[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).valueRangeReversedAsync(fromScore, fromInclusive, toScore, toInclusive)
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRevRange[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).valueRangeReversedAsync(
          fromScore,
          fromInclusive,
          toScore,
          toInclusive,
          offset,
          count
        )
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRevRangeWithScores[V](key: String, fromScore: Int, toScore: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V] = new ResultBuilder5[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[T, Double]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).entryRangeReversedAsync(fromScore, toScore))
      .flatMap(JavaDecoders.fromCollectionScored(_))
  }

  override def zRevRangeWithScores[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V] = new ResultBuilder5[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[T, Double]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).entryRangeReversedAsync(fromScore, fromInclusive, toScore, toInclusive)
      )
      .flatMap(JavaDecoders.fromCollectionScored(_))
  }

  override def zRevRangeWithScores[V](
    key: String,
    fromScore: Double,
    fromInclusive: Boolean,
    toScore: Double,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder5[V] = new ResultBuilder5[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Map[T, Double]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).entryRangeReversedAsync(
          fromScore,
          fromInclusive,
          toScore,
          toInclusive,
          offset,
          count
        )
      )
      .flatMap(JavaDecoders.fromCollectionScored(_))
  }

  override def zRevRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): ResultBuilder8[String] = new ResultBuilder8[String] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[String, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        lexSortedSet(key).rangeReversedAsync(fromElement, fromInclusive, toElement, toInclusive)
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRevRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  ): ResultBuilder8[String] = new ResultBuilder8[String] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[String, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        lexSortedSet(key).rangeReversedAsync(
          fromElement,
          fromInclusive,
          toElement,
          toInclusive,
          offset,
          count
        )
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRevRangeByScore[V](
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).valueRangeReversedAsync(
          startScore,
          startInclusive,
          endScore,
          endInclusive
        )
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRevRangeByScore[V](
    key: String,
    startScore: Double,
    startInclusive: Boolean,
    endScore: Double,
    endInclusive: Boolean,
    offset: Int,
    count: Int
  )(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(
        scoredSortedSet(key).valueRangeReversedAsync(
          startScore,
          startInclusive,
          endScore,
          endInclusive,
          offset,
          count
        )
      )
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zRevRank[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).revRankAsync(codec.encode(member)))
    .map(_.toInt)

  override def zScan[V](key: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(scoredSortedSet(key).stream())
      .mapZIO(JavaDecoders.fromValue(_))
  }

  override def zScan[V](key: String, pattern: String)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(scoredSortedSet(key).stream(pattern))
      .mapZIO(JavaDecoders.fromValue(_))
  }

  override def zScan[V](key: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(scoredSortedSet(key).stream(count))
      .mapZIO(JavaDecoders.fromValue(_))
  }

  override def zScan[V](key: String, pattern: String, count: Int)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder10[V] = new ResultBuilder10[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): ZStream[Any, Throwable, T] = ZStream
      .fromJavaStream(scoredSortedSet(key).stream(pattern, count))
      .mapZIO(JavaDecoders.fromValue(_))
  }

  override def zScore[T, V](key: String, member: T)(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[Double] = ZIO
    .fromCompletionStage(scoredSortedSet(key).getScoreAsync(codec.encode(member)))
    .map(_.toDouble)

  override def zScore[T, V](key: String, members: Iterable[T])(implicit
    codec: RCodec[_, V],
    encoder: BaseRedisEncoder[T, V]
  ): Task[List[Double]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).getScoreAsync(members.map(codec.encode(_)).asJavaCollection)
    )
    .map(JavaDecoders.fromListScored)

  override def zUnion[V](key: String, args: SetUnionArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = new ResultBuilder8[V] {
    override def as[T](implicit
      decoder: BaseRedisDecoder[V, T]
    ): Task[Iterable[T]] = ZIO
      .fromCompletionStage(scoredSortedSet(key).readUnionAsync(args))
      .flatMap(JavaDecoders.fromCollection(_))
  }

  override def zUnion[V](key: String, args: SetReadArgs)(implicit
    codec: RCodec[_, V]
  ): ResultBuilder8[V] = zUnion[V](key, args.asInstanceOf[SetUnionArgs])

  override def zUnionStore(key: String, args: SetUnionArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Int] = ZIO.fromCompletionStage(scoredSortedSet(key).unionAsync(args)).map(_.toInt)

  override def zUnionStore(key: String, args: SetReadArgs)(implicit
    codec: RCodec[_, _]
  ): Task[Int] = zUnionStore(key, args.asInstanceOf[SetUnionArgs])

}

case class RedisSortedSetOperationsLive(redissonClient: RedissonClient)
    extends RedisSortedSetOperationsImpl

object RedisSortedSetOperations {

  val live: URLayer[RedissonClient, RedisSortedSetOperations] =
    ZLayer.fromFunction(RedisSortedSetOperationsLive.apply _)

}
