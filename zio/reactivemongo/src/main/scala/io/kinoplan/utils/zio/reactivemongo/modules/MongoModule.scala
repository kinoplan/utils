package io.kinoplan.utils.zio.reactivemongo.modules

import zio._
import zio.prelude.NonEmptySet

import io.kinoplan.utils.zio.ZIntegration
import io.kinoplan.utils.zio.reactivemongo.api.ReactiveMongoApi
import io.kinoplan.utils.zio.reactivemongo.api.driver.AsyncDriverResource
import io.kinoplan.utils.zio.reactivemongo.config.MongoConfig

object MongoModule {

  def live(
    dbNames: NonEmptySet[String]
  ): ZLayer[Any, Throwable, ZIntegration[Map[String, ReactiveMongoApi]]] =
    AsyncDriverResource.live ++ MongoConfig.live(dbNames.toSeq) >>>
      ZLayer
        .foreach(dbNames.toSeq)(ReactiveMongoApi.make)
        .map(env =>
          ZIntegration.environment(
            env
              .get
              .map { case (module, _) =>
                module
              }
              .toMap,
            env
              .get
              .flatMap { case (_, moduleCheck) =>
                moduleCheck
              }
              .toSet
          )
        )

}
