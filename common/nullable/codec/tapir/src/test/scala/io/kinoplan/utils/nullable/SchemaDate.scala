package io.kinoplan.utils.nullable

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import scala.util.{Failure, Success, Try}

import sttp.tapir.{Codec, DecodeResult, Schema}
import sttp.tapir.CodecFormat.TextPlain

class SchemaDate private (dateTime: OffsetDateTime) {
  override def toString: String = DateTimeFormatter.ofPattern(SchemaDate.format).format(dateTime)
  def value: OffsetDateTime = dateTime
}

trait SchemaDateTapir {

  def decode(s: String): DecodeResult[SchemaDate] = SchemaDate.parse(s) match {
    case Success(v) => DecodeResult.Value(v)
    case Failure(f) => DecodeResult.Error(s, f)
  }

  def encode(entity: SchemaDate): String = entity.toString

  implicit val codec: Codec[String, SchemaDate, TextPlain] = Codec.string.mapDecode(decode)(encode)

  implicit val schema: Schema[SchemaDate] = Schema.string.format(SchemaDate.format)

}

object SchemaDate extends SchemaDateTapir {
  final val format = "dd.MM.yyyy"

  def apply(dateTime: OffsetDateTime): SchemaDate = new SchemaDate(dateTime)

  def parse(value: String): Try[SchemaDate] = Try(OffsetDateTime.parse(value)).map(SchemaDate(_))

}
