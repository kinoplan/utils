package io.kinoplan.utils.play.reactivemongo.metrics

import scala.concurrent.{ExecutionContext, Future}

import kamon.Kamon
import kamon.tag.TagSet
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

  object Type {
    val FIND = "find"
    val INSERT = "insert"
    val UPDATE = "update"
    val DELETE = "delete"
    val COMMAND = "command"
  }

  object wrapper {

    def findQueryTimerWrapper[T](collection: BSONCollection)(f: => Future[T])(implicit
      ec: ExecutionContext
    ): Future[T] = queryTimerWrapper(collection, Type.FIND)(f)

    def insertQueryTimerWrapper[T](collection: BSONCollection)(f: => Future[T])(implicit
      ec: ExecutionContext
    ): Future[T] = queryTimerWrapper(collection, Type.INSERT)(f)

    def updateQueryTimerWrapper[T](collection: BSONCollection)(f: => Future[T])(implicit
      ec: ExecutionContext
    ): Future[T] = queryTimerWrapper(collection, Type.UPDATE)(f)

    def deleteQueryTimerWrapper[T](collection: BSONCollection)(f: => Future[T])(implicit
      ec: ExecutionContext
    ): Future[T] = queryTimerWrapper(collection, Type.DELETE)(f)

    def commandQueryTimerWrapper[T](collection: BSONCollection)(f: => Future[T])(implicit
      ec: ExecutionContext
    ): Future[T] = queryTimerWrapper(collection, Type.COMMAND)(f)

    def findReceivedDocumentsCounterWrapper[T](collection: BSONCollection)(f: => Future[T])(
      count: T => Long
    )(implicit
      ec: ExecutionContext
    ): Future[T] = receivedDocumentsCounterWrapper(collection, Type.FIND)(f)(count)

    def commandReceivedDocumentsCounterWrapper[T](collection: BSONCollection)(f: => Future[T])(
      count: T => Long
    )(implicit
      ec: ExecutionContext
    ): Future[T] = receivedDocumentsCounterWrapper(collection, Type.COMMAND)(f)(count)

    private[reactivemongo] def receivedDocumentsCounterWrapper[T](
      collection: BSONCollection,
      typeName: String
    )(f: => Future[T])(count: T => Long)(implicit
      ec: ExecutionContext
    ): Future[T] = f.map { res =>
      receivedDocumentsCounter
        .withTags(
          TagSet.from(
            Map("collection" -> collection.name, "db" -> collection.db.name, "type" -> typeName)
          )
        )
        .increment(count(res))
      res
    }

    private[reactivemongo] def queryTimerWrapper[T](collection: BSONCollection, typeName: String)(
      f: => Future[T]
    )(implicit
      ec: ExecutionContext
    ): Future[T] = {
      val timer = queryTimer
        .withTags(
          TagSet.from(
            Map("collection" -> collection.name, "db" -> collection.db.name, "type" -> typeName)
          )
        )
        .start()
      f.map { res =>
        timer.stop()
        res
      }
    }

  }

}
