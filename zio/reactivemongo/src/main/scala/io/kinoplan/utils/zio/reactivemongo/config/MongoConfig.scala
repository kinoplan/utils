package io.kinoplan.utils.zio.reactivemongo.config

import zio.{Config, Layer, ZLayer}
import zio.Config._
import zio.config._
import zio.config.typesafe._

private[reactivemongo] case class MongoConfig(databases: List[Database])

private[reactivemongo] case class Database(name: String, uri: String) {
  def current(name: String): Boolean = this.name == name
}

private[reactivemongo] object MongoConfig {

  private def databaseDescriptor(dbName: String) = Config
    .succeed(dbName)
    .zip(string("uri"))
    .to[Database]
    .nested("mongodb", dbName)

  private def databasesDescriptor(dbNames: Seq[String]) = dbNames.map(databaseDescriptor)

  private def configDescriptor(dbNames: Seq[String]) = Config
    .collectAll(databasesDescriptor(dbNames).head, databasesDescriptor(dbNames).tail: _*)
    .to[MongoConfig]

  def live(dbNames: Seq[String]): Layer[Error, MongoConfig] = ZLayer
    .fromZIO(TypesafeConfigProvider.fromResourcePath().load(configDescriptor(dbNames)))

}
