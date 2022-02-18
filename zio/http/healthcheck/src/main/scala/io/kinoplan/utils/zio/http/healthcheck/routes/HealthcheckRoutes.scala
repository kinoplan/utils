package io.kinoplan.utils.zio.http.healthcheck.routes

import zhttp.http._
import zio.UIO

object HealthcheckRoutes {

  val expose: HttpApp[Any, Throwable] =
    Http.collectZIO[Request] { case Method.GET -> !! / "health" => UIO(Response.ok) }

}
