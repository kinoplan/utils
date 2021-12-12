package io.kinoplan.utils.zio.monitoring.prometheus.server

import io.prometheus.client.exporter.HTTPServer
import zio.{Has, ZManaged}
import zio.config.getConfig
import zio.logging.{Logging, log}
import zio.metrics.prometheus.Registry
import zio.metrics.prometheus.exporters.Exporters
import zio.metrics.prometheus.helpers.{getCurrentRegistry, http, initializeDefaultExports, stopHttp}

import io.kinoplan.utils.zio.monitoring.prometheus.config.PrometheusConfig

object PrometheusServer {
  case class PrometheusHttpServer(server: HTTPServer)

  val start: ZManaged[Logging with Exporters with Registry with Has[PrometheusConfig], Throwable, PrometheusHttpServer] =
    ZManaged.make(
      (for {
        config <- getConfig[PrometheusConfig]
        registry <- getCurrentRegistry()
        _ <- initializeDefaultExports(registry).ignore
        server <- http(registry, config.port)
        _ <- log.info(s"Prometheus server started on port ${config.port}")
      } yield PrometheusHttpServer(server)).tapError(error => log.throwable(error.getMessage, error))
    )(export => stopHttp(export.server).ignore)

}
