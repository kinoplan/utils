package io.kinoplan.utils.zio.redisson.utils

import org.redisson.api.StreamMessageId
import org.redisson.client.protocol.ScoredEntry
import zio.{Duration, Task, ZIO}

import io.kinoplan.utils.cross.collection.converters._
import io.kinoplan.utils.redisson.codec.base.BaseRedisDecoder
import io.kinoplan.utils.zio.redisson.codec.RCodec

private[redisson] object JavaDecoders {

  def fromCollection[T, K, V](jCollection: java.util.Collection[V])(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Iterable[T]] =
    if (jCollection == null) ZIO.succeed(Iterable.empty[T])
    else ZIO.foreach(jCollection.asScala)(value => ZIO.fromTry(codec.decode(value)))

  def fromCollectionScored[T, K, V](jCollection: java.util.Collection[ScoredEntry[V]])(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[T, Double]] =
    if (jCollection == null) ZIO.succeed(Map.empty[T, Double])
    else ZIO
      .foreach(jCollection.asScala)(entry =>
        fromValue(entry.getValue).map(_ -> entry.getScore.toDouble)
      )
      .map(_.toMap)

  def fromCollectionNullable[T, K, V](jCollection: java.util.Collection[V])(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Iterable[Option[T]]] =
    if (jCollection == null) ZIO.succeed(Iterable.empty[Option[T]])
    else ZIO.foreach(jCollection.asScala)(fromNullableValue(_))

  def fromListUnderlying[T](jList: java.util.List[T]): List[T] =
    if (jList == null) List.empty[T]
    else jList.asScala.toList

  def fromList[T, K, V](jList: java.util.List[V])(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[List[T]] =
    if (jList == null) ZIO.succeed(List.empty[T])
    else ZIO.foreach(jList.asScala.map(codec.decode(_)).toList)(ZIO.fromTry(_))

  def fromListScored(jList: java.util.List[java.lang.Double]): List[Double] =
    if (jList == null) List.empty[Double]
    else jList.asScala.map(_.toDouble).toList

  def fromSet[T, K, V](jSet: java.util.Set[V])(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Set[T]] =
    if (jSet == null) ZIO.succeed(Set.empty[T])
    else ZIO.foreach(jSet.asScala.map(codec.decode(_)).toSet)(ZIO.fromTry(_))

  def fromSetKeys[K](jSet: java.util.Set[K]): Set[K] =
    if (jSet == null) Set.empty[K]
    else jSet.asScala.toSet

  def fromMap[T, K, V](jMap: java.util.Map[K, V])(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[K, T]] =
    if (jMap == null) ZIO.succeed(Map.empty[K, T])
    else ZIO.foreach(jMap.asScala.toMap) { case (k, v) =>
      ZIO.fromTry(codec.decode(v)).map(k -> _)
    }

  def fromMapKey[T, K, V, A](jMap: java.util.Map[V, A])(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[T, A]] =
    if (jMap == null) ZIO.succeed(Map.empty[T, A])
    else ZIO.foreach(jMap.asScala.toMap) { case (k, v) =>
      ZIO.fromTry(codec.decode(k)).map(_ -> v)
    }

  def fromMapValue[T, K, V](jMap: java.util.Map[String, V])(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[String, T]] =
    if (jMap == null) ZIO.succeed(Map.empty[String, T])
    else ZIO.foreach(jMap.asScala.toMap) { case (k, v) =>
      ZIO.fromTry(codec.decode(v)).map(k -> _)
    }

  def fromMapEntry[T, K, V](jMapEntry: java.util.Map.Entry[K, V])(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[(K, T)] = ZIO.fromTry(codec.decode(jMapEntry.getValue)).map(jMapEntry.getKey -> _)

  def fromMapBoolean[K](jMap: java.util.Map[K, java.lang.Boolean]): Map[K, Boolean] =
    if (jMap == null) Map.empty[K, Boolean]
    else jMap
      .asScala
      .map { case (k, v) =>
        k -> Boolean.unbox(v)
      }
      .toMap

  def fromMapMillis[K](jMap: java.util.Map[K, java.lang.Long]): Map[K, Option[Duration]] =
    if (jMap == null) Map.empty[K, Option[Duration]]
    else jMap
      .asScala
      .map { case (k, v) =>
        k -> fromMillis(v)
      }
      .toMap

  def fromMapScored[T, V](jMap: java.util.Map[V, java.lang.Double])(implicit
    codec: RCodec[_, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[T, Double]] =
    if (jMap == null) ZIO.succeed(Map.empty[T, Double])
    else ZIO.foreach(jMap.asScala.toMap) { case (k, v) =>
      ZIO.fromTry(codec.decode(k)).map(_ -> v.toDouble)
    }

  def fromMapListValue[T, V](jMap: java.util.Map[String, java.util.List[V]])(implicit
    codec: RCodec[_, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[String, List[T]]] =
    if (jMap == null) ZIO.succeed(Map.empty[String, List[T]])
    else ZIO.foreach(jMap.asScala.toMap) { case (key, jValue) =>
      fromList(jValue).map(value => (key, value))
    }

  def fromMapScoredValue[T, V](
    jMap: java.util.Map[String, java.util.Map[V, java.lang.Double]]
  )(implicit
    codec: RCodec[_, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[String, Map[T, Double]]] =
    if (jMap == null) ZIO.succeed(Map.empty[String, Map[T, Double]])
    else ZIO.foreach(jMap.asScala.toMap) { case (key, jValue) =>
      fromMapScored(jValue).map(value => (key, value))
    }

  def fromNoKeys[T, V](value: V)(implicit
    codec: RCodec[_, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[T] = ZIO.fromTry(codec.decode(value))

  def fromValue[T, V](value: V)(implicit
    codec: RCodec[_, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[T] = ZIO.fromTry(codec.decode(value))

  def fromNullableValue[T, K, V](nullableValue: V)(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Option[T]] =
    if (nullableValue == null) ZIO.succeed(Option.empty[T])
    else ZIO.fromTry(codec.decode(nullableValue)).asSome

  def fromNullableValue[T](nullableValue: T): Option[T] = Option(nullableValue)

  def fromNullableString[T](nullableValue: String)(implicit
    decoder: BaseRedisDecoder[String, T]
  ): Task[Option[T]] =
    if (nullableValue == null) ZIO.succeed(Option.empty[T])
    else ZIO.fromTry(decoder.decode(nullableValue)).asSome

  def fromMillis(value: java.lang.Long): Option[Duration] =
    if (value < 0) None
    else Some(Duration.fromMillis(value.toLong))

  def fromStreamMessages[T, K, V](
    jMap: java.util.Map[StreamMessageId, java.util.Map[K, V]]
  )(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[StreamMessageId, Map[K, T]]] =
    if (jMap == null) ZIO.succeed(Map.empty[StreamMessageId, Map[K, T]])
    else ZIO.foreach(jMap.asScala.toMap) { case (key, jValue) =>
      fromMap(jValue).map(value => (key, value))
    }

  def fromStreamMultiMessages[T, K, V](
    jMap: java.util.Map[String, java.util.Map[StreamMessageId, java.util.Map[K, V]]]
  )(implicit
    codec: RCodec[K, V],
    decoder: BaseRedisDecoder[V, T]
  ): Task[Map[String, Map[StreamMessageId, Map[K, T]]]] =
    if (jMap == null) ZIO.succeed(Map.empty[String, Map[StreamMessageId, Map[K, T]]])
    else ZIO.foreach(jMap.asScala.toMap) { case (key, jValue) =>
      fromStreamMessages(jValue).map(value => (key, value))
    }

}
