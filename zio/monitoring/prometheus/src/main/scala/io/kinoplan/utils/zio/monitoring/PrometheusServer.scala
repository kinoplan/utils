package io.kinoplan.utils.zio.monitoring

import io.prometheus.client.exporter.HTTPServer
import zio.{Scope, Task, ZIO}
import zio.metrics.prometheus.helpers.{getCurrentRegistry, http, initializeDefaultExports, stopHttp}

object PrometheusServer {

  private def acquire = for {
    config <- ZIO.service[PrometheusConfig]
    registry <- getCurrentRegistry()
    _ <- initializeDefaultExports(registry).ignore
    server <- http(registry, config.port)
  } yield server

  private def release(httpServer: HTTPServer) = stopHttp(httpServer).ignore

  def start(): Task[Nothing] = ZIO
    .acquireRelease(acquire)(release)
    .flatMap(httpServer =>
      ZIO.logInfo(s"Prometheus server started on port: ${httpServer.getPort}") *> ZIO.never
    )
    .tapErrorCause(ZIO.logErrorCause(_))
    .provide(Scope.default, PrometheusConfig.live, PrometheusModule.live)

}
