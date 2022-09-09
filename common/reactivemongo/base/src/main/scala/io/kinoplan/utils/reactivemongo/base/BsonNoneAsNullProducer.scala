package io.kinoplan.utils.reactivemongo.base

import reactivemongo.api.bson.{BSONElement, BSONNull, BSONWriter, ElementProducer}

trait BsonNoneAsNullProducer {

  implicit def noneAsNullProducer[T](element: (String, Option[T]))(implicit
    writer: BSONWriter[T]
  ): ElementProducer = {
    val (key, valueO) = element

    valueO.flatMap(writer.writeOpt).fold(BSONElement(key, BSONNull))(BSONElement(key, _))
  }

}

object BsonNoneAsNullProducer extends BsonNoneAsNullProducer
