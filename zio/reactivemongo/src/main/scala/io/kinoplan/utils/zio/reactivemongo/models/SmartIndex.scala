package io.kinoplan.utils.zio.reactivemongo.models

import reactivemongo.api.indexes.IndexType

case class SmartIndex(
  key: Set[(String, IndexType)],
  unique: Boolean = false,
  background: Boolean = true
)
