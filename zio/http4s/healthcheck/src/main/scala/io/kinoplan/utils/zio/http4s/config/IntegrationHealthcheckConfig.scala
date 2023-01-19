package io.kinoplan.utils.zio.http4s.config

import com.typesafe.config.ConfigFactory
import zio.{Duration, Layer, ZIO, durationInt}
import zio.config._
import zio.config.ConfigDescriptor._
import zio.config.magnolia.descriptor
import zio.config.typesafe._

private[http4s] case class IntegrationHealthcheckConfig(
  timeout: Duration = 10.seconds,
  logStatuses: Boolean = false
)

private[http4s] object IntegrationHealthcheckConfig {

  private val configDescriptor = nested("server")(
    nested("healthcheck")(nested("integration")(descriptor[IntegrationHealthcheckConfig]))
  )

  val live: Layer[ReadError[String], IntegrationHealthcheckConfig] = TypesafeConfig
    .fromTypesafeConfig(ZIO.attempt(ConfigFactory.load.resolve), configDescriptor)

}
