package io.kinoplan.utils.play.reactivemongo

import play.api.mvc.QueryStringBindable
import reactivemongo.api.bson._

/** Instances of
  * [[https://www.playframework.com/documentation/latest/api/scala/play/api/mvc/QueryStringBindable.html]]
  * for the ReactiveMongo types.
  */
trait QueryBindables extends QueryBindablesTypes {

  implicit object BSONBooleanQueryBindable
      extends QueryStringBindable.Parsing[BSONBoolean](
        parse => BSONBoolean(parse.toBoolean),
        serialize => serialize.value.toString,
        (key, e) => "Cannot parse parameter %s as BSONBoolean: %s".format(key, e.getMessage)
      )

  implicit object BSONDateTimeQueryBindable
      extends QueryStringBindable.Parsing[BSONDateTime](
        parse => BSONDateTime(parse.toLong),
        serialize => serialize.value.toString,
        (key, e) => "Cannot parse parameter %s as BSONDateTime: %s".format(key, e.getMessage)
      )

  implicit object BSONDoubleQueryBindable
      extends QueryStringBindable.Parsing[BSONDouble](
        parse => BSONDouble(parse.toDouble),
        serialize => serialize.value.toString,
        (key, e) => "Cannot parse parameter %s as BSONDouble: %s".format(key, e.getMessage)
      )

  implicit object BSONLongQueryBindable
      extends QueryStringBindable.Parsing[BSONLong](
        parse => BSONLong(parse.toLong),
        serialize => serialize.value.toString,
        (key, e) => "Cannot parse parameter %s as BSONLong: %s".format(key, e.getMessage)
      )

  implicit object BSONStringQueryBindable
      extends QueryStringBindable.Parsing[BSONString](
        parse => BSONString(parse),
        serialize => serialize.value,
        (key, e) => "Cannot parse parameter %s as BSONString: %s".format(key, e.getMessage)
      )

  implicit object BSONSymbolQueryBindable
      extends QueryStringBindable.Parsing[BSONSymbol](
        parse => BSONSymbol(parse),
        serialize => serialize.value,
        (key, e) => "Cannot parse parameter %s as BSONSymbol: %s".format(key, e.getMessage)
      )

  implicit object BSONTimestampQueryBindable
      extends QueryStringBindable.Parsing[BSONTimestamp](
        parse => BSONTimestamp(parse.toLong),
        serialize => serialize.value.toString,
        (key, e) => "Cannot parse parameter %s as BSONTimestamp: %s".format(key, e.getMessage)
      )

  implicit object BSONObjectIDQueryBindable
      extends QueryStringBindable.Parsing[BSONObjectID](
        parse => BSONObjectID.parse(parse).get,
        serialize => serialize.stringify,
        (key, e) => "Cannot parse parameter %s as BSONObjectID: %s".format(key, e.getMessage)
      )

}

object QueryBindables extends QueryBindables
