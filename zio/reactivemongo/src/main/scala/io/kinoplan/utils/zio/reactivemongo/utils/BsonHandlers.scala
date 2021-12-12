package io.kinoplan.utils.zio.reactivemongo.utils

import reactivemongo.api.bson.{BSONNull, BSONValue, BSONWriter}

trait BsonHandlers {

  def noneAsNull[A](option: Option[A])(implicit
    writer: BSONWriter[A]
  ): BSONValue = option.flatMap(writer.writeOpt).getOrElse(BSONNull)

}
