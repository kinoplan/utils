package io.kinoplan.utils.zio.http4s

import scala.annotation.nowarn

import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware._
import zio.{RIO, Task, ZIO}
import zio.interop.catz._

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.http4s.config.ServerConfig

object IntegrationHealthcheckServer {
  type Env = IntegrationHealthcheck.Env

  @nowarn
  private def routesWithLogger(
    additionalIntegrationChecks: Set[IntegrationCheck[Task]] = Set.empty
  ) = Logger.httpRoutes(logHeaders = true, logBody = true)(
    new IntegrationHealthcheck[Env].routes(additionalIntegrationChecks)
  )

  def start(
    additionalIntegrationChecks: Set[IntegrationCheck[Task]] = Set.empty
  ): ZIO[Env, Throwable, Unit] = ZIO
    .service[ServerConfig]
    .flatMap(serverConfig =>
      ZIO
        .executor
        .flatMap(executor =>
          BlazeServerBuilder[RIO[Env, *]]
            .withExecutionContext(executor.asExecutionContext)
            .bindHttp(serverConfig.port, serverConfig.host)
            .withHttpApp(Router("/" -> routesWithLogger(additionalIntegrationChecks)).orNotFound)
            .serve
            .compile
            .drain
        )
    )
    .provideSome[Env](ServerConfig.live)

}
