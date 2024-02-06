package io.kinoplan.utils.reactivemongo.bson

sealed abstract class HandlerStrategy(val expectedName: String)

object HandlerStrategy {
  case object BSONDateTimeFormat extends HandlerStrategy("BSONDateTime")
  case object BSONStringFormat extends HandlerStrategy("BSONString")
}
