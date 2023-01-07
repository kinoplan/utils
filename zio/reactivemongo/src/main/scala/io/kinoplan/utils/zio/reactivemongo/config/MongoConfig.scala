package io.kinoplan.utils.zio.reactivemongo.config

import com.typesafe.config.ConfigFactory
import zio.{Layer, ZIO}
import zio.config._
import zio.config.ConfigDescriptor._
import zio.config.typesafe._

case class MongoConfig(databases: List[Database])

case class Database(name: String, uri: String) {
  def current(name: String): Boolean = this.name == name
}

object MongoConfig {

  private def databaseDescriptor(dbName: String) = nested("mongodb")(
    nested(dbName)(string("uri").transform[Database](Database.apply(dbName, _), _.uri))
  )

  private def databasesDescriptor(dbNames: Seq[String]) = dbNames.map(databaseDescriptor)

  private def configDescriptor(dbNames: Seq[String]) =
    collectAll(databasesDescriptor(dbNames).head, databasesDescriptor(dbNames).tail: _*)
      .to[MongoConfig]

  def live(dbNames: Seq[String]): Layer[ReadError[String], MongoConfig] = TypesafeConfig
    .fromTypesafeConfig(ZIO.attempt(ConfigFactory.load.resolve), configDescriptor(dbNames))

}
