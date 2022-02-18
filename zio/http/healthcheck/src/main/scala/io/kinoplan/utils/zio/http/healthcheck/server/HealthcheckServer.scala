package io.kinoplan.utils.zio.http.healthcheck.server

import zhttp.service._
import zio.ZIO
import zio.clock.Clock
import zio.logging._

import io.kinoplan.utils.zio.http.healthcheck.helpers.HttpLog
import io.kinoplan.utils.zio.http.healthcheck.routes.HealthcheckRoutes

object HealthcheckServer {

  def start(port: Int): ZIO[Logging with Clock, Throwable, Unit] = for {
    _ <- Server.start(port, HttpLog(HealthcheckRoutes.expose))
    _ <- log.info(s"Server started on port $port")
  } yield ()

}
