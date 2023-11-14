package io.kinoplan.utils.zio.reactivemongo

import reactivemongo.api.{FailoverStrategy, ReadConcern, ReadPreference}
import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.Index
import zio.{Task, Unsafe, ZIO}

import io.kinoplan.utils.reactivemongo.base.{
  BsonNoneAsNullProducer,
  Queries,
  QueryComment,
  SmartIndex
}

abstract class ReactiveMongoDaoBase[T](
  reactiveMongoApi: ReactiveMongoApi,
  collectionName: String,
  autoCommentQueries: Boolean = true,
  failoverStrategyO: Option[FailoverStrategy] = None,
  readPreferenceO: Option[ReadPreference] = None
) extends BsonNoneAsNullProducer {

  protected object dao {

    def collection: Task[BSONCollection] = reactiveMongoApi
      .database
      .map(db =>
        failoverStrategyO.fold(db.collection(collectionName))(db.collection(collectionName, _))
      )

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
      )
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
      )
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
      )
    } yield result

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
    ) = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec => Queries.insertManyQ(coll)(values))
    } yield result

    def insertOne(value: T)(implicit
      w: BSONDocumentWriter[T]
    ) = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec => Queries.insertOneQ(coll)(value))
    } yield result

    def update(
      q: BSONDocument,
      u: BSONDocument,
      multi: Boolean = false,
      upsert: Boolean = false,
      arrayFilters: Seq[BSONDocument] = Seq.empty[BSONDocument]
    ) = for {
      coll <- collection
      result <- ZIO
        .fromFuture(implicit ec => Queries.updateQ(coll)(q, u, multi, upsert, arrayFilters))
    } yield result

    def updateMany(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean)) = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec => Queries.updateManyQ(coll)(values, f))
    } yield result

    def upsert(q: BSONDocument, value: T)(implicit
      w: BSONDocumentWriter[T]
    ) = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec => Queries.upsertQ(coll)(q, value))
    } yield result

    def saveOne(q: BSONDocument, value: T, multi: Boolean = false, upsert: Boolean = true)(implicit
      w: BSONDocumentWriter[T]
    ) = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec => Queries.saveQ(coll)(q, value, multi, upsert))
    } yield result

    def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
      w: BSONDocumentWriter[T]
    ) = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec => Queries.saveManyQ(coll)(values, f))
    } yield result

    def delete(q: BSONDocument) = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec => Queries.deleteQ(coll)(q))
    } yield result

    def deleteByIds(ids: Set[BSONObjectID]) = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec => Queries.deleteByIdsQ(coll)(ids))
    } yield result

    def deleteById(id: BSONObjectID) = for {
      coll <- collection
      result <- ZIO.fromFuture(implicit ec => Queries.deleteByIdQ(coll)(id))
    } yield result

    private def createIndexes(coll: BSONCollection, smartIndexes: Seq[SmartIndex]): Task[Unit] = ZIO
      .foreach(smartIndexes)(smartIndex =>
        ZIO.fromFuture(implicit ec =>
          coll
            .indexesManager
            .ensure(
              Index(
                key = smartIndex.key.toSeq,
                name = smartIndex.name,
                unique = smartIndex.unique,
                background = smartIndex.background,
                partialFilter = smartIndex.partialFilter
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
            smartIndex.key == index.key.toSet &&
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

    def getQueryComment(implicit
      enclosing: sourcecode.Enclosing
    ): String = QueryComment.make(enclosing)

    private def withQueryComment(implicit
      enclosing: sourcecode.Enclosing
    ): Option[String] =
      if (autoCommentQueries) Some(getQueryComment)
      else None

  }

  implicit protected class WriteResultTaskOps[A <: WriteResult](task: Task[A]) {

    def adaptError: Task[A] = task.flatMap { result =>
      if (result.writeErrors.isEmpty) ZIO.succeed(result)
      else ZIO.fail(new Throwable(result.writeErrors.map(_.errmsg).mkString(", ")))
    }

  }

}
