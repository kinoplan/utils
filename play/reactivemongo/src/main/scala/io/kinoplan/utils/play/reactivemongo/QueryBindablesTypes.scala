package io.kinoplan.utils.play.reactivemongo

trait QueryBindablesTypes {
  type BSONBoolean = reactivemongo.api.bson.BSONBoolean
  type BSONDateTime = reactivemongo.api.bson.BSONDateTime
  type BSONDouble = reactivemongo.api.bson.BSONDouble
  type BSONLong = reactivemongo.api.bson.BSONLong
  type BSONObjectID = reactivemongo.api.bson.BSONObjectID
  type BSONString = reactivemongo.api.bson.BSONString
  type BSONSymbol = reactivemongo.api.bson.BSONSymbol
  type BSONTimestamp = reactivemongo.api.bson.BSONTimestamp
}
