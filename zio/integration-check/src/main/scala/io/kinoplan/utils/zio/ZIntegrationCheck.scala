package io.kinoplan.utils.zio

import zio.{Task, ZEnvironment, ZLayer}

import io.kinoplan.utils.IntegrationCheck

object ZIntegrationCheck {

  def live(
    layers: ZLayer[Any, Throwable, Set[IntegrationCheck[Task]]]*
  ): ZLayer[Any, Throwable, Set[IntegrationCheck[Task]]] = ZLayer
    .collectAll(layers)
    .map(env => ZEnvironment(env.get.flatten.toSet))

}
