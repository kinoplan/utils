package io.kinoplan.utils.zio.sttp.logging.slf4j

import org.slf4j.LoggerFactory
import sttp.client4.logging.{LogLevel, Logger}
import sttp.monad.MonadError
import zio.{Cause, LogAnnotation, ZIO}

class ZIoSlf4jLogger[F[_]](name: String, monad: MonadError[F]) extends Logger[F] {
  private val underlying = LoggerFactory.getLogger(name)

  override def apply(
    level: LogLevel,
    message: => String,
    exception: Option[Throwable],
    context: Map[String, Any]
  ): F[Unit] = {
    val _ = monad

    val annotations = context
      .map { case (key, value) =>
        LogAnnotation(key, value.toString)
      }
      .toSet

    ZIO
      .logAnnotate(annotations) {
        level match {
          case LogLevel.Trace if underlying.isTraceEnabled =>
            exception match {
              case Some(t) => ZIO.logTraceCause(message, Cause.fail(t))
              case None    => ZIO.logTrace(message)
            }
          case LogLevel.Debug if underlying.isDebugEnabled =>
            exception match {
              case Some(t) => ZIO.logDebugCause(message, Cause.fail(t))
              case None    => ZIO.logDebug(message)
            }
          case LogLevel.Info if underlying.isInfoEnabled =>
            exception match {
              case Some(t) => ZIO.logInfoCause(message, Cause.fail(t))
              case None    => ZIO.logInfo(message)
            }
          case LogLevel.Warn if underlying.isWarnEnabled =>
            exception match {
              case Some(t) => ZIO.logWarningCause(message, Cause.fail(t))
              case None    => ZIO.logWarning(message)
            }
          case LogLevel.Error if underlying.isErrorEnabled =>
            exception match {
              case Some(t) => ZIO.logErrorCause(message, Cause.fail(t))
              case None    => ZIO.logError(message)
            }
          case _ => ZIO.unit
        }
      }
      .asInstanceOf[F[Unit]]
  }

}
