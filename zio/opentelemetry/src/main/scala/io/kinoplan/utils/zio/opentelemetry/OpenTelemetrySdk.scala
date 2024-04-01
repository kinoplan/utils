package io.kinoplan.utils.zio.opentelemetry

import io.opentelemetry.{api, sdk}
import zio.{TaskLayer, ZIO, ZLayer}
import zio.telemetry.opentelemetry.OpenTelemetry
import zio.telemetry.opentelemetry.context.ContextStorage

import io.kinoplan.utils.zio.opentelemetry.config.{OpenTelemetryConfig, RootConfig}
import io.kinoplan.utils.zio.opentelemetry.config.extensions.TracerProviderType
import io.kinoplan.utils.zio.opentelemetry.provider.TracerProvider

object OpenTelemetrySdk {

  val live: TaskLayer[api.OpenTelemetry with ContextStorage] = RootConfig
    .live
    .project(_.opentelemetry) >>>
    ZLayer
      .service[OpenTelemetryConfig]
      .flatMap(config =>
        OpenTelemetry.custom(
          for {
            tracerProviderO <- ZIO
              .fromOption(config.get.tracing)
              .flatMap(tracerConfig =>
                tracerConfig.provider match {
                  case TracerProviderType.noop   => ZIO.none
                  case TracerProviderType.stdout => TracerProvider.stdout(tracerConfig).asSome
                  case TracerProviderType.jaeger => TracerProvider.jaeger(tracerConfig).asSome
                }
              )
              .orElseSucceed(None)
            openTelemetry <- ZIO.fromAutoCloseable(
              ZIO.succeed {
                val builder = sdk.OpenTelemetrySdk.builder()

                val builderWithTracer =
                  tracerProviderO.map(builder.setTracerProvider).getOrElse(builder)

                builderWithTracer.build()
              }
            )
          } yield openTelemetry
        )
      ) ++ ContextStorage.fiberRef

}
