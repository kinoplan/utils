package io.kinoplan.utils.zio

import zio.{Task, ZEnvironment, ZLayer}

import io.kinoplan.utils.IntegrationCheck

object ZIntegrationCheck {

  def live[R](
    layers: ZLayer[R, Throwable, Set[IntegrationCheck[Task]]]*
  ): ZLayer[R, Throwable, Set[IntegrationCheck[Task]]] = ZLayer
    .collectAll(layers)
    .map(env => ZEnvironment(env.get.flatten.toSet))

}
