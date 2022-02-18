package io.kinoplan.utils.zio.http.healthcheck.routes

import zhttp.http._
import zio.ZIO

object HealthcheckRoutes {

  val expose: HttpApp[Any, Throwable] = HttpApp.collectM { case Method.GET -> Root / "health" =>
    ZIO.succeed(Response.ok)
  }

}
