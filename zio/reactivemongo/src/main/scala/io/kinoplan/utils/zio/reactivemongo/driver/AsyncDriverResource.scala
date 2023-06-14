package io.kinoplan.utils.zio.reactivemongo.driver

import scala.concurrent.duration.DurationInt

import reactivemongo.api.AsyncDriver
import zio.{Scope, ULayer, ZIO, ZLayer}

private[reactivemongo] object AsyncDriverResource {

  private def make: ZIO[Scope, Nothing, AsyncDriver] = ZIO
    .succeed(AsyncDriver())
    .withFinalizer(asyncDriver =>
      ZIO
        .fromFuture(implicit ec => asyncDriver.close(10.seconds))
        .timeout(zio.Duration.fromSeconds(10))
        .orDie
    )

  val live: ULayer[AsyncDriver] = ZLayer.scoped(make)

}
