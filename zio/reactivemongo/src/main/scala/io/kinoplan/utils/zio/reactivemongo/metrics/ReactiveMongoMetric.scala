package io.kinoplan.utils.zio.reactivemongo.metrics

import java.time.temporal.ChronoUnit

import reactivemongo.api.bson.collection.BSONCollection
import zio.ZIOAspect
import zio.metrics.{Metric, MetricKeyType, MetricLabel, MetricState}
import zio.metrics.Metric.Counter

import io.kinoplan.utils.zio.reactivemongo.metrics.ReactiveMongoLabels._

object ReactiveMongoMetric {

  private val PREFIX = "reactive_mongo"

  val queryTimer: Metric[MetricKeyType.Histogram, zio.Duration, MetricState.Histogram] = Metric
    .timer(
      s"${PREFIX}_query_duration_seconds",
      "Tracks the time elapsed between sending a request and receiving a response per type and db.collection.",
      ChronoUnit.SECONDS
    )

  val queriesCounter: Counter[Long] = Metric
    .counter(s"${PREFIX}_queries", "Number of completed queries per type and db.collection.")

  val receivedDocumentsCounter: Counter[Long] = Metric.counter(
    s"${PREFIX}_received_documents",
    "Number of received documents per type and db.collection."
  )

  object aspect {

    def commandQueryTimer(
      collection: BSONCollection
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
      mkQueryTimerAspect(collection, commandLabel)

    def commandQueriesCounter(
      collection: BSONCollection
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
      mkQueriesCounterAspect(collection, commandLabel)

    def commandReceivedDocumentsCounter[In](
      collection: BSONCollection
    )(f: In => Long): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, In] =
      mkReceivedDocumentsCounterAspect[In](collection, commandLabel)(f)

    def findQueryTimer(
      collection: BSONCollection
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
      mkQueryTimerAspect(collection, findLabel)

    def findQueriesCounter(
      collection: BSONCollection
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
      mkQueriesCounterAspect(collection, findLabel)

    def findReceivedDocumentsCounter[In](
      collection: BSONCollection
    )(f: In => Long): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, In] =
      mkReceivedDocumentsCounterAspect[In](collection, findLabel)(f)

    def insertQueryTimer(
      collection: BSONCollection
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
      mkQueryTimerAspect(collection, insertLabel)

    def insertQueriesCounter(
      collection: BSONCollection
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
      mkQueriesCounterAspect(collection, insertLabel)

    def updateQueryTimer(
      collection: BSONCollection
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
      mkQueryTimerAspect(collection, updateLabel)

    def updateQueriesCounter(
      collection: BSONCollection
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
      mkQueriesCounterAspect(collection, updateLabel)

    def deleteQueryTimer(
      collection: BSONCollection
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
      mkQueryTimerAspect(collection, deleteLabel)

    def deleteQueriesCounter(
      collection: BSONCollection
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] =
      mkQueriesCounterAspect(collection, deleteLabel)

    private def mkQueryTimerAspect(
      collection: BSONCollection,
      typeLabel: MetricLabel
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] = queryTimer
      .tagged(collectionLabels(collection) + typeLabel)
      .trackDuration

    private def mkQueriesCounterAspect(
      collection: BSONCollection,
      typeLabel: MetricLabel
    ): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, Any] = queriesCounter
      .tagged(collectionLabels(collection) + typeLabel)
      .trackSuccessWith[Any](_ => 1L)

    private def mkReceivedDocumentsCounterAspect[In](
      collection: BSONCollection,
      typeLabel: MetricLabel
    )(f: In => Long): ZIOAspect[Nothing, Any, Nothing, Any, Nothing, In] = receivedDocumentsCounter
      .tagged(collectionLabels(collection) + typeLabel)
      .trackSuccessWith[In](f)

  }

}
