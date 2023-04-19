package io.kinoplan.utils.zio.sttp.logging.slf4j

import sttp.client3._
import sttp.client3.logging.{DefaultLog, LogLevel, Logger, LoggingBackend}
import sttp.model.{HeaderNames, StatusCode}

object ZioSlf4jLoggingBackend {

  def apply[F[_], S](
    delegate: SttpBackend[F, S],
    includeTiming: Boolean = true,
    beforeCurlInsteadOfShow: Boolean = false,
    logRequestBody: Boolean = false,
    logRequestHeaders: Boolean = true,
    logResponseBody: Boolean = false,
    logResponseHeaders: Boolean = true,
    sensitiveHeaders: Set[String] = HeaderNames.SensitiveHeaders,
    beforeRequestSendLogLevel: LogLevel = LogLevel.Debug,
    responseLogLevel: StatusCode => LogLevel = DefaultLog.defaultResponseLogLevel,
    responseExceptionLogLevel: LogLevel = LogLevel.Error
  ): SttpBackend[F, S] = {
    val logger = new ZIoSlf4jLogger(
      "io.kinoplan.utils.zio.sttp.logging.slf4j.ZioSlf4jLoggingBackend"
    ).asInstanceOf[Logger[F]]
    LoggingBackend(
      delegate,
      logger,
      includeTiming,
      beforeCurlInsteadOfShow,
      logRequestBody,
      logRequestHeaders,
      logResponseBody,
      logResponseHeaders,
      sensitiveHeaders,
      beforeRequestSendLogLevel,
      responseLogLevel,
      responseExceptionLogLevel
    )
  }

}
