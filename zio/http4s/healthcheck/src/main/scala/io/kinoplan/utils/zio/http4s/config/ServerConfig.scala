package io.kinoplan.utils.zio.http4s.config

import com.typesafe.config.ConfigFactory
import zio._
import zio.config._
import zio.config.magnolia.descriptor
import zio.config.syntax.ZIOConfigNarrowOps
import zio.config.typesafe.TypesafeConfig

private[http4s] case class RootConfig(server: ServerConfig)

private[http4s] case class ServerConfig(port: Int, host: String)

private[http4s] object ServerConfig {
  private val configDescriptor = descriptor[RootConfig]

  val live: Layer[ReadError[String], ServerConfig] = TypesafeConfig
    .fromTypesafeConfig(ZIO.attempt(ConfigFactory.load.resolve), configDescriptor)
    .narrow(_.server)

}
