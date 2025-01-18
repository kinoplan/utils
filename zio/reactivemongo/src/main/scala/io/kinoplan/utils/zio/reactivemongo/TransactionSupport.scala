package io.kinoplan.utils.zio.reactivemongo

import reactivemongo.api.DB
import zio.{Task, ZIO}

trait TransactionSupport {

  def transactionally[T](reactiveMongoApi: ReactiveMongoApi, failIfAlreadyStarted: Boolean = false)(
    requests: Task[DB] => Task[T]
  ): Task[T] = for {
    db <- reactiveMongoApi.database
    dbWithSession <- ZIO.fromFuture(implicit ec => db.startSession(failIfAlreadyStarted))
    dbWithTx <-
      ZIO.fromFuture(implicit ec => dbWithSession.startTransaction(None, failIfAlreadyStarted))
    result <- requests(ZIO.attempt(dbWithTx)).foldZIO(
      error =>
        ZIO
          .fromFuture(implicit ec =>
            for {
              _ <- dbWithTx.abortTransaction(failIfAlreadyStarted)
              _ <- dbWithSession.endSession(failIfAlreadyStarted)
            } yield ()
          )
          .zipRight(ZIO.fail(error)),
      result =>
        ZIO.fromFuture(implicit ec =>
          for {
            _ <- dbWithTx.commitTransaction(failIfAlreadyStarted)
            _ <- dbWithSession.endSession(failIfAlreadyStarted)
          } yield result
        )
    )
  } yield result

}

object TransactionSupport extends TransactionSupport
