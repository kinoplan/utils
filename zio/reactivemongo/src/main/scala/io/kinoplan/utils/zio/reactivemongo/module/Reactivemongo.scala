package io.kinoplan.utils.zio.reactivemongo.module

import zio._

import io.kinoplan.utils.zio.ZIntegration
import io.kinoplan.utils.zio.reactivemongo.ReactiveMongoApi
import io.kinoplan.utils.zio.reactivemongo.config.MongoConfig
import io.kinoplan.utils.zio.reactivemongo.driver.AsyncDriverResource

object Reactivemongo {

  def live(dbNames: String*): ZLayer[Any, Throwable, ZIntegration[Map[String, ReactiveMongoApi]]] =
    AsyncDriverResource.live ++ MongoConfig.live(dbNames) >>>
      ZLayer
        .foreach(dbNames)(ReactiveMongoApi.live)
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
