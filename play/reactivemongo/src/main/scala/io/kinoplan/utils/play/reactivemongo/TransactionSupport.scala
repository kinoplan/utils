package io.kinoplan.utils.play.reactivemongo

import scala.concurrent.{ExecutionContext, Future}

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.DB

trait TransactionSupport {

  def transactionally[T](reactiveMongoApi: ReactiveMongoApi)(requests: DB => Future[T])(implicit
    ec: ExecutionContext
  ): Future[T] = for {
    db <- reactiveMongoApi.database
    dbWithSession <- db.startSession()
    dbWithTx <- dbWithSession.startTransaction(None)
    result <- requests(dbWithTx)
      .flatMap(result =>
        for {
          _ <- dbWithTx.commitTransaction()
          _ <- dbWithSession.endSession()
        } yield result
      )
      .recoverWith { case error: Throwable =>
        (
          for {
            _ <- dbWithTx.abortTransaction()
            _ <- dbWithSession.endSession()
          } yield ()
        ).flatMap(_ => Future.failed(error))
      }
  } yield result

}

object TransactionSupport extends TransactionSupport
