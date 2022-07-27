package io.kinoplan.utils.reactivemongo.bson.any

import scala.util.{Failure, Success, Try}

import reactivemongo.api.bson.{BSONDouble, BSONHandler, BSONInteger, BSONLong, BSONString, BSONValue}

trait BsonAnyHandlers {

  implicit object BSONAnyHandler extends BSONHandler[Any] {

    override def writeTry(t: Any): Try[BSONValue] = t match {
      case i: Int    => Success(BSONInteger(i))
      case l: Long   => Success(BSONLong(l))
      case d: Double => Success(BSONDouble(d))
      case s: String => Success(BSONString(s))
      case _         => Failure(new IllegalArgumentException(s"unexpected value $t"))

    }

    override def readTry(bson: BSONValue): Try[Any] = bson match {
      case BSONInteger(i) => Success(i)
      case BSONLong(l)    => Success(l)
      case BSONDouble(d)  => Success(d)
      case BSONString(s)  => Success(s)
      case _              => Failure(new IllegalArgumentException(s"unexpected value $bson"))
    }

  }

}
