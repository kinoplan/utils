package io.kinoplan.utils.zio.http4s

import org.http4s._
import org.http4s.dsl.Http4sDsl
import zio._
import zio.interop.catz._

class Healthcheck[R] extends Http4sDsl[RIO[R, *]] {

  def routes(): HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] { case GET -> Root / "health" =>
    Ok()
  }

}
