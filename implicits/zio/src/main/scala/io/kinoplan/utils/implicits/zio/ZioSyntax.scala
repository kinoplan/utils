package io.kinoplan.utils.implicits.zio

import zio.{CancelableFuture, Unsafe, ZIO}

final private[implicits] class ZioOps[E <: Throwable, A](private val value: ZIO[Any, E, A]) {

  def runToFuture: CancelableFuture[A] = Unsafe.unsafe { implicit unsafe =>
    zio.Runtime.default.unsafe.runToFuture[E, A](value)
  }

}

trait ZioSyntax {

  implicit final def syntaxZioOps[E <: Throwable, A](value: ZIO[Any, E, A]): ZioOps[E, A] =
    new ZioOps[E, A](value)

}

object ZioSyntax extends ZioSyntax
