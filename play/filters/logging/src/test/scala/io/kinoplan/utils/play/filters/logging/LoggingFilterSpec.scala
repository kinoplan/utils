package io.kinoplan.utils.play.filters.logging

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.xml.Elem

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import org.mockito.Mockito._
import org.scalatest.Assertion
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import org.slf4j.LoggerFactory
import play.api.{Configuration, Environment}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import play.api.test._

import io.kinoplan.utils.play.compat.Materializer
import io.kinoplan.utils.play.filters.logging.kit.ListAppenderSyntax
import io.kinoplan.utils.play.request.RequestMapContext
import io.kinoplan.utils.scala.logging.context.MapContext

class LoggingFilterSpec
    extends AnyWordSpecLike
      with Matchers
      with MockitoSugar
      with ScalaFutures
      with GuiceOneAppPerTest
      with ListAppenderSyntax
      with RequestMapContext {

  implicit lazy val mat: Materializer = fakeApplication().materializer
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val jsonBody: JsObject = Json.obj("test" -> "test")
  val textBody = "test"
  val xmlBody: Elem = <message>Hello World!</message>
  val otherBody: Elem = <h1>Hello World!</h1>

  private class Setup(configName: String) extends Results {

    System.setProperty("config.resource", configName)

    private val configuration: Configuration = Configuration.load(Environment.simple())

    private val loggingFilter = new LoggingFilter(configuration)(mat, ec)

    private val logbackLogger: Logger = LoggerFactory
      .getLogger(loggingFilter.getClass.getName)
      .asInstanceOf[Logger]

    private val listAppender = new ListAppender[ILoggingEvent]
    listAppender.start()

    def action(
      request: FakeRequest[AnyContentAsEmpty.type],
      result: Result,
      headers: (String, String)*
    ): RequestHeader => Future[Result] = {
      val mockAction = mock[RequestHeader => Future[Result]]
      val outgoingResponse = Future.successful(result.withHeaders(headers: _*))

      when(mockAction.apply(request)).thenReturn(outgoingResponse)

      mockAction
    }

    def getResult(responseResult: Result, headers: (String, String)*): Result = {
      listAppender.start()
      logbackLogger.addAppender(listAppender)

      val request = FakeRequest("GET", "/?test=test")

      implicit val mapContext: MapContext = request.putMapContext("test" -> "test")

      loggingFilter.logger.info("Test putMapContext")

      assert(mapContext.get("test").contains("test"))

      val result = loggingFilter(action(request, responseResult, headers: _*))(request).futureValue

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
      assert(logMessages.contains("[START] GET /?test=test"))
      assert(logMessages.exists(_.contains("[END] GET /?test=test took")))
      assert(logMessages.exists(_.contains(s"and returned ${result.header.status}")))
      assert(logMdcProperties.get("request_method").contains("GET"))
      assert(logMdcProperties.get("request_path").contains("/"))
      assert(logMdcProperties.get("request_host").contains("localhost"))
      assert(logMdcProperties.get("request_remote_address").contains("127.0.0.1"))
      assert(logMdcProperties.get("request_real_ip").contains("127.0.0.1"))
      assert(logMdcProperties.get("response_status").contains(result.header.status.toString))
      assert(logMdcProperties.get("request_param_test").contains("test"))
      assert(logMdcProperties.get("test").contains("test"))
      assert(logMdcProperties.contains("response_length"))
      assert(logMdcProperties.contains("response_time"))
      assert(logMdcProperties.contains("request_id"))
      assert(logMdcProperties.contains("request_internal_id"))
    }

  }

  "filter by application-test-simple.conf" should {
    val configName = "application-test-simple.conf"

    "correct for isSuccessful" in
      new Setup(configName) {
        val result: Result = getResult(Ok)
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
      }

    "correct for clientErrors with response no body" in
      new Setup(configName) {
        val result: Result = getResult(BadRequest)
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains("No content type")))
      }

    "correct for clientErrors with response json body" in
      new Setup(configName) {
        val body: JsObject = jsonBody
        val result: Result = getResult(BadRequest(body))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains(s"Response: $body")))
      }

    "correct for clientErrors with response text body" in
      new Setup(configName) {
        val body: String = textBody
        val result: Result = getResult(BadRequest(body))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains(s"Response: $body")))
      }

    "correct for clientErrors with response xml body" in
      new Setup(configName) {
        val body: Elem = xmlBody
        val result: Result = getResult(BadRequest(body))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains(s"Response: $body")))
      }

    "correct for clientErrors with response other body" in
      new Setup(configName) {
        val body: Elem = otherBody
        val result: Result = getResult(BadRequest(body).as("text/html"))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains("Content type text/html")))
      }

    "correct for serverErrors with response no body" in
      new Setup(configName) {
        val result: Result = getResult(ServiceUnavailable)
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains("No content type")))
      }

    "correct for serverErrors with response json body" in
      new Setup(configName) {
        val body: JsObject = jsonBody
        val result: Result = getResult(ServiceUnavailable(body))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains(s"Response: $body")))
      }

    "correct for serverErrors with response text body" in
      new Setup(configName) {
        val body: String = textBody
        val result: Result = getResult(ServiceUnavailable(body))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains(s"Response: $body")))
      }

    "correct for serverErrors with response xml body" in
      new Setup(configName) {
        val body: Elem = xmlBody
        val result: Result = getResult(ServiceUnavailable(body))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains(s"Response: $body")))
      }

    "correct for serverErrors with response other body" in
      new Setup(configName) {
        val body: Elem = otherBody
        val result: Result = getResult(ServiceUnavailable(body).as("text/html"))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains("Content type text/html")))
      }
  }

  "filter by application-test-only-successful.conf" should {
    val configName = "application-test-only-successful.conf"

    "correct for isSuccessful" in
      new Setup(configName) {
        val body: JsObject = jsonBody
        val result: Result = getResult(Ok(body))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.exists(_.contains(s"Response: $body")))
      }

    "correct for clientErrors" in
      new Setup(configName) {
        val body: JsObject = jsonBody
        val result: Result = getResult(BadRequest(body))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.size === 3)
      }

    "correct for serverErrors" in
      new Setup(configName) {
        val body: JsObject = jsonBody
        val result: Result = getResult(ServiceUnavailable(body))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)
        assert(logMessages.size === 3)
      }
  }

  "filter by application-test-body-size.conf" should {
    val configName = "application-test-body-size.conf"

    "correct for isSuccessful" in
      new Setup(configName) {
        val body: JsObject = jsonBody
        val result: Result = getResult(Ok(body))
        val logMessages: Seq[String] = getLogMessages
        val logMdcProperties: Map[String, String] = getLogMdcProperties

        commonAssert(result, logMessages, logMdcProperties)

        assert(
          logMessages.exists(
            _.contains(
              s"""Response: {"tes...\nNote: The log message is limited by the io.kinoplan.play.filters.logging.response.body.maxLength=5 configuration parameter. Set a higher value for the maxLength parameter if you want to see more."""
            )
          )
        )

      }
  }

}
