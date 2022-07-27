package io.kinoplan.utils.zio.reactivemongo.api

import scala.concurrent.duration.DurationInt

import reactivemongo.api.{AsyncDriver, DB, MongoConnection}
import reactivemongo.api.MongoConnection.ParsedURIWithDB
import zio.{Task, ZIO, ZLayer}

import io.kinoplan.utils.zio.reactivemongo.config.MongoConfig

trait ReactiveMongoApi {
  def driver: AsyncDriver
  def connection: Task[MongoConnection]
  def database: Task[DB]
}

private case class ReactiveMongoApiLive(
  mongoParsedUri: ParsedURIWithDB,
  mongoConnection: MongoConnection,
  mongoDb: DB,
  asyncDriver: AsyncDriver
) extends ReactiveMongoApi
      with IntegrationCheck[Task] {

  override val driver: AsyncDriver = asyncDriver

  override val connection: Task[MongoConnection] = ZIO.succeed(mongoConnection)

  override def database: Task[DB] = ZIO.succeed(mongoDb)

  override def checkAvailability: Task[Boolean] = ZIO.fromFuture(implicit ec => mongoDb.ping())

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
      mongoConnection <- ZIO.fromFuture(_ =>
        asyncDriver.connect(mongoParsedUri, Some(mongoParsedUri.db), strictMode = false)
      )
      mongoDb <- ZIO.fromFuture(implicit ec => mongoConnection.database(mongoParsedUri.db))
    } yield dbName -> ReactiveMongoApiLive(mongoParsedUri, mongoConnection, mongoDb, asyncDriver)
  ).uninterruptible

  private def release(api: ReactiveMongoApi) = (
    for {
      mongoConnection <- api.connection
      _ <- ZIO.fromFuture(_ => mongoConnection.close()(10.seconds))
    } yield ()
  ).orDie.unit

  def make(
    dbName: String
  ): ZLayer[AsyncDriver with MongoConfig, Throwable, (String, ReactiveMongoApi)] = ZLayer.scoped(
    ZIO.acquireRelease(acquire(dbName)) { case (_, api) =>
      release(api)
    }
  )

  def getAt(dbName: String): ZIO[Map[String, ReactiveMongoApi], Throwable, ReactiveMongoApi] = ZIO
    .serviceAt[ReactiveMongoApi](dbName)
    .flatMap(ZIO.fromOption(_))
    .orElseFail(new Throwable(s"The mongodb $dbName key cannot be found in the ZIO environment"))

}
