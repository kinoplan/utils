package io.kinoplan.utils.zio.sttp.logging.slf4j

import sttp.client4._
import sttp.client4.logging.{LogConfig, Logger, LoggingBackend}
import sttp.monad.MonadError

object ZioSlf4jLoggingBackend {

  def apply(delegate: SyncBackend): SyncBackend = LoggingBackend(delegate, logger(delegate.monad))

  def apply[F[_]](delegate: Backend[F]): Backend[F] = LoggingBackend(delegate, logger(delegate.monad))

  def apply[F[_]](delegate: WebSocketBackend[F]): WebSocketBackend[F] =
    LoggingBackend(delegate, logger(delegate.monad))

  def apply(delegate: WebSocketSyncBackend): WebSocketSyncBackend =
    LoggingBackend(delegate, logger(delegate.monad))

  def apply[F[_], S](delegate: StreamBackend[F, S]): StreamBackend[F, S] =
    LoggingBackend(delegate, logger(delegate.monad))

  def apply[F[_], S](delegate: WebSocketStreamBackend[F, S]): WebSocketStreamBackend[F, S] =
    LoggingBackend(delegate, logger(delegate.monad))

  def apply(delegate: SyncBackend, config: LogConfig): SyncBackend =
    LoggingBackend(delegate, logger(delegate.monad), config)

  def apply[F[_]](delegate: Backend[F], config: LogConfig): Backend[F] =
    LoggingBackend(delegate, logger(delegate.monad), config)

  def apply[F[_]](delegate: WebSocketBackend[F], config: LogConfig): WebSocketBackend[F] =
    LoggingBackend(delegate, logger(delegate.monad), config)

  def apply(delegate: WebSocketSyncBackend, config: LogConfig): WebSocketSyncBackend =
    LoggingBackend(delegate, logger(delegate.monad), config)

  def apply[F[_], S](delegate: StreamBackend[F, S], config: LogConfig): StreamBackend[F, S] =
    LoggingBackend(delegate, logger(delegate.monad), config)

  def apply[F[_], S](
    delegate: WebSocketStreamBackend[F, S],
    config: LogConfig
  ): WebSocketStreamBackend[F, S] = LoggingBackend(delegate, logger(delegate.monad), config)

  private def logger[F[_]](monad: MonadError[F]): Logger[F] =
    new ZIoSlf4jLogger("io.kinoplan.utils.zio.sttp.logging.slf4j.ZioSlf4jLoggingBackend", monad)

}
