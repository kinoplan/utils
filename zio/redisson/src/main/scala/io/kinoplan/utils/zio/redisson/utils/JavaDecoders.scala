package io.kinoplan.utils.zio.redisson.utils

import io.kinoplan.utils.redisson.base.codec.RedisDecoder
import io.kinoplan.utils.redisson.crossCollectionConverters._
import zio.{Task, ZIO}

private[redisson] object JavaDecoders {

  def decodeCollection[T: RedisDecoder](jCollection: java.util.Collection[String]): Task[List[T]] =
    ZIO.foreach(jCollection.asScala.map(RedisDecoder[T].decode).toList)(ZIO.fromTry(_))

  def decodeIterator[T: RedisDecoder](jCollection: java.util.Iterator[String]): Task[Iterator[T]] =
    ZIO
      .foreach(jCollection.asScala.map(RedisDecoder[T].decode).toList)(ZIO.fromTry(_))
      .map(_.iterator)

  def decodeSet[T: RedisDecoder](jCollection: java.util.Set[String]): Task[Set[T]] = ZIO
    .foreach(jCollection.asScala.map(RedisDecoder[T].decode).toSet)(ZIO.fromTry(_))

  def decodeNullableValue[T: RedisDecoder](nullableValue: String): Task[Option[T]] = ZIO
    .fromOption(Option(nullableValue))
    .foldZIO(
      _ => ZIO.succeed(Option.empty[T]),
      value => ZIO.fromTry(RedisDecoder[T].decode(value)).asSome
    )

  def decodeValue[T: RedisDecoder](value: String): Task[T] = ZIO
    .fromTry(RedisDecoder[T].decode(value))

  def decodeMapListValue[T: RedisDecoder, K](
    jMap: java.util.Map[K, java.util.List[String]]
  ): Task[Map[K, List[T]]] = ZIO.foreach(jMap.asScala.toMap) { case (key, jValue) =>
    decodeCollection(jValue).map(value => (key, value))
  }

}
