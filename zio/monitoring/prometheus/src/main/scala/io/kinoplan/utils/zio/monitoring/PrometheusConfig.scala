package io.kinoplan.utils.zio.monitoring

import zio.{Layer, ZLayer}
import zio.Config.Error
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider

private[monitoring] case class PrometheusConfig(port: Int)

private[monitoring] object PrometheusConfig {
  private val config = deriveConfig[PrometheusConfig].nested("prometheus")

  val live: Layer[Error, PrometheusConfig] = ZLayer
    .fromZIO(TypesafeConfigProvider.fromResourcePath().load(config))

}
