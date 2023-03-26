package io.kinoplan.utils.zio.reactivemongo.api

import scala.concurrent.duration.DurationInt

import reactivemongo.api.{AsyncDriver, DB, MongoConnection}
import reactivemongo.api.MongoConnection.ParsedURIWithDB
import zio.{Task, ZIO, ZLayer}

import io.kinoplan.utils.IntegrationCheck
import io.kinoplan.utils.zio.reactivemongo.config.MongoConfig

trait ReactiveMongoApi {
  def driver: AsyncDriver
  def connection: MongoConnection
  def database: Task[DB]
}

case class ReactiveMongoApiLive(
  asyncDriver: AsyncDriver,
  mongoParsedUri: ParsedURIWithDB,
  mongoConnection: MongoConnection
) extends ReactiveMongoApi
      with IntegrationCheck[Task] {

  lazy val checkServiceName: String = s"reactivemongo.${mongoParsedUri.db}"

  lazy val driver: AsyncDriver = asyncDriver

  lazy val connection: MongoConnection = mongoConnection

  def database: Task[DB] = ZIO.fromFuture(implicit ec => connection.database(mongoParsedUri.db))

  def checkAvailability: Task[Boolean] = for {
    db <- database
    status <- ZIO.fromFuture(implicit ec => db.ping())
  } yield status

}

object ReactiveMongoApi {

  private def acquire(dbName: String) = (
    for {
      asyncDriver <- ZIO.service[AsyncDriver]
      mongoConfig <- ZIO.service[MongoConfig]
      uri <- ZIO
        .fromOption(mongoConfig.databases.find(_.current(dbName)).map(_.uri))
        .orElseFail(new Throwable(s"mongodb database with name $dbName not found"))
      mongoParsedUri <- ZIO.fromFuture(implicit ec => MongoConnection.fromStringWithDB(uri))
      connection <- ZIO.fromFuture(_ =>
        asyncDriver.connect(mongoParsedUri, Some(mongoParsedUri.db), strictMode = false)
      )
      reactiveMongoApi = ReactiveMongoApiLive(asyncDriver, mongoParsedUri, connection)
      integrationCheck = Set(reactiveMongoApi.asInstanceOf[IntegrationCheck[Task]])
    } yield (dbName -> reactiveMongoApi, integrationCheck)
  ).uninterruptible

  private def release(api: ReactiveMongoApi) = ZIO
    .fromFuture(_ => api.connection.close()(10.seconds))
    .orDie
    .unit

  def make(
    dbName: String
  ): ZLayer[AsyncDriver with MongoConfig, Throwable, ((String, ReactiveMongoApi), Set[IntegrationCheck[Task]])] =
    ZLayer.scoped(
      ZIO.acquireRelease(acquire(dbName)) { case ((_, api), _) =>
        release(api)
      }
    )

  def getAt(dbName: String): ZIO[Map[String, ReactiveMongoApi], Throwable, ReactiveMongoApi] = ZIO
    .serviceAt[ReactiveMongoApi](dbName)
    .flatMap(ZIO.fromOption(_))
    .orElseFail(new Throwable(s"The mongodb $dbName key cannot be found in the ZIO environment"))

}
