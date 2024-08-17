package io.kinoplan.utils.zio.monitoring

import io.micrometer.core.instrument.Clock
import io.micrometer.prometheusmetrics.{PrometheusConfig, PrometheusMeterRegistry}
import io.prometheus.metrics.model.registry.PrometheusRegistry
import zio.{ULayer, ZLayer}
import zio.metrics.connectors.micrometer
import zio.metrics.connectors.micrometer.MicrometerConfig

private[monitoring] object PrometheusModule {

  private val registryLive = ZLayer.succeed(
    new PrometheusMeterRegistry(
      PrometheusConfig.DEFAULT,
      PrometheusRegistry.defaultRegistry,
      Clock.SYSTEM
    )
  )

  private val micrometerConfigLive = ZLayer.succeed(MicrometerConfig.default)
  private val micrometerLive = registryLive ++ micrometerConfigLive >>> micrometer.micrometerLayer

  val live: ULayer[Unit with PrometheusMeterRegistry] = micrometerLive ++ registryLive

}
