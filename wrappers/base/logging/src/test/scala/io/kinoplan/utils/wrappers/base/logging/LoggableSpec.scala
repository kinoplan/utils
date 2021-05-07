package io.kinoplan.utils.wrappers.base.logging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

import kit.data.TestKitConstants
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class LoggableSpec extends AnyWordSpec with Matchers with Loggable with TestKitConstants {
  "TryOps#toOptionLogging" should {
    "call Try with success result" in assert(Try(zero / 1).toOptionLogging.contains(0))
    "call Try with failure result" in assert(Try(1 / zero).toOptionLogging.isEmpty)
  }

  "FutureOps#logError" should {
    "call Future with success result" in {
      val f: Future[Int] = Future.successful(1).logError()

      ScalaFutures.whenReady(f)(_ shouldBe 1)
    }
    "call Future with failure result" in {
      val f: Future[Int] = Future(throw new NullPointerException).logError()

      ScalaFutures.whenReady(f.failed)(_ shouldBe a[NullPointerException])
    }
  }
}
