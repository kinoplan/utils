package io.kinoplan.utils.wrappers.base.logging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

trait Loggable {
  implicit val logger: Logger = Logger(getClass.getName)

  implicit class TryOps[T](t: Try[T]) {

    def toOptionLogging(implicit
      logger: Logger
    ): Option[T] = {
      t match {
        case Failure(ex) => logger.error(s"Try to Option conversion, error lost: $ex")
        case _           => ()
      }

      t.toOption
    }

  }

  implicit class FutureOps[T](f: Future[T]) {

    def logError()(implicit
      logger: Logger,
      ec: ExecutionContext
    ): Future[T] = f.andThen { case Failure(ex) => logger.error(ex.toString) }

  }

}
