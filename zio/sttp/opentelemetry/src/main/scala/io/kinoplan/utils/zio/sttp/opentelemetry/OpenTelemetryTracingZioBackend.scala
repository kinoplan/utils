package io.kinoplan.utils.zio.sttp.opentelemetry

import io.opentelemetry.api.trace.{SpanKind, StatusCode}
import sttp.capabilities.Effect
import sttp.client3._
import zio._
import zio.telemetry.opentelemetry.context.OutgoingContextCarrier
import zio.telemetry.opentelemetry.tracing._
import zio.telemetry.opentelemetry.tracing.propagation.TraceContextPropagator

// FIXME: Change to sttp opentelemetry-tracing-zio-backend after updating it to zio-opentelemetry 3.x.x
private class OpenTelemetryTracingZioBackend[+P](
  delegate: SttpBackend[Task, P],
  tracer: OpenTelemetryZioTracer,
  tracing: Tracing
) extends DelegateSttpBackend[Task, P](delegate) {

  def send[T, R >: P with Effect[Task]](request: Request[T, R]): Task[Response[T]] = {
    val propagator = TraceContextPropagator.default
    val carrier = OutgoingContextCarrier.default()

    tracing
      .span(
        tracer.spanName(request),
        SpanKind.CLIENT,
        statusMapper = StatusMapper.failureThrowable(_ => StatusCode.ERROR)
      )(
        for {
          _ <- tracing.injectSpan(propagator, carrier)
          _ <- tracer.before(request)
          resp <- delegate.send(request.headers(carrier.kernel.toMap))
          _ <- tracer.after(resp)
        } yield resp
      )
      .provideLayer(ZLayer.succeed(tracing))
  }

}

object OpenTelemetryTracingZioBackend {

  def apply[P](
    other: SttpBackend[Task, P],
    tracing: Tracing,
    tracer: OpenTelemetryZioTracer
  ): SttpBackend[Task, P] = new OpenTelemetryTracingZioBackend[P](other, tracer, tracing)

}

trait OpenTelemetryZioTracer {
  def spanName[T](request: Request[T, Nothing]): String
  def before[T](request: Request[T, Nothing]): RIO[Tracing, Unit]
  def after[T](response: Response[T]): RIO[Tracing, Unit]
}

object OpenTelemetryZioTracer {

  def default(tracing: Tracing): OpenTelemetryZioTracer = new OpenTelemetryZioTracer {
    override def spanName[T](request: Request[T, Nothing]): String = s"HTTP ${request.method.method}"

    override def before[T](request: Request[T, Nothing]): RIO[Tracing, Unit] =
      (
        tracing.setAttribute("http.method", request.method.method) *>
          tracing.setAttribute("http.url", request.uri.toString())
      ).unit

    override def after[T](response: Response[T]): RIO[Tracing, Unit] = tracing
      .setAttribute("http.status_code", response.code.code.toLong)
      .unit

  }

}
