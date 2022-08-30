package io.kinoplan.utils.reactivemongo.base

import reactivemongo.api.indexes.IndexType

case class SmartIndex(
  key: Set[(String, IndexType)],
  unique: Boolean = false,
  background: Boolean = true
)
