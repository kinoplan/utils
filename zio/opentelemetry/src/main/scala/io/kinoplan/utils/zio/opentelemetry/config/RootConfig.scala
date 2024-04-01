package io.kinoplan.utils.zio.opentelemetry.config

import zio._
import zio.Config.Error
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider

import io.kinoplan.utils.zio.opentelemetry.config.extensions.TracerProviderType

private[opentelemetry] case class RootConfig(
  opentelemetry: OpenTelemetryConfig = OpenTelemetryConfig()
)

private[opentelemetry] case class OpenTelemetryConfig(tracing: Option[TracingConfig] = None)

private[opentelemetry] case class TracingConfig(
  provider: TracerProviderType,
  serviceName: String,
  endpoint: Option[String] = None,
  ignoreNamePatterns: Set[String] = Set.empty
)

private[opentelemetry] object RootConfig {
  private val config = deriveConfig[RootConfig]

  val live: Layer[Error, RootConfig] = ZLayer
    .fromZIO(TypesafeConfigProvider.fromResourcePath().load(config))

}
