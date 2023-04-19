package io.kinoplan.utils.zio.sttp.logging.slf4j

import org.slf4j.LoggerFactory
import sttp.client3.logging.{LogLevel, Logger}
import zio.{Cause, UIO, ZIO}

class ZIoSlf4jLogger(name: String) extends Logger[UIO] {
  private val underlying = LoggerFactory.getLogger(name)

  override def apply(level: LogLevel, message: => String): UIO[Unit] = (
    level match {
      case LogLevel.Trace => ZIO.logTrace(message).when(underlying.isTraceEnabled)
      case LogLevel.Debug => ZIO.logDebug(message).when(underlying.isDebugEnabled)
      case LogLevel.Info  => ZIO.logInfo(message).when(underlying.isInfoEnabled)
      case LogLevel.Warn  => ZIO.logWarning(message).when(underlying.isWarnEnabled)
      case LogLevel.Error => ZIO.logError(message).when(underlying.isErrorEnabled)
    }
  ).unit

  override def apply(level: LogLevel, message: => String, t: Throwable): UIO[Unit] = (
    level match {
      case LogLevel.Trace =>
        ZIO.logTraceCause(message, Cause.fail(t)).when(underlying.isTraceEnabled)
      case LogLevel.Debug =>
        ZIO.logDebugCause(message, Cause.fail(t)).when(underlying.isDebugEnabled)
      case LogLevel.Info => ZIO.logInfoCause(message, Cause.fail(t)).when(underlying.isInfoEnabled)
      case LogLevel.Warn =>
        ZIO.logWarningCause(message, Cause.fail(t)).when(underlying.isWarnEnabled)
      case LogLevel.Error =>
        ZIO.logErrorCause(message, Cause.fail(t)).when(underlying.isErrorEnabled)
    }
  ).unit

}
