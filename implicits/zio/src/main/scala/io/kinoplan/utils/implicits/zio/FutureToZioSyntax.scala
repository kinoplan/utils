package io.kinoplan.utils.implicits.zio

import scala.concurrent.Future

import zio.{Task, ZIO}

final private[implicits] class FutureToZioOps[A](private val value: Future[A]) {

  def toZIO: Task[A] = ZIO.fromFuture(_ => value)

}

trait FutureToZioSyntax {

  implicit final def syntaxFutureToZioOps[A](value: Future[A]): FutureToZioOps[A] =
    new FutureToZioOps[A](value)

}

object FutureToZioSyntax extends FutureToZioSyntax
