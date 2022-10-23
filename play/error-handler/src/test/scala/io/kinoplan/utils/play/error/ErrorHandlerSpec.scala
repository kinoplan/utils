package io.kinoplan.utils.play.error

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

import akka.stream.Materializer

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.scalatest.Assertion
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import org.slf4j.LoggerFactory
import play.api.{Configuration, Environment}
import play.api.mvc.{AnyContentAsEmpty, Result, Results}
import play.api.test.FakeRequest

import io.kinoplan.utils.play.filters.logging.kit.ListAppenderSyntax
import io.kinoplan.utils.play.request.RequestMapContext

class ErrorHandlerSpec
    extends AnyWordSpecLike
      with Matchers
      with MockitoSugar
      with ScalaFutures
      with GuiceOneAppPerTest
      with ListAppenderSyntax
      with RequestMapContext {

  implicit lazy val mat: Materializer = fakeApplication().materializer
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val throwable = new Throwable("Test")

  private class Setup(configName: String) extends Results {

    System.setProperty("config.resource", configName)

    private val configuration: Configuration = Configuration.load(Environment.simple())

    val errorHandler = new ErrorHandler(configuration)

    private val logbackLogger: Logger = LoggerFactory
      .getLogger(errorHandler.getClass.getName)
      .asInstanceOf[Logger]

    private val listAppender = new ListAppender[ILoggingEvent]

    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/?test=test")

    def getResultOnServerError: Result = {
      listAppender.start()

      logbackLogger.addAppender(listAppender)

      val result = errorHandler.onServerError(request, throwable).futureValue

      listAppender.stop()

      result
    }

    def getLogMessages: Seq[String] = listAppender.getLogMessages

    def getLogMdcProperties: Map[String, String] = listAppender.getLogMdcProperties

    def commonAssert(
      result: Result,
      logMessages: Seq[String],
      logMdcProperties: Map[String, String]
    ): Assertion = {
      assert(logMessages.exists(_.contains("Internal server error, for (GET) [/?test=test]")))
      assert(logMdcProperties.get("request_method").contains("GET"))
      assert(logMdcProperties.get("request_path").contains("/"))
      assert(logMdcProperties.get("request_host").contains("localhost"))
      assert(logMdcProperties.get("request_remote_address").contains("127.0.0.1"))
      assert(logMdcProperties.get("request_real_ip").contains("127.0.0.1"))
      assert(logMdcProperties.get("response_status").contains(result.header.status.toString))
      assert(logMdcProperties.get("request_param_test").contains("test"))
      assert(logMdcProperties.contains("response_length"))
      assert(logMdcProperties.contains("request_id"))
      assert(logMdcProperties.contains("request_internal_id"))
    }

  }

  "error handler by application-test-dev.conf" should {
    val configName = "application-test-dev.conf"

    "correct for onClientError" in
      new Setup(configName) {
        val statusCode = 403
        val body = "Test"
        val result: Result = errorHandler.onClientError(request, statusCode, body).futureValue

        assert(result === Forbidden(body))
      }

    "correct for onServerError" in
      new Setup(configName) {
        val result: Result = getResultOnServerError
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(result.header.status === InternalServerError.header.status)
        assert(
          result.body.consumeData.map(_.decodeString("UTF-8")).futureValue ===
            "A server error occurred: Test"
        )
      }
  }

  "error handler by application-test-prod.conf" should {
    val configName = "application-test-prod.conf"

    "correct for onClientError" in
      new Setup(configName) {
        val statusCode = 403
        val body = "Test"
        val result: Result = errorHandler.onClientError(request, statusCode, body).futureValue

        assert(result === Forbidden(body))
      }

    "correct for onServerError" in
      new Setup(configName) {
        val result: Result = getResultOnServerError
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(result.header.status === InternalServerError.header.status)
        assert(result.body.consumeData.map(_.decodeString("UTF-8")).futureValue === "")
      }
  }

}
