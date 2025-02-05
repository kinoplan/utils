package io.kinoplan.utils.reactivemongo.base

import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.indexes.{Index, IndexType}

case class SmartIndex(
  key: Seq[(String, IndexType)],
  name: Option[String] = None,
  unique: Boolean = false,
  background: Boolean = true,
  partialFilter: Option[BSONDocument] = None,
  expireAfterSeconds: Option[Int] = None
) {

  def toIndex = Index(
    key = key,
    name = name,
    unique = unique,
    background = background,
    partialFilter = partialFilter,
    expireAfterSeconds = expireAfterSeconds
  )

}
