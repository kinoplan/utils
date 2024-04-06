package io.kinoplan.utils.zio.http4s.config

import zio._
import zio.Config.Error
import zio.config.magnolia.deriveConfig

private[http4s] case class RootConfig(server: ServerConfig)

private[http4s] case class ServerConfig(port: Int, host: String)

private[http4s] object ServerConfig {
  private val config = deriveConfig[RootConfig].map(_.server)

  val live: Layer[Error, ServerConfig] = ZLayer.fromZIO(ZIO.config(config))
}
