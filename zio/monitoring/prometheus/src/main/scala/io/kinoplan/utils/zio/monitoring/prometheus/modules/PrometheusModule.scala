package io.kinoplan.utils.zio.monitoring.prometheus.modules

import distage.ModuleDef
import zio.metrics.prometheus.Registry
import zio.metrics.prometheus.exporters.Exporters

import io.kinoplan.utils.zio.monitoring.prometheus.config.PrometheusConfig

object PrometheusModule {

  def apply(): ModuleDef = new ModuleDef {
    make[PrometheusConfig].fromHas(PrometheusConfig.live)
    make[Registry.Service].fromHas(Registry.live)
    make[Exporters.Service].fromHas(Exporters.live)
  }

}
