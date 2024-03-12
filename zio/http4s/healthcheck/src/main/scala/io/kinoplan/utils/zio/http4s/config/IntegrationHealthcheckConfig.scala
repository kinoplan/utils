package io.kinoplan.utils.zio.http4s.config

import zio.{Duration, Layer, ZLayer, durationInt}
import zio.Config.Error
import zio.config.magnolia.deriveConfig
import zio.config.typesafe._

private[http4s] case class IntegrationHealthcheckConfig(
  timeout: Duration = 10.seconds,
  logStatuses: Boolean = false
)

private[http4s] object IntegrationHealthcheckConfig {

  private val config = deriveConfig[IntegrationHealthcheckConfig]
    .nested("server", "healthcheck", "integration")

  val live: Layer[Error, IntegrationHealthcheckConfig] = ZLayer
    .fromZIO(TypesafeConfigProvider.fromResourcePath().load(config))

}
