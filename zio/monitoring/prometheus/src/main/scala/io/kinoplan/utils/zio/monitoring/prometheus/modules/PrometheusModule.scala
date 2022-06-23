package io.kinoplan.utils.zio.monitoring.prometheus.modules

import zio.ZLayer
import zio.config.ReadError
import zio.metrics.prometheus.Registry
import zio.metrics.prometheus.exporters.Exporters

import io.kinoplan.utils.zio.monitoring.prometheus.config.PrometheusConfig

object PrometheusModule {

  val live: ZLayer[Any, ReadError[String], PrometheusConfig with Registry.Service with Exporters] =
    PrometheusConfig.live ++ Registry.live ++ Exporters.live

}
