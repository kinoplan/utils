package io.kinoplan.utils.zio.monitoring

import java.net.InetSocketAddress

import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.exporter.HTTPServer
import zio.{Scope, Task, ZIO}
import zio.metrics.jvm.DefaultJvmMetrics

object PrometheusServer {

  private def acquire = for {
    _ <- ZIO.logInfo("Starting prometheus server...")
    config <- ZIO.service[RootConfig]
    prometheusRegistry <- ZIO.service[PrometheusMeterRegistry]
    http <- ZIO.attempt(
      new HTTPServer(new InetSocketAddress(config.port), prometheusRegistry.getPrometheusRegistry)
    )
  } yield http

  private def release(httpServer: HTTPServer) = ZIO.succeed(httpServer.close())

  def start(): Task[Nothing] = ZIO
    .acquireRelease(acquire)(release)
    .flatMap(httpServer =>
      ZIO.logInfo(s"Prometheus server started on port ${httpServer.getPort}") *> ZIO.never
    )
    .tapErrorCause(ZIO.logErrorCause(_))
    .provide(Scope.default, RootConfig.live, PrometheusModule.live, DefaultJvmMetrics.live.unit)

}
