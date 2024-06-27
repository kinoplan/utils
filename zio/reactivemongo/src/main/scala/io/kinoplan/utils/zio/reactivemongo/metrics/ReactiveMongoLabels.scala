package io.kinoplan.utils.zio.reactivemongo.metrics

import reactivemongo.api.bson.collection.BSONCollection
import zio.metrics.MetricLabel

object ReactiveMongoLabels {

  def collectionLabels(collection: BSONCollection): Set[MetricLabel] =
    Set(MetricLabel("collection", collection.name), MetricLabel("db", collection.db.name))

  private def typeLabel(typeName: String): MetricLabel = MetricLabel("type", typeName)

  val findLabel: MetricLabel = typeLabel("find")
  val insertLabel: MetricLabel = typeLabel("insert")
  val updateLabel: MetricLabel = typeLabel("update")
  val deleteLabel: MetricLabel = typeLabel("delete")
  val commandLabel: MetricLabel = typeLabel("command")

}
