package io.kinoplan.utils.zio.http.healthcheck.routes

import zhttp.http._

object HealthcheckRoutes {

  val expose: HttpApp[Any, Throwable] = Http.collect { case Method.GET -> !! / "health" =>
    Response.ok
  }

}
