package io.kinoplan.utils.zio.reactivemongo.metrics

import java.time.temporal.ChronoUnit

import reactivemongo.api.bson.collection.BSONCollection
import zio.{Chunk, ZIOAspect}
import zio.metrics.{Metric, MetricKeyType, MetricLabel, MetricState}
import zio.metrics.Metric.Counter

import io.kinoplan.utils.zio.reactivemongo.metrics.ReactiveMongoLabels._

object ReactiveMongoMetric {

  private val PREFIX = "reactive_mongo"

  val queryTimer: Metric[MetricKeyType.Histogram, zio.Duration, MetricState.Histogram] = Metric
    .timer(
      s"${PREFIX}_query_duration_seconds",
      "Tracks the time elapsed between sending a request and receiving a response per type and db.collection.",
      ChronoUnit.SECONDS,
      Chunk(0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 0.75, 1.0, 2.5, 5.0, 7.5, 10.0)
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
