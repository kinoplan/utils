package io.kinoplan.utils.zio.monitoring

import io.micrometer.core.instrument.Clock
import io.micrometer.prometheus.{PrometheusConfig, PrometheusMeterRegistry}
import io.prometheus.client.CollectorRegistry
import zio.{ULayer, ZLayer}
import zio.metrics.connectors.micrometer
import zio.metrics.connectors.micrometer.MicrometerConfig

private[monitoring] object PrometheusModule {

  private val registryLive = ZLayer.succeed(
    new PrometheusMeterRegistry(
      PrometheusConfig.DEFAULT,
      CollectorRegistry.defaultRegistry,
      Clock.SYSTEM
    )
  )

  private val micrometerConfigLive = ZLayer.succeed(MicrometerConfig.default)
  private val micrometerLive = registryLive ++ micrometerConfigLive >>> micrometer.micrometerLayer

  val live: ULayer[Unit with PrometheusMeterRegistry] = micrometerLive ++ registryLive

}
