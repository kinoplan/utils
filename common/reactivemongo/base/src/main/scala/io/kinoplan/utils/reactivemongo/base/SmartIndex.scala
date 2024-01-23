package io.kinoplan.utils.reactivemongo.base

import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.indexes.IndexType

case class SmartIndex(
  key: Set[(String, IndexType)],
  name: Option[String] = None,
  unique: Boolean = false,
  background: Boolean = true,
  partialFilter: Option[BSONDocument] = None,
  expireAfterSeconds: Option[Int] = None
)
