package io.kinoplan.utils.zio.redisson.utils

import io.kinoplan.utils.redisson.codec.RedisDecoder
import org.redisson.client.protocol.ScoredEntry
import zio.{Duration, Task, ZIO}

import scala.jdk.CollectionConverters.{CollectionHasAsScala, MapHasAsScala}

private[redisson] object JavaDecoders {

  def fromCollection[T: RedisDecoder](
    jCollection: java.util.Collection[String]
  ): Task[Iterable[T]] =
    if (jCollection == null) ZIO.succeed(Iterable.empty[T])
    else ZIO.foreach(jCollection.asScala)(fromValue(_))

  def fromCollectionScored[T: RedisDecoder](
    jCollection: java.util.Collection[ScoredEntry[String]]
  ): Task[Map[T, Double]] =
    if (jCollection == null) ZIO.succeed(Map.empty[T, Double])
    else ZIO
      .foreach(jCollection.asScala)(entry =>
        fromValue(entry.getValue).map(_ -> entry.getScore.toDouble)
      )
      .map(_.toMap)

  def fromCollectionNullable[T: RedisDecoder](
    jCollection: java.util.Collection[String]
  ): Task[Iterable[Option[T]]] =
    if (jCollection == null) ZIO.succeed(Iterable.empty[Option[T]])
    else ZIO.foreach(jCollection.asScala)(fromNullableValue(_))

  def fromList[T: RedisDecoder](jList: java.util.List[String]): Task[List[T]] =
    if (jList == null) ZIO.succeed(List.empty[T])
    else ZIO.foreach(jList.asScala.map(RedisDecoder[T].decode).toList)(ZIO.fromTry(_))

  def fromListScored(jList: java.util.List[java.lang.Double]): List[Double] =
    if (jList == null) List.empty[Double]
    else jList.asScala.map(_.toDouble).toList

  def fromSet[T: RedisDecoder](jSet: java.util.Set[String]): Task[Set[T]] =
    if (jSet == null) ZIO.succeed(Set.empty[T])
    else ZIO.foreach(jSet.asScala.map(RedisDecoder[T].decode).toSet)(ZIO.fromTry(_))

  def fromSetKeys(jSet: java.util.Set[String]): Set[String] =
    if (jSet == null) Set.empty[String]
    else jSet.asScala.toSet

  def fromMap[T: RedisDecoder](jMap: java.util.Map[String, String]): Task[Map[String, T]] =
    if (jMap == null) ZIO.succeed(Map.empty[String, T])
    else ZIO.foreach(jMap.asScala.toMap) { case (k, v) =>
      ZIO.fromTry(RedisDecoder[T].decode(v)).map(k -> _)
    }

  def fromMapKey[T: RedisDecoder](jMap: java.util.Map[String, String]): Task[Map[T, String]] =
    if (jMap == null) ZIO.succeed(Map.empty[T, String])
    else ZIO.foreach(jMap.asScala.toMap) { case (k, v) =>
      ZIO.fromTry(RedisDecoder[T].decode(k)).map(_ -> v)
    }

  def fromMapEntry[T: RedisDecoder](
    jMapEntry: java.util.Map.Entry[String, String]
  ): Task[(String, T)] = ZIO
    .fromTry(RedisDecoder[T].decode(jMapEntry.getValue))
    .map(jMapEntry.getKey -> _)

  def fromMapBoolean(jMap: java.util.Map[String, java.lang.Boolean]): Map[String, Boolean] =
    if (jMap == null) Map.empty[String, Boolean]
    else jMap
      .asScala
      .map { case (k, v) =>
        k -> Boolean.unbox(v)
      }
      .toMap

  def fromMapMillis(jMap: java.util.Map[String, java.lang.Long]): Map[String, Option[Duration]] =
    if (jMap == null) Map.empty[String, Option[Duration]]
    else jMap
      .asScala
      .map { case (k, v) =>
        k -> fromMillis(v)
      }
      .toMap

  def fromMapScored[T: RedisDecoder](
    jMap: java.util.Map[String, java.lang.Double]
  ): Task[Map[T, Double]] =
    if (jMap == null) ZIO.succeed(Map.empty[T, Double])
    else ZIO.foreach(jMap.asScala.toMap) { case (k, v) =>
      ZIO.fromTry(RedisDecoder[T].decode(k)).map(_ -> v.toDouble)
    }

  def fromMapListValue[T: RedisDecoder, K](
    jMap: java.util.Map[K, java.util.List[String]]
  ): Task[Map[K, List[T]]] =
    if (jMap == null) ZIO.succeed(Map.empty[K, List[T]])
    else ZIO.foreach(jMap.asScala.toMap) { case (key, jValue) =>
      fromList(jValue).map(value => (key, value))
    }

  def fromMapScoredValue[T: RedisDecoder, K](
    jMap: java.util.Map[K, java.util.Map[String, java.lang.Double]]
  ): Task[Map[K, Map[T, Double]]] =
    if (jMap == null) ZIO.succeed(Map.empty[K, Map[T, Double]])
    else ZIO.foreach(jMap.asScala.toMap) { case (key, jValue) =>
      fromMapScored(jValue).map(value => (key, value))
    }

  def fromNullableValue[T: RedisDecoder](nullableValue: String): Task[Option[T]] =
    if (nullableValue == null) ZIO.succeed(Option.empty[T])
    else fromValue(nullableValue).asSome

  def fromNullableString(nullableValue: String): Option[String] = Option(nullableValue)

  def fromValue[T: RedisDecoder](value: String): Task[T] = ZIO.fromTry(RedisDecoder[T].decode(value))

  def fromMillis(value: java.lang.Long): Option[Duration] =
    if (value < 0) None
    else Some(Duration.fromMillis(value.toLong))

}
