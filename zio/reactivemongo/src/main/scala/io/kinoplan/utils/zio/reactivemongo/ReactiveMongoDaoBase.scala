package io.kinoplan.utils.zio.reactivemongo

import reactivemongo.api._
import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.collection.BSONSerializationPack.NarrowValueReader
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.Index
import zio.{Task, Unsafe, ZIO}

import io.kinoplan.utils.reactivemongo.base._
import io.kinoplan.utils.zio.reactivemongo.metrics.ReactiveMongoMetric._

abstract class ReactiveMongoDaoBase[T](
  reactiveMongoApi: ReactiveMongoApi,
  collectionName: String,
  autoCommentQueries: Boolean = true,
  failoverStrategyO: Option[FailoverStrategy] = None,
  readPreferenceO: Option[ReadPreference] = None
) extends BsonNoneAsNullProducer
      with BsonDocumentSyntax {

  protected object dao {

    def collection: Task[BSONCollection] = reactiveMongoApi
      .database
      .map(db =>
        failoverStrategyO.fold(db.collection(collectionName))(db.collection(collectionName, _))
      )

    private def operations = ReactiveMongoOperations[T](collection)

    def smartEnsureIndexes(smartIndexes: Seq[SmartIndex], clearDiff: Boolean = false): Unit = Unsafe
      .unsafe { implicit unsafe =>
        zio
          .Runtime
          .default
          .unsafe
          .fork(
            (
              for {
                coll <- collection
                _ <- dropIndexes(coll, smartIndexes).when(clearDiff).unit
                _ <- createIndexes(coll, smartIndexes)
              } yield ()
            ).tapError(ex =>
              ZIO.logError(
                s"Failure ensure for $collectionName indexes $smartIndexes with clearDiff=$clearDiff: $ex"
              )
            )
          )
          .unsafe
          .addObserver(_ => ())
      }

    def count(
      selector: Option[BSONDocument] = None,
      limit: Option[Int] = None,
      skip: Int = 0,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = None
    ): Task[Long] = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec =>
        Queries.countQ(coll)(selector, limit, skip, readConcern, readPreference)
      ) @@ aspect.commandQueryTimer(coll) @@ aspect.commandQueriesCounter(coll)
    } yield result

    def countGrouped(
      groupBy: String,
      matchQuery: BSONDocument = document,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = None
    ): Task[Map[String, Int]] = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec =>
        Queries.countGroupedQ(coll)(groupBy, matchQuery, readConcern, readPreference)
      ) @@ aspect.commandQueryTimer(coll) @@ aspect.commandQueriesCounter(coll) @@
        aspect.commandReceivedDocumentsCounter[Map[String, Int]](coll)(_.size.toLong)
    } yield result

    def distinct[R](
      key: String,
      selector: Option[BSONDocument] = None,
      readConcern: Option[ReadConcern] = None,
      collation: Option[Collation] = None
    )(implicit
      reader: NarrowValueReader[R]
    ): ZIO[Any, Throwable, Set[R]] = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec =>
        Queries.distinctQ[R](coll)(key, selector, readConcern, collation)
      ) @@ aspect.commandQueryTimer(coll) @@ aspect.commandQueriesCounter(coll) @@
        aspect.commandReceivedDocumentsCounter[Set[R]](coll)(_.size.toLong)
    } yield result

    def findAll(
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
    )(implicit
      r: BSONDocumentReader[T],
      enclosing: sourcecode.Enclosing
    ): Task[List[T]] = findMany(readConcern = readConcern, readPreference = readPreference)

    def findMany[M <: T](
      selector: BSONDocument = document,
      projection: Option[BSONDocument] = None,
      sort: BSONDocument = document,
      hint: Option[BSONDocument] = None,
      skip: Int = 0,
      limit: Int = -1,
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
    )(implicit
      r: BSONDocumentReader[M],
      enclosing: sourcecode.Enclosing
    ): Task[List[M]] = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec =>
        Queries.findManyQ[M](coll)(
          selector,
          projection,
          sort,
          hint,
          skip,
          limit,
          readConcern,
          readPreference,
          withQueryComment
        )
      ) @@ aspect.findQueryTimer(coll) @@ aspect.findQueriesCounter(coll) @@
        aspect.findReceivedDocumentsCounter[List[T]](coll)(_.length.toLong)
    } yield result

    def findManyC[M <: T](
      selector: BSONDocument = document,
      projection: Option[BSONDocument] = None,
      sort: BSONDocument = document,
      batchSize: Int = 0,
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
    )(implicit
      r: BSONDocumentReader[M],
      enclosing: sourcecode.Enclosing,
      cursorProducer: CursorProducer[M]
    ): Task[cursorProducer.ProducedCursor] = collection.flatMap(coll =>
      ZIO.succeed(
        Queries.findManyCursorQ[M](coll)(
          selector,
          projection,
          sort,
          batchSize,
          readConcern,
          readPreference,
          withQueryComment
        )(r, cursorProducer)
      ) @@ aspect.findQueriesCounter(coll)
    )

    def findManyByIds(
      ids: Set[BSONObjectID],
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
    )(implicit
      r: BSONDocumentReader[T],
      enclosing: sourcecode.Enclosing
    ): Task[List[T]] = findMany(
      BSONDocument("_id" -> BSONDocument("$in" -> ids)),
      readConcern = readConcern,
      readPreference = readPreference
    )

    def findOne(
      selector: BSONDocument = BSONDocument(),
      projection: Option[BSONDocument] = None,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = readPreferenceO
    )(implicit
      r: BSONDocumentReader[T],
      enclosing: sourcecode.Enclosing
    ): Task[Option[T]] = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec =>
        Queries
          .findOneQ[T](coll)(selector, projection, readConcern, readPreference, withQueryComment)
      ) @@ aspect.findQueryTimer(coll) @@ aspect.findQueriesCounter(coll) @@
        aspect.findReceivedDocumentsCounter[Option[T]](coll)(opt =>
          if (opt.isEmpty) 0L
          else 1L
        )
    } yield result

    def findOneById(
      id: BSONObjectID,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = readPreferenceO
    )(implicit
      r: BSONDocumentReader[T],
      enclosing: sourcecode.Enclosing
    ): Task[Option[T]] =
      findOne(BSONDocument("_id" -> id), readConcern = readConcern, readPreference = readPreference)

    def insertMany(values: List[T])(implicit
      w: BSONDocumentWriter[T]
    ) = operations.insertMany(values)

    def insertOne(value: T)(implicit
      w: BSONDocumentWriter[T]
    ) = operations.insertOne(value)

    def update(
      q: BSONDocument,
      u: BSONDocument,
      multi: Boolean = false,
      upsert: Boolean = false,
      arrayFilters: Seq[BSONDocument] = Seq.empty[BSONDocument]
    ) = operations.update(q, u, multi = multi, upsert = upsert, arrayFilters = arrayFilters)

    def updateMany(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean)) =
      operations.updateMany(values, f)

    def upsert(q: BSONDocument, value: T)(implicit
      w: BSONDocumentWriter[T]
    ) = operations.upsert(q, value)

    def saveOne(q: BSONDocument, value: T, multi: Boolean = false, upsert: Boolean = true)(implicit
      w: BSONDocumentWriter[T]
    ) = operations.saveOne(q, value, multi = multi, upsert = upsert)

    def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
      w: BSONDocumentWriter[T]
    ) = operations.saveMany(values, f)

    def delete(q: BSONDocument) = operations.delete(q)

    def deleteByIds(ids: Set[BSONObjectID]) = operations.deleteByIds(ids)

    def deleteById(id: BSONObjectID) = operations.deleteById(id)

    private def createIndexes(coll: BSONCollection, smartIndexes: Seq[SmartIndex]): Task[Unit] = ZIO
      .foreach(smartIndexes)(smartIndex =>
        ZIO.fromFuture(implicit ec =>
          coll
            .indexesManager
            .ensure(
              Index(
                key = smartIndex.key,
                name = smartIndex.name,
                unique = smartIndex.unique,
                background = smartIndex.background,
                partialFilter = smartIndex.partialFilter,
                expireAfterSeconds = smartIndex.expireAfterSeconds
              )
            )
        )
      )
      .unit

    private def dropIndexes(coll: BSONCollection, smartIndexes: Seq[SmartIndex]): Task[Unit] = for {
      indexes <- ZIO.fromFuture(implicit ec => coll.indexesManager.list())
      filteredIndexNames = indexes
        .filterNot(index =>
          smartIndexes.exists(smartIndex =>
            smartIndex.key == index.key &&
            ((smartIndex.name.nonEmpty && smartIndex.name.exists(index.name.contains(_))) ||
            smartIndex.name.isEmpty)
          ) || index.name.contains("_id_")
        )
        .flatMap(_.name)
      _ <- ZIO
        .logInfo(
          s"Index names in collection ${coll.name} to drop ${filteredIndexNames.mkString(",")}"
        )
        .when(filteredIndexNames.nonEmpty)
      _ <- ZIO.foreachDiscard(filteredIndexNames)(indexName =>
        ZIO.fromFuture(implicit ec => coll.indexesManager.drop(indexName))
      )
    } yield ()

    private def withQueryComment(implicit
      enclosing: sourcecode.Enclosing
    ): Option[String] =
      if (autoCommentQueries) Some(QueryComment.make)
      else None

  }

  implicit protected class WriteResultTaskOps[A <: WriteResult](task: Task[A]) {

    def adaptError: Task[A] = task.flatMap { result =>
      if (result.writeErrors.isEmpty) ZIO.succeed(result)
      else ZIO.fail(new Throwable(result.writeErrors.map(_.errmsg).mkString(", ")))
    }

  }

}
