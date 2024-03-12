package io.kinoplan.utils.zio.http4s

import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware._
import zio.{RIO, Task, ZIO}
import zio.interop.catz._

import io.kinoplan.utils.zio.http4s.config.ServerConfig

object HealthcheckServer {

  private def routesWithLogger = Logger
    .httpRoutes(logHeaders = true, logBody = true)(new Healthcheck[Any].routes())

  def start(): Task[Unit] = ZIO
    .serviceWithZIO[ServerConfig] { serverConfig =>
      ZIO
        .executor
        .flatMap(executor =>
          BlazeServerBuilder[RIO[Any, *]]
            .withExecutionContext(executor.asExecutionContext)
            .bindHttp(serverConfig.port, serverConfig.host)
            .withHttpApp(Router("/" -> routesWithLogger).orNotFound)
            .serve
            .compile
            .drain
        )
    }
    .provide(ServerConfig.live)

}
