package io.kinoplan.utils.redisson.core

import scala.concurrent.{ExecutionContext, Future}

import org.redisson.api.StreamMessageId

import io.kinoplan.utils.redisson.core.codec.RedisDecoder
import io.kinoplan.utils.redisson.crossCollectionConverters._

object JavaDecoders {

  def decodeNullableValue[T: RedisDecoder](nullableValue: String)(implicit
    executionContext: ExecutionContext
  ): Future[Option[T]] = Future
    .traverse(Option(nullableValue).map(RedisDecoder[T].decode).toSeq)(Future.fromTry)
    .map(_.headOption)

  def decodeArray[T: RedisDecoder](jArray: java.util.Collection[String])(implicit
    executionContext: ExecutionContext
  ): Future[List[T]] = Future
    .traverse(jArray.asScala.map(RedisDecoder[T].decode).toList)(Future.fromTry)

  def decodeSet[T: RedisDecoder](jSet: java.util.Collection[String])(implicit
    executionContext: ExecutionContext
  ): Future[Set[T]] = Future.traverse(jSet.asScala.map(RedisDecoder[T].decode).toSet)(Future.fromTry)

  def decodeMap[T: RedisDecoder](jMap: java.util.Map[String, String])(implicit
    executionContext: ExecutionContext
  ): Future[Map[String, T]] = Future
    .traverse(jMap.asScala.toList) { case (objectKey, objectValue) =>
      Future.fromTry(RedisDecoder[T].decode(objectValue)).map(objectKey -> _)
    }
    .map(_.toMap)

  def decodeStreamEntries[T: RedisDecoder](
    entries: java.util.Map[StreamMessageId, java.util.Map[String, String]]
  )(implicit
    executionContext: ExecutionContext
  ): Future[Map[StreamMessageId, Map[String, T]]] = Future
    .traverse(entries.asScala.toList) { case (id, objects) =>
      Future
        .traverse(objects.asScala.toList) { case (objectKey, objectValue) =>
          Future.fromTry(RedisDecoder[T].decode(objectValue)).map(objectKey -> _)
        }
        .map(id -> _.toMap)
    }
    .map(_.toMap)

}
