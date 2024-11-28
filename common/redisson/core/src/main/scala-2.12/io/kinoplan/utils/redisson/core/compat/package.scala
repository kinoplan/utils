package io.kinoplan.utils.redisson.core

import java.util.concurrent.CompletionStage

import scala.concurrent.Future

package object compat {
  private[utils] val crossFutureConverters = FutureConverters

  private[utils] object FutureConverters {

    implicit class FutureOps[T](private val f: Future[T]) extends AnyVal {
      def asJava: CompletionStage[T] = scala.compat.java8.FutureConverters.toJava(f)
    }

    implicit class CompletionStageOps[T](private val cs: CompletionStage[T]) extends AnyVal {
      def asScala: Future[T] = scala.compat.java8.FutureConverters.toScala(cs)
    }

  }

}
