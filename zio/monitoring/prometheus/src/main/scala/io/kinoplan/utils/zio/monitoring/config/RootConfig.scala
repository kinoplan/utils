package io.kinoplan.utils.zio.monitoring.config

import zio.{Layer, ZIO, ZLayer}
import zio.Config.Error
import zio.config.magnolia.deriveConfig

private[monitoring] case class RootConfig(port: Int)

private[monitoring] object RootConfig {
  private val config = deriveConfig[RootConfig].nested("prometheus")

  val live: Layer[Error, RootConfig] = ZLayer.fromZIO(ZIO.config(config))
}
