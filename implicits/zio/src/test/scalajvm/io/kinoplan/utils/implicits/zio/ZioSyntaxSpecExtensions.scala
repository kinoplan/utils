package io.kinoplan.utils.implicits.zio

import scala.concurrent.Future

import zio.ZIO

import io.kinoplan.utils.implicits.zio.ZioSyntax.syntaxZioOps

trait ZioSyntaxSpecExtensions {
  self: ZioSyntaxSpec =>

  "ZioSyntax#runToFuture" should "return success value" in {
    val zio = ZIO.succeed(42)
    val future: Future[Int] = zio.runToFuture

    future.map(_ shouldBe 42)
  }

  "ZioSyntax#runToFuture" should "return failed value" in {
    val zio: ZIO[Any, Throwable, Nothing] = ZIO.fail(new RuntimeException("test error"))
    val future: Future[Nothing] = zio.runToFuture

    recoverToExceptionIf[RuntimeException](future).map(_.getMessage shouldBe "test error")
  }

}
