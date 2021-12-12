package io.kinoplan.utils.zio.reactivemongo.daos

import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.{Index, IndexType}
import zio.{Task, ZIO}

import io.kinoplan.utils.zio.reactivemongo.api.ReactiveMongoApi
import io.kinoplan.utils.zio.reactivemongo.utils.Queries._
import io.kinoplan.utils.zio.reactivemongo.utils.syntax.ReactiveMongoSyntax

abstract class ReactiveMongoDaoBase[T](reactiveMongoApi: ReactiveMongoApi, collectionName: String)
    extends ReactiveMongoSyntax {

  val collection: Task[BSONCollection] = reactiveMongoApi.database.map(_.collection(collectionName))

  protected def smartEnsureIndexes(ensuredKeys: Seq[Set[(String, IndexType)]]): Unit = zio.Runtime
    .default.unsafeRunAsync_(
      for {
        coll <- collection
        _ <- createIndexes(coll, ensuredKeys)
        _ <- dropIndexes(coll, ensuredKeys)
      } yield ()
    )

  def findMany[M <: T](
    selector: BSONDocument = BSONDocument(),
    projection: Option[BSONDocument] = None
  )(implicit
    r: BSONDocumentReader[M]
  ): Task[List[M]] = for {
    coll <- collection
    result <- Task.fromFuture(implicit ec => findManyQ[M](coll)(selector, projection))
  } yield result

  def findOne(selector: BSONDocument = BSONDocument(), projection: Option[BSONDocument] = None)(
    implicit
    r: BSONDocumentReader[T]
  ): Task[Option[T]] = for {
    coll <- collection
    result <- Task.fromFuture(implicit ec => findOneQ[T](coll)(selector, projection))
  } yield result

  def findOneById(id: BSONObjectID)(implicit
    r: BSONDocumentReader[T]
  ): Task[Option[T]] = findOne(BSONDocument("_id" -> id))

  def findManyByIds(ids: List[BSONObjectID])(implicit
    r: BSONDocumentReader[T]
  ): Task[List[T]] = findMany(BSONDocument("_id" -> BSONDocument("$in" -> ids)))

  def insertMany(values: List[T])(implicit
    w: BSONDocumentWriter[T]
  ): Task[BSONCollection#MultiBulkWriteResult] = for {
    coll <- collection
    result <- Task.fromFuture(implicit ec => insertManyQ(coll)(values))
  } yield result

  def insertOne(value: T)(implicit
    w: BSONDocumentWriter[T]
  ): Task[WriteResult] = for {
    coll <- collection
    result <- Task.fromFuture(implicit ec => insertOneQ(coll)(value))
  } yield result

  def update(
    q: BSONDocument,
    u: BSONDocument,
    multi: Boolean = false,
    upsert: Boolean = false
  ): Task[BSONCollection#UpdateWriteResult] = for {
    coll <- collection
    result <- Task.fromFuture(implicit ec => updateQ(coll)(q, u, multi, upsert))
  } yield result

  def updateMany(
    values: List[T],
    f: T => (BSONDocument, BSONDocument, Boolean, Boolean)
  ): Task[BSONCollection#MultiBulkWriteResult] = for {
    coll <- collection
    result <- Task.fromFuture(implicit ec => updateManyQ(coll)(values, f))
  } yield result

  def saveOne(q: BSONDocument, value: T, multi: Boolean, upsert: Boolean)(implicit
    w: BSONDocumentWriter[T]
  ): Task[BSONCollection#UpdateWriteResult] = for {
    coll <- collection
    result <- Task.fromFuture(implicit ec => saveQ(coll)(q, value, multi, upsert))
  } yield result

  def saveOneWithoutId(q: BSONDocument, value: T)(implicit
    w: BSONDocumentWriter[T]
  ): Task[BSONCollection#UpdateWriteResult] = for {
    coll <- collection
    result <- Task.fromFuture(implicit ec => saveWithoutIdQ(coll)(q, value))
  } yield result

  def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
    w: BSONDocumentWriter[T]
  ): Task[BSONCollection#MultiBulkWriteResult] = for {
    coll <- collection
    result <- Task.fromFuture(implicit ec => saveManyQ(coll)(values, f))
  } yield result

  def removeOne(q: BSONDocument): Task[WriteResult] = for {
    coll <- collection
    result <- Task.fromFuture(implicit ec => removeOneQ(coll)(q))
  } yield result

  private def createIndexes(
    coll: BSONCollection,
    ensuredKeys: Seq[Set[(String, IndexType)]]
  ): Task[Seq[Boolean]] = ZIO.foreach(ensuredKeys)(key =>
    Task.fromFuture(implicit ec =>
      coll.indexesManager.ensure(Index(key = key.toSeq, unique = false, background = true))
    )
  )

  private def dropIndexes(
    coll: BSONCollection,
    ensuredKeys: Seq[Set[(String, IndexType)]]
  ): Task[List[Int]] = for {
    indexes <- Task.fromFuture(implicit ec => coll.indexesManager.list())
    filteredIndexNames = indexes.filterNot(index =>
      index.unique || ensuredKeys.contains(index.key.toSet) || index.name.contains("_id_")
    ).flatMap(_.name)
    result <- ZIO.foreach(filteredIndexNames)(indexName =>
      Task.fromFuture(implicit ec => coll.indexesManager.drop(indexName))
    )
  } yield result

}
