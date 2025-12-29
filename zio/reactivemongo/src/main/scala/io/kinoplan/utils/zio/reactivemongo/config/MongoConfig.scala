package io.kinoplan.utils.zio.reactivemongo.config

import zio.{Config, Layer, ZIO, ZLayer}
import zio.Config._
import zio.config.syntax.ConfigSyntax

private[reactivemongo] case class MongoConfig(databases: List[Database])

private[reactivemongo] case class Database(name: String, uri: String) {
  def current(name: String): Boolean = this.name == name
}

private[reactivemongo] object MongoConfig extends ConfigSyntax {

  private def databaseConfig(dbName: String) = Config
    .succeed(dbName)
    .zip(string("uri"))
    .map { case (dbName, uri) =>
      Database(dbName, uri)
    }
    .nested("mongodb", dbName)

  private def databasesConfig(dbNames: Seq[String]) = dbNames.map(databaseConfig)

  private def config(dbNames: Seq[String]) = Config
    .collectAll(databasesConfig(dbNames).head, databasesConfig(dbNames).tail: _*)
    .map(MongoConfig(_))

  def live(dbNames: Seq[String]): Layer[Error, MongoConfig] =
    ZLayer.fromZIO(ZIO.config[MongoConfig](config(dbNames)))

}
