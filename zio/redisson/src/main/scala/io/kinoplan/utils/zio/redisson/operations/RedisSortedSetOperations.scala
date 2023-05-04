package io.kinoplan.utils.zio.redisson.operations

import io.kinoplan.utils.redisson.codec.{DefaultRedisEncoders, RedisDecoder, RedisEncoder}
import io.kinoplan.utils.zio.redisson.utils.JavaEncoders.encodeMapWithWeight
import io.kinoplan.utils.zio.redisson.utils.{JavaDecoders, JavaEncoders}
import org.redisson.api.RScoredSortedSet.Aggregate
import org.redisson.api.{RLexSortedSet, RScoredSortedSet, RedissonClient}
import org.redisson.client.codec.StringCodec
import zio.{Duration, Task, URLayer, ZIO, ZLayer}

import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters._

trait RedisSortedSetOperations {

  def bzmPopMax[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    names: Seq[String]
  ): Task[Map[String, Map[T, Double]]]

  def bzmPopMin[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    names: Seq[String]
  ): Task[Map[String, Map[T, Double]]]

  def bzPopMax[T: RedisDecoder](key: String, timeout: Duration): Task[Option[T]]

  def bzPopMin[T: RedisDecoder](key: String, timeout: Duration): Task[Option[T]]

  def zAdd[T: RedisEncoder](key: String, score: Double, member: T): Task[Boolean]

  def zAdd[T: RedisEncoder](key: String, scoreMembers: Map[T, Double]): Task[Int]

  def zCard(key: String): Task[Int]

  def zCount(
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean
  ): Task[Int]

  def zDiff[T: RedisDecoder](key: String, names: Seq[String]): Task[Iterable[T]]

  def zDiffStore[T: RedisDecoder](key: String, names: Seq[String]): Task[Int]

  def zIncrBy[T: RedisEncoder](key: String, increment: Number, member: T): Task[Double]

  def zInter[T: RedisDecoder](key: String, names: Seq[String]): Task[Iterable[T]]

  def zInter[T: RedisDecoder](
    key: String,
    aggregate: Aggregate,
    names: Seq[String]
  ): Task[Iterable[T]]

  def zInter[T: RedisDecoder](key: String, nameWithWeight: Map[String, Double]): Task[Iterable[T]]

  def zInter[T: RedisDecoder](
    key: String,
    aggregate: Aggregate,
    nameWithWeight: Map[String, Double]
  ): Task[Iterable[T]]

  def zInterStore(key: String, names: Seq[String]): Task[Int]

  def zInterStore(key: String, aggregate: Aggregate, names: Seq[String]): Task[Int]

  def zInterStore(key: String, nameWithWeight: Map[String, Double]): Task[Int]

  def zInterStore(key: String, aggregate: Aggregate, nameWithWeight: Map[String, Double]): Task[Int]

  def zLexCount(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): Task[Int]

  def zLexCountMin(key: String, toElement: String, toInclusive: Boolean): Task[Int]

  def zLexCountMax(key: String, fromElement: String, fromInclusive: Boolean): Task[Int]

  def zmPopMax[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    names: Seq[String]
  ): Task[Map[String, Map[T, Double]]]

  def zmPopMin[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    names: Seq[String]
  ): Task[Map[String, Map[T, Double]]]

  def zPopMax[T: RedisDecoder](key: String): Task[Option[T]]

  def zPopMax[T: RedisDecoder](key: String, count: Int): Task[Iterable[T]]

  def zPopMin[T: RedisDecoder](key: String): Task[Option[T]]

  def zPopMin[T: RedisDecoder](key: String, count: Int): Task[Iterable[T]]

  def zRandMember[T: RedisDecoder](key: String): Task[Option[T]]

  def zRandMember[T: RedisDecoder](key: String, count: Int): Task[Iterable[T]]

  def zRange[T: RedisDecoder](key: String, fromScore: Int, toScore: Int): Task[Iterable[T]]

  def zRange[T: RedisDecoder](
    key: String,
    fromScore: Int,
    fromInc: Boolean,
    toScore: Int,
    toInc: Boolean
  ): Task[Iterable[T]]

  def zRange[T: RedisDecoder](
    key: String,
    fromScore: Int,
    fromInc: Boolean,
    toScore: Int,
    toInc: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]]

  def zRevRange[T: RedisDecoder](key: String, fromScore: Int, toScore: Int): Task[Iterable[T]]

  def zRevRange[T: RedisDecoder](
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean
  ): Task[Iterable[T]]

  def zRevRange[T: RedisDecoder](
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]]

  def zRangeByLex[T: RedisDecoder](key: String, startIndex: Int, endIndex: Int): Task[Iterable[T]]

  def zRangeByLex[T: RedisDecoder](
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): Task[Iterable[T]]

  def zRangeByLex[T: RedisDecoder](
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]]

  def zRevRangeByLex[T: RedisDecoder](
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): Task[Iterable[T]]

  def zRevRangeByLex[T: RedisDecoder](
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]]

  def zRangeByScore[T: RedisDecoder](key: String, startIndex: Int, endIndex: Int): Task[Iterable[T]]

  def zRangeByScore[T: RedisDecoder](
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean
  ): Task[Iterable[T]]

  def zRangeByScore[T: RedisDecoder](
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]]

  def zRevRangeByScore[T: RedisDecoder](
    key: String,
    startIndex: Int,
    endIndex: Int
  ): Task[Iterable[T]]

  def zRevRangeByScore[T: RedisDecoder](
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean
  ): Task[Iterable[T]]

  def zRevRangeByScore[T: RedisDecoder](
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]]

  def zRank[T: RedisEncoder](key: String, member: T): Task[Int]

  def zRevRank[T: RedisEncoder](key: String, member: T): Task[Int]

  def zRem[T: RedisEncoder](key: String, member: T): Task[Boolean]

  def zRem[T: RedisEncoder](key: String, members: Seq[T]): Task[Boolean]

  def zRem[T: RedisEncoder](key: String, members: Set[T]): Task[Boolean]

  def zRemRangeByLex(
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): Task[Int]

  def zRemRangeByLexMin(key: String, toElement: String, toInclusive: Boolean): Task[Int]

  def zRemRangeByLexMax(key: String, fromElement: String, fromInclusive: Boolean): Task[Int]

  def zRemRangeByRank(key: String, startIndex: Int, endIndex: Int): Task[Int]

  def zRemRangeByScore(
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean
  ): Task[Int]

  def zScan[T: RedisDecoder](key: String): Task[Iterator[T]]

  def zScan[T: RedisDecoder](key: String, pattern: String): Task[Iterator[T]]

  def zScan[T: RedisDecoder](key: String, count: Int): Task[Iterator[T]]

  def zScan[T: RedisDecoder](key: String, pattern: String, count: Int): Task[Iterator[T]]

  def zScore[T: RedisEncoder](key: String, member: T): Task[Double]

  def zScore[T: RedisEncoder](key: String, members: Seq[T]): Task[List[Double]]

  def zScore[T: RedisEncoder](key: String, members: Set[T]): Task[List[Double]]

  def zUnion[T: RedisDecoder](key: String, names: Seq[String]): Task[Iterable[T]]

  def zUnion[T: RedisDecoder](
    key: String,
    aggregate: Aggregate,
    names: Seq[String]
  ): Task[Iterable[T]]

  def zUnion[T: RedisDecoder](key: String, nameWithWeight: Map[String, Double]): Task[Iterable[T]]

  def zUnion[T: RedisDecoder](
    key: String,
    aggregate: Aggregate,
    nameWithWeight: Map[String, Double]
  ): Task[Iterable[T]]

  def zUnionStore(key: String, names: Seq[String]): Task[Int]

  def zUnionStore(key: String, aggregate: Aggregate, names: Seq[String]): Task[Int]

  def zUnionStore(key: String, nameWithWeight: Map[String, Double]): Task[Int]

  def zUnionStore(key: String, aggregate: Aggregate, nameWithWeight: Map[String, Double]): Task[Int]

}

trait RedisSortedSetOperationsImpl extends RedisSortedSetOperations with DefaultRedisEncoders {
  protected val redissonClient: RedissonClient

  private lazy val scoredSortedSet: String => RScoredSortedSet[String] =
    redissonClient.getScoredSortedSet[String](_, StringCodec.INSTANCE)

  private lazy val lexSortedSet: String => RLexSortedSet = redissonClient.getLexSortedSet

  override def bzmPopMax[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    names: Seq[String]
  ): Task[Map[String, Map[T, Double]]] = ZIO
    .attemptBlocking(scoredSortedSet(key).pollLastEntriesFromAny(timeout, count, names: _*))
    .flatMap(JavaDecoders.decodeMapScoredValue(_))

  override def bzmPopMin[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    names: Seq[String]
  ): Task[Map[String, Map[T, Double]]] = ZIO
    .attemptBlocking(scoredSortedSet(key).pollFirstEntriesFromAny(timeout, count, names: _*))
    .flatMap(JavaDecoders.decodeMapScoredValue(_))

  override def bzPopMax[T: RedisDecoder](key: String, timeout: Duration): Task[Option[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).pollLastAsync(timeout.toMillis, TimeUnit.MILLISECONDS))
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def bzPopMin[T: RedisDecoder](key: String, timeout: Duration): Task[Option[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).pollFirstAsync(timeout.toMillis, TimeUnit.MILLISECONDS))
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def zAdd[T: RedisEncoder](key: String, score: Double, member: T): Task[Boolean] = ZIO
    .fromCompletionStage(scoredSortedSet(key).addAsync(score, RedisEncoder[T].encode(member)))
    .map(_.booleanValue())

  override def zAdd[T: RedisEncoder](key: String, scoreMembers: Map[T, Double]): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).addAllAsync(encodeMapWithWeight(scoreMembers)))
    .map(_.intValue())

  override def zCard(key: String): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).sizeAsync())
    .map(_.intValue())

  override def zCount(
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean
  ): Task[Int] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).countAsync(startScore, startScoreInclusive, endScore, endScoreInclusive)
    )
    .map(_.intValue())

  override def zDiff[T: RedisDecoder](key: String, names: Seq[String]): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).readDiffAsync(names: _*))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zDiffStore[T: RedisDecoder](key: String, names: Seq[String]): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).diffAsync(names: _*))
    .map(_.intValue())

  override def zIncrBy[T: RedisEncoder](key: String, increment: Number, member: T): Task[Double] =
    ZIO
      .fromCompletionStage(
        scoredSortedSet(key).addScoreAsync(RedisEncoder[T].encode(member), increment)
      )
      .map(_.doubleValue())

  override def zInter[T: RedisDecoder](key: String, names: Seq[String]): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).readIntersectionAsync(names: _*))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zInter[T: RedisDecoder](
    key: String,
    aggregate: Aggregate,
    names: Seq[String]
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).readIntersectionAsync(aggregate, names: _*))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zInter[T: RedisDecoder](
    key: String,
    nameWithWeight: Map[String, Double]
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).readIntersectionAsync(encodeMapWithWeight(nameWithWeight))
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zInter[T: RedisDecoder](
    key: String,
    aggregate: Aggregate,
    nameWithWeight: Map[String, Double]
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).readIntersectionAsync(aggregate, encodeMapWithWeight(nameWithWeight))
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zInterStore(key: String, names: Seq[String]): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).intersectionAsync(names: _*))
    .map(_.intValue())

  override def zInterStore(key: String, aggregate: Aggregate, names: Seq[String]): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).intersectionAsync(aggregate, names: _*))
    .map(_.intValue())

  override def zInterStore(key: String, nameWithWeight: Map[String, Double]): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).intersectionAsync(encodeMapWithWeight(nameWithWeight)))
    .map(_.intValue())

  override def zInterStore(
    key: String,
    aggregate: Aggregate,
    nameWithWeight: Map[String, Double]
  ): Task[Int] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).intersectionAsync(aggregate, encodeMapWithWeight(nameWithWeight))
    )
    .map(_.intValue())

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
    .map(_.intValue())

  override def zLexCountMin(key: String, toElement: String, toInclusive: Boolean): Task[Int] = ZIO
    .fromCompletionStage(lexSortedSet(key).countHeadAsync(toElement, toInclusive))
    .map(_.intValue())

  override def zLexCountMax(key: String, fromElement: String, fromInclusive: Boolean): Task[Int] =
    ZIO
      .fromCompletionStage(lexSortedSet(key).countTailAsync(fromElement, fromInclusive))
      .map(_.intValue())

  override def zmPopMax[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    names: Seq[String]
  ): Task[Map[String, Map[T, Double]]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).pollLastEntriesFromAnyAsync(timeout, count, names: _*))
    .flatMap(JavaDecoders.decodeMapScoredValue(_))

  override def zmPopMin[T: RedisDecoder](
    key: String,
    timeout: Duration,
    count: Int,
    names: Seq[String]
  ): Task[Map[String, Map[T, Double]]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).pollFirstEntriesFromAnyAsync(timeout, count, names: _*))
    .flatMap(JavaDecoders.decodeMapScoredValue(_))

  override def zPopMax[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).pollLastAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def zPopMax[T: RedisDecoder](key: String, count: Int): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).pollLastAsync(count))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zPopMin[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).pollFirstAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def zPopMin[T: RedisDecoder](key: String, count: Int): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).pollFirstAsync(count))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRandMember[T: RedisDecoder](key: String): Task[Option[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).randomAsync())
    .flatMap(JavaDecoders.decodeNullableValue(_))

  override def zRandMember[T: RedisDecoder](key: String, count: Int): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).randomAsync(count))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRange[T: RedisDecoder](
    key: String,
    fromScore: Int,
    toScore: Int
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).valueRangeAsync(fromScore, toScore))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRange[T: RedisDecoder](
    key: String,
    fromScore: Int,
    fromInc: Boolean,
    toScore: Int,
    toInc: Boolean
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).valueRangeAsync(fromScore.toDouble, fromInc, toScore.toDouble, toInc)
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRange[T: RedisDecoder](
    key: String,
    fromScore: Int,
    fromInc: Boolean,
    toScore: Int,
    toInc: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).valueRangeAsync(
        fromScore.toDouble,
        fromInc,
        toScore.toDouble,
        toInc,
        offset,
        count
      )
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRevRange[T: RedisDecoder](
    key: String,
    fromScore: Int,
    toScore: Int
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).valueRangeReversedAsync(fromScore, toScore))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRevRange[T: RedisDecoder](
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).valueRangeReversedAsync(fromScore, fromInc, toScore, toInc)
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRevRange[T: RedisDecoder](
    key: String,
    fromScore: Double,
    fromInc: Boolean,
    toScore: Double,
    toInc: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).valueRangeReversedAsync(fromScore, fromInc, toScore, toInc, offset, count)
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRangeByLex[T: RedisDecoder](
    key: String,
    startIndex: Int,
    endIndex: Int
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(lexSortedSet(key).rangeAsync(startIndex, endIndex))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRangeByLex[T: RedisDecoder](
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      lexSortedSet(key).rangeAsync(fromElement, fromInclusive, toElement, toInclusive)
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRangeByLex[T: RedisDecoder](
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      lexSortedSet(key).rangeAsync(fromElement, fromInclusive, toElement, toInclusive, offset, count)
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRevRangeByLex[T: RedisDecoder](
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      lexSortedSet(key).rangeReversedAsync(fromElement, fromInclusive, toElement, toInclusive)
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRevRangeByLex[T: RedisDecoder](
    key: String,
    fromElement: String,
    fromInclusive: Boolean,
    toElement: String,
    toInclusive: Boolean,
    offset: Int,
    count: Int
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
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRangeByScore[T: RedisDecoder](
    key: String,
    startIndex: Int,
    endIndex: Int
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).valueRangeAsync(startIndex, endIndex))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRangeByScore[T: RedisDecoder](
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).valueRangeAsync(
        startScore,
        startScoreInclusive,
        endScore,
        endScoreInclusive
      )
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRangeByScore[T: RedisDecoder](
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).valueRangeAsync(
        startScore,
        startScoreInclusive,
        endScore,
        endScoreInclusive,
        offset,
        count
      )
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRevRangeByScore[T: RedisDecoder](
    key: String,
    startIndex: Int,
    endIndex: Int
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).valueRangeReversedAsync(startIndex, endIndex))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRevRangeByScore[T: RedisDecoder](
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).valueRangeReversedAsync(
        startScore,
        startScoreInclusive,
        endScore,
        endScoreInclusive
      )
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRevRangeByScore[T: RedisDecoder](
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean,
    offset: Int,
    count: Int
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).valueRangeReversedAsync(
        startScore,
        startScoreInclusive,
        endScore,
        endScoreInclusive,
        offset,
        count
      )
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zRank[T: RedisEncoder](key: String, member: T): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).rankAsync(RedisEncoder[T].encode(member)))
    .map(_.intValue())

  override def zRevRank[T: RedisEncoder](key: String, member: T): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).revRankAsync(RedisEncoder[T].encode(member)))
    .map(_.intValue())

  override def zRem[T: RedisEncoder](key: String, member: T): Task[Boolean] = ZIO
    .fromCompletionStage(scoredSortedSet(key).removeAsync(RedisEncoder[T].encode(member)))
    .map(_.booleanValue())

  override def zRem[T: RedisEncoder](key: String, members: Seq[T]): Task[Boolean] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).removeAllAsync(members.map(RedisEncoder[T].encode).asJava)
    )
    .map(_.booleanValue())

  override def zRem[T: RedisEncoder](key: String, members: Set[T]): Task[Boolean] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).removeAllAsync(members.map(RedisEncoder[T].encode).asJava)
    )
    .map(_.booleanValue())

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
    .map(_.intValue())

  override def zRemRangeByLexMin(key: String, toElement: String, toInclusive: Boolean): Task[Int] =
    ZIO
      .fromCompletionStage(lexSortedSet(key).removeRangeHeadAsync(toElement, toInclusive))
      .map(_.intValue())

  override def zRemRangeByLexMax(
    key: String,
    fromElement: String,
    fromInclusive: Boolean
  ): Task[Int] = ZIO
    .fromCompletionStage(lexSortedSet(key).removeRangeTailAsync(fromElement, fromInclusive))
    .map(_.intValue())

  override def zRemRangeByRank(key: String, startIndex: Int, endIndex: Int): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).removeRangeByRankAsync(startIndex, endIndex))
    .map(_.intValue())

  override def zRemRangeByScore(
    key: String,
    startScore: Double,
    startScoreInclusive: Boolean,
    endScore: Double,
    endScoreInclusive: Boolean
  ): Task[Int] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).removeRangeByScoreAsync(
        startScore,
        startScoreInclusive,
        endScore,
        endScoreInclusive
      )
    )
    .map(_.intValue())

  override def zScan[T: RedisDecoder](key: String): Task[Iterator[T]] = ZIO
    .attempt(scoredSortedSet(key).iterator())
    .flatMap(JavaDecoders.decodeIterator(_))

  override def zScan[T: RedisDecoder](key: String, pattern: String): Task[Iterator[T]] = ZIO
    .attempt(scoredSortedSet(key).iterator(pattern))
    .flatMap(JavaDecoders.decodeIterator(_))

  override def zScan[T: RedisDecoder](key: String, count: Int): Task[Iterator[T]] = ZIO
    .attempt(scoredSortedSet(key).iterator(count))
    .flatMap(JavaDecoders.decodeIterator(_))

  override def zScan[T: RedisDecoder](key: String, pattern: String, count: Int): Task[Iterator[T]] =
    ZIO.attempt(scoredSortedSet(key).iterator(pattern, count)).flatMap(JavaDecoders.decodeIterator(_))

  override def zScore[T: RedisEncoder](key: String, member: T): Task[Double] = ZIO
    .attempt(scoredSortedSet(key).getScoreAsync(RedisEncoder[T].encode(member)))
    .map(_.get())

  override def zScore[T: RedisEncoder](key: String, members: Seq[T]): Task[List[Double]] = ZIO
    .attempt(scoredSortedSet(key).getScoreAsync(members.map(RedisEncoder[T].encode).asJava))
    .map(_.get())
    .map(JavaDecoders.decodeListDouble)

  override def zScore[T: RedisEncoder](key: String, members: Set[T]): Task[List[Double]] = ZIO
    .attempt(scoredSortedSet(key).getScoreAsync(members.map(RedisEncoder[T].encode).asJava))
    .map(_.get())
    .map(JavaDecoders.decodeListDouble)

  override def zUnion[T: RedisDecoder](key: String, names: Seq[String]): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).readUnionAsync(names: _*))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zUnion[T: RedisDecoder](
    key: String,
    aggregate: Aggregate,
    names: Seq[String]
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(scoredSortedSet(key).readUnionAsync(aggregate, names: _*))
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zUnion[T: RedisDecoder](
    key: String,
    nameWithWeight: Map[String, Double]
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).readUnionAsync(JavaEncoders.encodeMapWithWeight(nameWithWeight))
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zUnion[T: RedisDecoder](
    key: String,
    aggregate: Aggregate,
    nameWithWeight: Map[String, Double]
  ): Task[Iterable[T]] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).readUnionAsync(aggregate, JavaEncoders.encodeMapWithWeight(nameWithWeight))
    )
    .flatMap(JavaDecoders.decodeCollection(_))

  override def zUnionStore(key: String, names: Seq[String]): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).unionAsync(names: _*))
    .map(_.intValue())

  override def zUnionStore(key: String, aggregate: Aggregate, names: Seq[String]): Task[Int] = ZIO
    .fromCompletionStage(scoredSortedSet(key).unionAsync(aggregate, names: _*))
    .map(_.intValue())

  override def zUnionStore(key: String, nameWithWeight: Map[String, Double]): Task[Int] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).unionAsync(JavaEncoders.encodeMapWithWeight(nameWithWeight))
    )
    .map(_.intValue())

  override def zUnionStore(
    key: String,
    aggregate: Aggregate,
    nameWithWeight: Map[String, Double]
  ): Task[Int] = ZIO
    .fromCompletionStage(
      scoredSortedSet(key).unionAsync(aggregate, JavaEncoders.encodeMapWithWeight(nameWithWeight))
    )
    .map(_.intValue())

}

case class RedisSortedSetOperationsLive(redissonClient: RedissonClient)
    extends RedisSortedSetOperationsImpl

object RedisSortedSetOperations {

  val live: URLayer[RedissonClient, RedisSortedSetOperations] =
    ZLayer.fromZIO(ZIO.serviceWith[RedissonClient](RedisSortedSetOperationsLive))

}
