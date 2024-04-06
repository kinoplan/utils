package io.kinoplan.utils.zio.tapir.opentelemetry

import io.opentelemetry.api.trace.{SpanKind, StatusCode}
import sttp.monad.MonadError
import sttp.tapir.AnyEndpoint
import sttp.tapir.model.ServerRequest
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.interceptor._
import sttp.tapir.server.interpreter.BodyListener
import sttp.tapir.server.model.ServerResponse
import zio._
import zio.telemetry.opentelemetry.tracing.{StatusMapper, Tracing}

class TracingInterceptor[R1](tracing: Tracing, ignoreEndpoints: Seq[AnyEndpoint])
    extends RequestInterceptor[ZIO[R1, Throwable, *]] {

  override def apply[R, B](
    responder: Responder[RIO[R1, *], B],
    requestHandler: EndpointInterceptor[RIO[R1, *]] => RequestHandler[RIO[R1, *], R, B]
  ): RequestHandler[RIO[R1, *], R, B] = new RequestHandler[RIO[R1, *], R, B] {

    override def apply(request: ServerRequest, endpoints: List[ServerEndpoint[R, RIO[R1, *]]])(
      implicit
      monad: MonadError[RIO[R1, *]]
    ): RIO[R1, RequestResult[B]] = {

      val statusMapper = StatusMapper.failureThrowable(_ => StatusCode.ERROR)

      val response = for {
        _ <- tracing.setAttribute("http.method", request.method.method)
        _ <- tracing.setAttribute("http.url", request.uri.toString())
        response <- requestHandler(new EndpointTracingInterceptor[R1](tracing))(
          request,
          endpoints.filterNot(serverEndpoint => ignoreEndpoints.contains(serverEndpoint.endpoint))
        )
      } yield response

      tracing.span(
        request.method.method + " " + request.uri.toString(),
        spanKind = SpanKind.SERVER,
        statusMapper = statusMapper
      )(response)
    }

  }

}

private class EndpointTracingInterceptor[R1](tracing: Tracing)
    extends EndpointInterceptor[RIO[R1, *]] {

  override def apply[B](
    responder: Responder[RIO[R1, *], B],
    endpointHandler: EndpointHandler[RIO[R1, *], B]
  ): EndpointHandler[RIO[R1, *], B] = new EndpointHandler[RIO[R1, *], B] {

    override def onDecodeSuccess[A, U, I](ctx: DecodeSuccessContext[RIO[R1, *], A, U, I])(implicit
      monad: MonadError[RIO[R1, *]],
      bodyListener: BodyListener[RIO[R1, *], B]
    ): RIO[R1, ServerResponse[B]] = endpointHandler.onDecodeSuccess(ctx).tap(onResponse)

    override def onSecurityFailure[A](ctx: SecurityFailureContext[RIO[R1, *], A])(implicit
      monad: MonadError[RIO[R1, *]],
      bodyListener: BodyListener[RIO[R1, *], B]
    ): RIO[R1, ServerResponse[B]] = endpointHandler.onSecurityFailure(ctx).tap(onResponse)

    override def onDecodeFailure(ctx: DecodeFailureContext)(implicit
      monad: MonadError[RIO[R1, *]],
      bodyListener: BodyListener[RIO[R1, *], B]
    ): RIO[R1, Option[ServerResponse[B]]] = endpointHandler
      .onDecodeFailure(ctx)
      .tap(responseO => responseO.fold(ZIO.unit)(onResponse))

    private def onResponse(response: ServerResponse[B]): ZIO[Any, Nothing, Unit] = tracing
      .setAttribute("http.status_code", response.code.code.toLong)
      .unit

  }

}

object TracingInterceptor {

  def apply[R](tracing: Tracing, ignoreEndpoints: Seq[AnyEndpoint] = Seq.empty) =
    new TracingInterceptor[R](tracing, ignoreEndpoints)

}
