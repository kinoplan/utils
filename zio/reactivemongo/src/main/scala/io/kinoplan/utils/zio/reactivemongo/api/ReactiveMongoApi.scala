package io.kinoplan.utils.zio.reactivemongo.api

import scala.concurrent.duration.DurationInt

import reactivemongo.api.{AsyncDriver, DB, MongoConnection}
import reactivemongo.api.MongoConnection.ParsedURIWithDB
import zio.{Has, IO, Task, ZIO, ZLayer, ZManaged}

import io.kinoplan.utils.zio.reactivemongo.config.MongoConfig

trait ReactiveMongoApi {
  def driver: AsyncDriver
  def connection: Task[MongoConnection]
  def database: Task[DB]
}

object ReactiveMongoApi {

  final private case class Live(
    mongoParsedUri: ParsedURIWithDB,
    mongoConnection: MongoConnection,
    mongoDb: DB,
    asyncDriver: AsyncDriver
  ) extends ReactiveMongoApi with IntegrationCheck[Task] {

    override val driver: AsyncDriver = asyncDriver

    override val connection: Task[MongoConnection] = Task.succeed(mongoConnection)

    override def database: Task[DB] = Task.succeed(mongoDb)

    override def checkAvailability: Task[Boolean] = Task.fromFuture(implicit ec => mongoDb.ping())

  }

  private def make(
    dbName: String,
    asyncDriver: AsyncDriver,
    mongoConfig: MongoConfig
  ): ZManaged[Any, Throwable, ReactiveMongoApi] = {

    def acquire = for {
      uri <- ZIO.fromOption(mongoConfig.databases.find(_.current(dbName)).map(_.uri)).orElseFail(
        new Throwable(s"mongodb database with name $dbName not found")
      )
      mongoParsedUri <- Task.fromFuture(implicit ec => MongoConnection.fromStringWithDB(uri))
      mongoConnection <- Task.fromFuture(_ =>
        asyncDriver.connect(mongoParsedUri, Some(mongoParsedUri.db), strictMode = false)
      )
      mongoDb <- Task.fromFuture(implicit ec => mongoConnection.database(mongoParsedUri.db))
    } yield Live(mongoParsedUri, mongoConnection, mongoDb, asyncDriver)

    def release(api: ReactiveMongoApi) = (for {
      mongoConnection <- api.connection
      _ <- IO.fromFuture(_ => mongoConnection.close()(10.seconds))
    } yield ()).orDie.unit

    ZManaged.make(acquire)(release)
  }

  def live(
    dbName: String
  ): ZLayer[Has[AsyncDriver] with Has[MongoConfig], Throwable, Has[ReactiveMongoApi]] = ZLayer
    .fromServicesManaged[AsyncDriver, MongoConfig, Any, Throwable, ReactiveMongoApi] {
      (asyncDriver, mongoConfig) => make(dbName, asyncDriver, mongoConfig)
    }

}
