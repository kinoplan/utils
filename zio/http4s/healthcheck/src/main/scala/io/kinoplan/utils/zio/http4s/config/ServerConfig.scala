package io.kinoplan.utils.zio.http4s.config

import zio._
import zio.Config.Error
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider

private[http4s] case class RootConfig(server: ServerConfig)

private[http4s] case class ServerConfig(port: Int, host: String)

private[http4s] object ServerConfig {

  val live: Layer[Error, ServerConfig] = ZLayer
    .fromZIO(TypesafeConfigProvider.fromResourcePath().load(deriveConfig[RootConfig]).map(_.server))

}
