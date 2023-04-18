package io.kinoplan.utils.http4s.server.middleware

import cats.{FlatMap, ~>}
import cats.arrow.FunctionK
import cats.data.{Kleisli, NonEmptyList, OptionT}
import cats.effect.Sync
import cats.syntax.all._
import org.http4s.{Header, Http, HttpApp, HttpRoutes, Request, Response}
import org.http4s.server.middleware.RequestId
import org.typelevel.ci._

object CustomRequestId {

  private[middleware] val requestIdHeader = ci"X-Request-ID"

  def apply[G[_], F[_]](fk: F ~> G, headerName: CIString = requestIdHeader, genReqId: F[String])(
    http: Http[G, F]
  )(implicit
    G: FlatMap[G],
    F: Sync[F]
  ): Http[G, F] = Kleisli[G, Request[F], Response[F]] { req =>
    for {
      header <- fk(
        req.headers.get(headerName) match {
          case None                          => genReqId.map(reqId => Header.Raw(headerName, reqId))
          case Some(NonEmptyList(header, _)) => F.pure(header)
        }
      )
      reqId = header.value
      response <- http(req.withAttribute(RequestId.requestIdAttrKey, reqId).putHeaders(header))
    } yield response.withAttribute(RequestId.requestIdAttrKey, reqId).putHeaders(header)
  }

  object httpApp {

    def apply[F[_]: Sync](headerName: CIString = requestIdHeader, genReqId: F[String])(
      httpApp: HttpApp[F]
    ): HttpApp[F] = CustomRequestId.apply(FunctionK.id[F], headerName, genReqId)(httpApp)

  }

  object httpRoutes {

    def apply[F[_]: Sync](headerName: CIString = requestIdHeader, genReqId: F[String])(
      httpRoutes: HttpRoutes[F]
    ): HttpRoutes[F] = CustomRequestId.apply(OptionT.liftK[F], headerName, genReqId)(httpRoutes)

  }

}
