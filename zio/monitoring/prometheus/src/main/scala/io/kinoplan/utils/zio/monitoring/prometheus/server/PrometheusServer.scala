package io.kinoplan.utils.zio.monitoring.prometheus.server

import io.prometheus.client.exporter.HTTPServer
import zio.{Scope, ZIO}
import zio.config.getConfig
import zio.metrics.prometheus.Registry
import zio.metrics.prometheus.exporters.Exporters
import zio.metrics.prometheus.helpers.{getCurrentRegistry, http, initializeDefaultExports, stopHttp}

import io.kinoplan.utils.zio.monitoring.prometheus.config.PrometheusConfig

object PrometheusServer {

  private def acquire = (for {
    config <- getConfig[PrometheusConfig]
    registry <- getCurrentRegistry()
    _ <- initializeDefaultExports(registry).ignore
    server <- http(registry, config.port)
  } yield PrometheusHttpServer(server)).tapError(error => ZIO.logError(error.getMessage))

  private def release(prometheusHttpServer: PrometheusHttpServer) =
    stopHttp(prometheusHttpServer.httpServer).ignore

  case class PrometheusHttpServer(httpServer: HTTPServer)

  def start: ZIO[Any, Throwable, Nothing] = ZIO
    .acquireRelease(acquire)(release)
    .flatMap(server =>
      ZIO.logInfo(
        s"Prometheus server started on port: ${server.httpServer.getPort}"
      ) *> ZIO.never
    )
    .provideSomeLayer(
      PrometheusConfig.live ++ Registry.live ++ Exporters.live ++ Scope.default
    )

}
