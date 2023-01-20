package io.kinoplan.utils.zio.monitoring

import zio.{&, ULayer}
import zio.metrics.prometheus.Registry
import zio.metrics.prometheus.exporters.Exporters

object PrometheusModule {

  val live: ULayer[Registry.Service & Exporters] = Registry.live ++ Exporters.live

}
