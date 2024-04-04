package io.kinoplan.utils.play.reactivemongo.metrics

import scala.concurrent.{ExecutionContext, Future}

import kamon.Kamon
import reactivemongo.api.bson.collection.BSONCollection

object ReactiveMongoMetrics {

  private val PREFIX = "reactive_mongo"

  val queryTimer = Kamon.timer(
    s"${PREFIX}_query_duration_seconds",
    "Tracks the time elapsed between sending a request and receiving a response per type and db.collection."
  )

  val receivedDocumentsCounter = Kamon.counter(
    s"${PREFIX}_received_documents",
    "Number of received documents per type and db.collection."
  )

  object wrapper {

    def findQueryTimerWrapper[T](collection: BSONCollection)(f: => Future[T])(implicit
      ec: ExecutionContext
    ): Future[T] = queryTimerWrapper(collection, "find")(f)

    def insertQueryTimerWrapper[T](collection: BSONCollection)(f: => Future[T])(implicit
      ec: ExecutionContext
    ): Future[T] = queryTimerWrapper(collection, "insert")(f)

    def updateQueryTimerWrapper[T](collection: BSONCollection)(f: => Future[T])(implicit
      ec: ExecutionContext
    ): Future[T] = queryTimerWrapper(collection, "update")(f)

    def deleteQueryTimerWrapper[T](collection: BSONCollection)(f: => Future[T])(implicit
      ec: ExecutionContext
    ): Future[T] = queryTimerWrapper(collection, "delete")(f)

    def commandQueryTimerWrapper[T](collection: BSONCollection)(f: => Future[T])(implicit
      ec: ExecutionContext
    ): Future[T] = queryTimerWrapper(collection, "command")(f)

    def findReceivedDocumentsCounterWrapper[T](collection: BSONCollection)(f: => Future[T])(
      count: T => Long
    )(implicit
      ec: ExecutionContext
    ): Future[T] = receivedDocumentsCounterWrapper(collection, "find")(f)(count)

    def commandReceivedDocumentsCounterWrapper[T](collection: BSONCollection)(f: => Future[T])(
      count: T => Long
    )(implicit
      ec: ExecutionContext
    ): Future[T] = receivedDocumentsCounterWrapper(collection, "command")(f)(count)

    private[reactivemongo] def receivedDocumentsCounterWrapper[T](
      collection: BSONCollection,
      typeName: String
    )(f: => Future[T])(count: T => Long)(implicit
      ec: ExecutionContext
    ): Future[T] = {
      val counter = receivedDocumentsCounter
        .withTag("collection", collection.name)
        .withTag("db", collection.db.name)
        .withTag("type", typeName)

      f.map { res =>
        counter.increment(count(res))
        res
      }
    }

    private[reactivemongo] def queryTimerWrapper[T](collection: BSONCollection, typeName: String)(
      f: => Future[T]
    )(implicit
      ec: ExecutionContext
    ): Future[T] = {
      val timer = queryTimer
        .withTag("collection", collection.name)
        .withTag("db", collection.db.name)
        .withTag("type", typeName)
        .start()
      f.map { res =>
        timer.stop()
        res
      }
    }

  }

}
