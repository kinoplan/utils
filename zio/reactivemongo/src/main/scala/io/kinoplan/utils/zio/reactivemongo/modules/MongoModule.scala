package io.kinoplan.utils.zio.reactivemongo.modules

import distage.ModuleDef
import reactivemongo.api.AsyncDriver
import zio.prelude.NonEmptySet

import io.kinoplan.utils.zio.reactivemongo.api.ReactiveMongoApi
import io.kinoplan.utils.zio.reactivemongo.api.driver.AsyncDriverResource
import io.kinoplan.utils.zio.reactivemongo.config.MongoConfig

object MongoModule {

  def apply(dbNames: NonEmptySet[String]): ModuleDef = new ModuleDef {
    make[AsyncDriver].fromResource[AsyncDriverResource]
    make[MongoConfig].fromHas(MongoConfig.live(dbNames.toSeq))
    dbNames.toSeq.foreach { dbName =>
      make[ReactiveMongoApi].named(dbName).fromHas(ReactiveMongoApi.live(dbName))
    }
  }

}
