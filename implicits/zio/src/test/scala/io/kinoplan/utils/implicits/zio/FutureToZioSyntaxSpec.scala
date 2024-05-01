package io.kinoplan.utils.implicits.zio

import scala.concurrent.Future

import zio.Task
import zio.test._
import zio.test.Assertion._

import io.kinoplan.utils.implicits.zio.FutureToZioSyntax.syntaxFutureToZioOps

object FutureToZioSyntaxSpec extends ZIOSpecDefault {

  def spec: Spec[Any, Throwable] = suite("FutureToZioSyntaxSpec")(
    test("toZIO from Future.successful") {
      val future: Future[Int] = Future.successful(42)
      val zio: Task[Int] = future.toZIO

      assertZIO(zio)(equalTo(42))
    },
    test("toZIO from Future.failed") {
      val future: Future[Int] = Future.failed(new RuntimeException("test error"))
      val zio: Task[Int] = future.toZIO

      assertZIO(zio.exit)(fails(anything))
    }
  )

}
