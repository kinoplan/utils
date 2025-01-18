package io.kinoplan.utils.zio.tapir.opentelemetry

import scala.collection.mutable

import io.opentelemetry.api.trace.{SpanKind, StatusCode}
import io.opentelemetry.semconv.{HttpAttributes, UrlAttributes, UserAgentAttributes}
import sttp.model.HeaderNames
import sttp.monad.MonadError
import sttp.tapir.AnyEndpoint
import sttp.tapir.model.ServerRequest
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.interceptor._
import sttp.tapir.server.interpreter.BodyListener
import sttp.tapir.server.model.ServerResponse
import zio._
import zio.telemetry.opentelemetry.common.{Attribute, Attributes}
import zio.telemetry.opentelemetry.context.IncomingContextCarrier
import zio.telemetry.opentelemetry.tracing.{StatusMapper, Tracing}
import zio.telemetry.opentelemetry.tracing.propagation.TraceContextPropagator

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

      val attributes = List(
        Attribute(HttpAttributes.HTTP_REQUEST_METHOD, request.method.method),
        Attribute(UrlAttributes.URL_FULL, request.uri.toString())
      ) ++ request.uri.scheme.map(Attribute(UrlAttributes.URL_SCHEME, _)).toList ++
        request
          .header(HeaderNames.UserAgent)
          .map(Attribute(UserAgentAttributes.USER_AGENT_ORIGINAL, _))
          .toList

      tracing.extractSpan(
        TraceContextPropagator.default,
        IncomingContextCarrier.default(
          mutable.Map(request.headers.map(header => header.name -> header.value): _*)
        ),
        request.method.method,
        spanKind = SpanKind.SERVER,
        statusMapper = statusMapper,
        attributes = Attributes.fromList(attributes)
      )(
        requestHandler(new EndpointTracingInterceptor[R1](tracing))(
          request,
          endpoints.filterNot(serverEndpoint => ignoreEndpoints.contains(serverEndpoint.endpoint))
        )
      )
    }

  }

}

private class EndpointTracingInterceptor[R1](tracing: Tracing) extends EndpointInterceptor[RIO[R1, *]] {

  override def apply[B](
    responder: Responder[RIO[R1, *], B],
    endpointHandler: EndpointHandler[RIO[R1, *], B]
  ): EndpointHandler[RIO[R1, *], B] = new EndpointHandler[RIO[R1, *], B] {

    override def onDecodeSuccess[A, U, I](ctx: DecodeSuccessContext[RIO[R1, *], A, U, I])(implicit
      monad: MonadError[RIO[R1, *]],
      bodyListener: BodyListener[RIO[R1, *], B]
    ): RIO[R1, ServerResponse[B]] = onEndpoint(ctx.endpoint) *>
      endpointHandler.onDecodeSuccess(ctx).tap(onResponse)

    override def onSecurityFailure[A](ctx: SecurityFailureContext[RIO[R1, *], A])(implicit
      monad: MonadError[RIO[R1, *]],
      bodyListener: BodyListener[RIO[R1, *], B]
    ): RIO[R1, ServerResponse[B]] = onEndpoint(ctx.endpoint) *>
      endpointHandler.onSecurityFailure(ctx).tap(onResponse)

    override def onDecodeFailure(ctx: DecodeFailureContext)(implicit
      monad: MonadError[RIO[R1, *]],
      bodyListener: BodyListener[RIO[R1, *], B]
    ): RIO[R1, Option[ServerResponse[B]]] = onEndpoint(ctx.endpoint) *>
      endpointHandler.onDecodeFailure(ctx).tap(responseO => responseO.fold(ZIO.unit)(onResponse))

    private def onResponse(response: ServerResponse[B]): ZIO[Any, Nothing, Unit] = tracing
      .setAttribute(HttpAttributes.HTTP_RESPONSE_STATUS_CODE.getKey, response.code.code.toLong)
      .unit

    private def onEndpoint(endpoint: AnyEndpoint): ZIO[Any, Nothing, Unit] = {
      val route = endpoint.showPathTemplate(showQueryParam = None, includeAuth = false)
      val method = endpoint.method.fold("")(_.method)
      tracing
        .setAttribute(HttpAttributes.HTTP_ROUTE, route)
        .zipRight(tracing.getCurrentSpanUnsafe.map(_.updateName(s"$method $route")))
        .unit
    }

  }

}

object TracingInterceptor {

  def apply[R](tracing: Tracing, ignoreEndpoints: Seq[AnyEndpoint] = Seq.empty) =
    new TracingInterceptor[R](tracing, ignoreEndpoints)

}
