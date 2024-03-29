package io.kinoplan.utils.zio.monitoring

import zio.{Layer, ZLayer}
import zio.Config.Error
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider

private[monitoring] case class RootConfig(port: Int)

private[monitoring] object RootConfig {
  private val config = deriveConfig[RootConfig].nested("prometheus")

  val live: Layer[Error, RootConfig] = ZLayer
    .fromZIO(TypesafeConfigProvider.fromResourcePath().load(config))

}
