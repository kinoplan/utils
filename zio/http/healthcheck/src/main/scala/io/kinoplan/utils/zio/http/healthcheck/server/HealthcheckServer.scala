package io.kinoplan.utils.zio.http.healthcheck.server

import zhttp.service._
import zio.ZIO

import io.kinoplan.utils.zio.http.healthcheck.routes.HealthcheckRoutes

object HealthcheckServer {

  def start(port: Int): ZIO[Any, Throwable, Nothing] = Server.start(port, HealthcheckRoutes.expose)

}
