package io.kinoplan.utils.zio.reactivemongo.metrics

import reactivemongo.api.bson.collection.BSONCollection
import zio.metrics.{Metric, MetricLabel}
import zio.metrics.Metric.Counter

object ReactiveMongoMetric {

  private val PREFIX = "reactive_mongo"

  val findCounter: Counter[Long] = Metric
    .counter(s"${PREFIX}_find", "Number of completed find queries per db.collection")

  val findReceivedDocumentsCounter: Counter[Long] = Metric.counter(
    s"${PREFIX}_find_response_documents",
    "Number of total received parsed documents made by ReactiveMongo driver"
  )

  val insertCounter: Counter[Long] = Metric
    .counter(s"${PREFIX}_insert", "Number of completed insert queries per db.collection")

  val updateCounter: Counter[Long] = Metric
    .counter(s"${PREFIX}_update", "Number of completed update queries per db.collection")

  val removeCounter: Counter[Long] = Metric
    .counter(s"${PREFIX}_remove", "Number of completed remove queries per db.collection")

  val commandsCounter: Counter[Long] = Metric
    .counter(s"${PREFIX}_command", "Number of completed commands per db.collection")

  def collectionLabels(collection: BSONCollection): Set[MetricLabel] =
    Set(MetricLabel("collection", collection.name), MetricLabel("db", collection.db.name))

}
