package io.kinoplan.utils.zio.http4s.config

import zio.{Duration, Layer, ZIO, ZLayer, durationInt}
import zio.Config.Error
import zio.config.magnolia.deriveConfig

private[http4s] case class IntegrationHealthcheckConfig(
  timeout: Duration = 10.seconds,
  logStatuses: Boolean = false
)

private[http4s] object IntegrationHealthcheckConfig {

  private val config =
    deriveConfig[IntegrationHealthcheckConfig].nested("server", "healthcheck", "integration")

  val live: Layer[Error, IntegrationHealthcheckConfig] = ZLayer.fromZIO(ZIO.config(config))

}
