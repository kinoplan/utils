package io.kinoplan.utils.http4s.server.middleware

import java.util.UUID

import cats.arrow.FunctionK
import cats.data.{Kleisli, OptionT}
import cats.effect.kernel.{Async, MonadCancelThrow}
import cats.~>
import org.http4s.{Http, HttpApp, HttpRoutes, Request}
import org.http4s.server.middleware.Logger
import org.typelevel.ci.CIString

object CustomLogger {

  def apply[G[_], F[_]](
    logHeaders: Boolean,
    logBody: Boolean,
    logAction: String => String => F[Unit],
    fk: F ~> G,
    redactHeadersWhen: CIString => Boolean = Logger.defaultRedactHeadersWhen,
    requestIdHeaderName: CIString = CustomRequestId.requestIdHeader
  )(http: Http[G, F])(implicit
    G: MonadCancelThrow[G],
    F: Async[F]
  ): Http[G, F] = Kleisli { req: Request[F] =>
    val requestId = req
      .headers
      .get(requestIdHeaderName)
      .map(_.head.value)
      .getOrElse(UUID.randomUUID().toString)

    val routesWithRequestId: Http[G, F] =
      CustomRequestId.apply(fk, genReqId = F.pure(requestId))(http)

    val logActionWithRequestId = logAction(requestId)

    val routesWithLogger: Http[G, F] = Logger.apply(
      logHeaders = logHeaders,
      logBody = logBody,
      fk = fk,
      redactHeadersWhen = redactHeadersWhen,
      logAction = Some(logActionWithRequestId)
    )(routesWithRequestId)

    routesWithLogger(req)
  }

  def httpApp[F[_]: Async](
    logHeaders: Boolean,
    logBody: Boolean,
    logAction: String => String => F[Unit],
    redactHeadersWhen: CIString => Boolean = Logger.defaultRedactHeadersWhen,
    requestIdHeaderName: CIString = CustomRequestId.requestIdHeader
  )(httpApp: HttpApp[F]): HttpApp[F] =
    apply(logHeaders, logBody, logAction, FunctionK.id[F], redactHeadersWhen, requestIdHeaderName)(
      httpApp
    )

  def httpRoutes[F[_]: Async](
    logHeaders: Boolean,
    logBody: Boolean,
    logAction: String => String => F[Unit],
    redactHeadersWhen: CIString => Boolean = Logger.defaultRedactHeadersWhen,
    requestIdHeaderName: CIString = CustomRequestId.requestIdHeader
  )(httpRoutes: HttpRoutes[F]): HttpRoutes[F] =
    apply(logHeaders, logBody, logAction, OptionT.liftK[F], redactHeadersWhen, requestIdHeaderName)(
      httpRoutes
    )

}
