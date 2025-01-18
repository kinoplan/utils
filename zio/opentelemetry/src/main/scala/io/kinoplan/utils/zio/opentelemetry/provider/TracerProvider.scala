package io.kinoplan.utils.zio.opentelemetry.provider

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.exporter.logging.otlp.OtlpJsonLoggingSpanExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.`export`.{BatchSpanProcessor, SimpleSpanProcessor}
import io.opentelemetry.semconv.ServiceAttributes
import zio.{Scope, URIO, ZIO}

import io.kinoplan.utils.zio.opentelemetry.config.TracingConfig
import io.kinoplan.utils.zio.opentelemetry.crossCollectionConverters._

private[opentelemetry] object TracerProvider {

  def stdout(config: TracingConfig): URIO[Scope, SdkTracerProvider] = for {
    spanExporter <- ZIO.fromAutoCloseable(ZIO.succeed(OtlpJsonLoggingSpanExporter.create()))
    spanProcessor <- ZIO.fromAutoCloseable(ZIO.succeed(SimpleSpanProcessor.create(spanExporter)))
    tracerProvider <- ZIO.fromAutoCloseable(
      ZIO.succeed(
        SdkTracerProvider
          .builder()
          .setResource(
            Resource.create(Attributes.of(ServiceAttributes.SERVICE_NAME, config.serviceName))
          )
          .addSpanProcessor(spanProcessor)
          .setSampler(new TracerSampler(config.ignoreNamePatterns.asJava))
          .build()
      )
    )
  } yield tracerProvider

  def jaeger(config: TracingConfig): URIO[Scope, SdkTracerProvider] = for {
    spanExporter <- ZIO.fromAutoCloseable(
      ZIO.succeed {
        val builder = OtlpGrpcSpanExporter.builder()

        val builderWithEndpoint = config.endpoint.map(builder.setEndpoint).getOrElse(builder)

        builderWithEndpoint.build()
      }
    )
    spanProcessor <-
      ZIO.fromAutoCloseable(ZIO.succeed(BatchSpanProcessor.builder(spanExporter).build()))
    tracerProvider <- ZIO.fromAutoCloseable(
      ZIO.succeed(
        SdkTracerProvider
          .builder()
          .setResource(
            Resource.create(Attributes.of(ServiceAttributes.SERVICE_NAME, config.serviceName))
          )
          .addSpanProcessor(spanProcessor)
          .setSampler(new TracerSampler(config.ignoreNamePatterns.asJava))
          .build()
      )
    )
  } yield tracerProvider

}
