package io.kinoplan.utils.zio.redisson.utils

import io.kinoplan.utils.redisson.codec.RedisDecoder
import zio.{Task, ZIO}

import scala.jdk.CollectionConverters.{CollectionHasAsScala, IteratorHasAsScala, MapHasAsScala}

private[redisson] object JavaDecoders {

  def decodeCollection[T: RedisDecoder](
    jCollection: java.util.Collection[String]
  ): Task[Iterable[T]] = ZIO.foreach(jCollection.asScala.map(RedisDecoder[T].decode))(ZIO.fromTry(_))

  def decodeList[T: RedisDecoder](jCollection: java.util.List[String]): Task[List[T]] = ZIO
    .foreach(jCollection.asScala.map(RedisDecoder[T].decode).toList)(ZIO.fromTry(_))

  def decodeIterator[T: RedisDecoder](jCollection: java.util.Iterator[String]): Task[Iterator[T]] =
    ZIO
      .foreach(jCollection.asScala.map(RedisDecoder[T].decode).toSeq)(ZIO.fromTry(_))
      .map(_.iterator)

  def decodeSet[T: RedisDecoder](jCollection: java.util.Set[String]): Task[Set[T]] =
    ZIO.foreach(jCollection.asScala.map(RedisDecoder[T].decode).toSet)(ZIO.fromTry(_))

  def decodeListDouble(jList: java.util.List[java.lang.Double]): List[Double] = jList
    .asScala
    .map(_.doubleValue())
    .toList

  def decodeMapValue[T: RedisDecoder](jMap: java.util.Map[String, String]): Task[Map[String, T]] =
    ZIO.foreach(jMap.asScala.toMap) { case (k, v) =>
      ZIO.fromTry(RedisDecoder[T].decode(v)).map(k -> _)
    }

  def decodeMapScored[T: RedisDecoder](
    jMap: java.util.Map[String, java.lang.Double]
  ): Task[Map[T, Double]] = ZIO.foreach(jMap.asScala.toMap) { case (k, v) =>
    ZIO.fromTry(RedisDecoder[T].decode(k)).map(_ -> v.doubleValue())
  }

  def decodeNullableValue[T: RedisDecoder](nullableValue: String): Task[Option[T]] = ZIO
    .fromOption(Option(nullableValue))
    .foldZIO(
      _ => ZIO.succeed(Option.empty[T]),
      value => ZIO.fromTry(RedisDecoder[T].decode(value)).asSome
    )

  def decodeValue[T: RedisDecoder](value: String): Task[T] =
    ZIO.fromTry(RedisDecoder[T].decode(value))

  def decodeMapListValue[T: RedisDecoder, K](
    jMap: java.util.Map[K, java.util.List[String]]
  ): Task[Map[K, List[T]]] = ZIO.foreach(jMap.asScala.toMap) { case (key, jValue) =>
    decodeList(jValue).map(value => (key, value))
  }

  def decodeMapScoredValue[T: RedisDecoder, K](
    jMap: java.util.Map[K, java.util.Map[String, java.lang.Double]]
  ): Task[Map[K, Map[T, Double]]] = ZIO.foreach(jMap.asScala.toMap) { case (key, jValue) =>
    decodeMapScored(jValue).map(value => (key, value))
  }

}
