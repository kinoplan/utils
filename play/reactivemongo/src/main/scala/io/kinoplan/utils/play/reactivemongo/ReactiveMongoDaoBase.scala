package io.kinoplan.utils.play.reactivemongo

import scala.concurrent.{ExecutionContext, Future}

import org.scalactic.source.Position
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.{
  BSONDocument,
  BSONDocumentReader,
  BSONDocumentWriter,
  BSONObjectID,
  document
}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.Index

import io.kinoplan.utils.reactivemongo.base.{Queries, QueryBuilderSyntax, SmartIndex}

protected trait ReactiveMongoDao[T] extends QueryBuilderSyntax {
  def collection: Future[BSONCollection]

  def smartEnsureIndexes(smartIndexes: Seq[SmartIndex], drop: Boolean = false)(implicit
    position: Position
  ): Future[Unit]

  def count(selector: Option[BSONDocument] = None, limit: Option[Int] = None, skip: Int = 0)(
    implicit
    position: Position
  ): Future[Long]

  def countGrouped(groupBy: String, matchQuery: BSONDocument = document)(implicit
    position: Position
  ): Future[Map[String, Int]]

  def findAll(implicit
    r: BSONDocumentReader[T],
    position: Position
  ): Future[List[T]]

  def findMany[M <: T](
    selector: BSONDocument = document,
    projection: Option[BSONDocument] = None,
    sort: BSONDocument = document,
    hint: Option[BSONDocument] = None,
    skip: Int = 0,
    limit: Int = -1
  )(implicit
    r: BSONDocumentReader[M],
    position: Position
  ): Future[List[M]]

  def findManyByIds(ids: Set[BSONObjectID])(implicit
    r: BSONDocumentReader[T],
    position: Position
  ): Future[List[T]]

  def findOne(selector: BSONDocument = BSONDocument(), projection: Option[BSONDocument] = None)(
    implicit
    r: BSONDocumentReader[T],
    position: Position
  ): Future[Option[T]]

  def findOneById(id: BSONObjectID)(implicit
    r: BSONDocumentReader[T],
    position: Position
  ): Future[Option[T]]

  def insertMany(values: List[T])(implicit
    w: BSONDocumentWriter[T],
    position: Position
  ): Future[BSONCollection#MultiBulkWriteResult]

  def insertOne(value: T)(implicit
    w: BSONDocumentWriter[T],
    position: Position
  ): Future[WriteResult]

  def update(q: BSONDocument, u: BSONDocument, multi: Boolean = false, upsert: Boolean = false)(
    implicit
    position: Position
  ): Future[BSONCollection#UpdateWriteResult]

  def updateMany(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean))(implicit
    position: Position
  ): Future[BSONCollection#MultiBulkWriteResult]

  def upsert(q: BSONDocument, value: T)(implicit
    w: BSONDocumentWriter[T],
    position: Position
  ): Future[BSONCollection#UpdateWriteResult]

  def saveOne(q: BSONDocument, value: T, multi: Boolean, upsert: Boolean)(implicit
    w: BSONDocumentWriter[T],
    position: Position
  ): Future[BSONCollection#UpdateWriteResult]

  def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
    w: BSONDocumentWriter[T],
    position: Position
  ): Future[BSONCollection#MultiBulkWriteResult]

  def delete(q: BSONDocument)(implicit
    position: Position
  ): Future[WriteResult]

  def deleteByIds(ids: Set[BSONObjectID])(implicit
    ec: ExecutionContext,
    position: Position
  ): Future[WriteResult]

  def deleteById(id: BSONObjectID)(implicit
    ec: ExecutionContext,
    position: Position
  ): Future[WriteResult]

}

abstract class ReactiveMongoDaoBase[T](
  reactiveMongoApi: ReactiveMongoApi,
  collectionName: String,
  diagnostic: Boolean = true
)(implicit
  ec: ExecutionContext
) {

  protected val dao: ReactiveMongoDao[T] = new ReactiveMongoDao[T] {

    def collection: Future[BSONCollection] = reactiveMongoApi
      .database
      .map(_.collection(collectionName))

    def smartEnsureIndexes(smartIndexes: Seq[SmartIndex], drop: Boolean = false)(implicit
      position: Position
    ): Future[Unit] = (
      for {
        coll <- collection
        _ <- createIndexes(coll, smartIndexes)
        _ <-
          if (drop) dropIndexes(coll, smartIndexes)
          else Future.successful(List.empty[Int])
      } yield ()
    ).withDiagnostic

    def count(selector: Option[BSONDocument] = None, limit: Option[Int] = None, skip: Int = 0)(
      implicit
      position: Position
    ): Future[Long] = collection
      .flatMap {
        Queries.countQ(_)(selector, limit, skip)
      }
      .withDiagnostic

    def countGrouped(groupBy: String, matchQuery: BSONDocument = document)(implicit
      position: Position
    ): Future[Map[String, Int]] = collection
      .flatMap {
        Queries.countGroupedQ(_)(groupBy, matchQuery)
      }
      .withDiagnostic

    def findAll(implicit
      r: BSONDocumentReader[T],
      position: Position
    ): Future[List[T]] = findMany()

    def findMany[M <: T](
      selector: BSONDocument = document,
      projection: Option[BSONDocument] = None,
      sort: BSONDocument = document,
      hint: Option[BSONDocument] = None,
      skip: Int = 0,
      limit: Int = -1
    )(implicit
      r: BSONDocumentReader[M],
      position: Position
    ): Future[List[M]] = collection
      .flatMap {
        Queries.findManyQ(_)(selector, projection, sort, hint, skip, limit)
      }
      .withDiagnostic

    def findManyByIds(ids: Set[BSONObjectID])(implicit
      r: BSONDocumentReader[T],
      position: Position
    ): Future[List[T]] = dao.findMany(BSONDocument("_id" -> BSONDocument("$in" -> ids)))

    def findOne(selector: BSONDocument = BSONDocument(), projection: Option[BSONDocument] = None)(
      implicit
      r: BSONDocumentReader[T],
      position: Position
    ): Future[Option[T]] = collection
      .flatMap {
        Queries.findOneQ(_)(selector, projection)
      }
      .withDiagnostic

    def findOneById(id: BSONObjectID)(implicit
      r: BSONDocumentReader[T],
      position: Position
    ): Future[Option[T]] = dao.findOne(BSONDocument("_id" -> id))

    def insertMany(values: List[T])(implicit
      w: BSONDocumentWriter[T],
      position: Position
    ): Future[BSONCollection#MultiBulkWriteResult] = dao
      .collection
      .flatMap {
        Queries.insertManyQ(_)(values)
      }
      .withDiagnostic

    def insertOne(value: T)(implicit
      w: BSONDocumentWriter[T],
      position: Position
    ): Future[WriteResult] = dao
      .collection
      .flatMap {
        Queries.insertOneQ(_)(value)
      }
      .withDiagnostic

    def update(q: BSONDocument, u: BSONDocument, multi: Boolean = false, upsert: Boolean = false)(
      implicit
      position: Position
    ): Future[BSONCollection#UpdateWriteResult] = collection
      .flatMap {
        Queries.updateQ(_)(q, u, multi = multi, upsert = upsert)
      }
      .withDiagnostic

    def updateMany(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean))(implicit
      position: Position
    ): Future[BSONCollection#MultiBulkWriteResult] = collection
      .flatMap {
        Queries.updateManyQ(_)(values, f)
      }
      .withDiagnostic

    def upsert(q: BSONDocument, value: T)(implicit
      w: BSONDocumentWriter[T],
      position: Position
    ): Future[BSONCollection#UpdateWriteResult] = collection
      .flatMap {
        Queries.upsertQ(_)(q, value)
      }
      .withDiagnostic

    def saveOne(q: BSONDocument, value: T, multi: Boolean, upsert: Boolean)(implicit
      w: BSONDocumentWriter[T],
      position: Position
    ): Future[BSONCollection#UpdateWriteResult] = collection
      .flatMap {
        Queries.saveQ(_)(q, value, multi = multi, upsert = upsert)
      }
      .withDiagnostic

    def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
      w: BSONDocumentWriter[T],
      position: Position
    ): Future[BSONCollection#MultiBulkWriteResult] = collection
      .flatMap {
        Queries.saveManyQ(_)(values, f)
      }
      .withDiagnostic

    def delete(q: BSONDocument)(implicit
      position: Position
    ): Future[WriteResult] = collection
      .flatMap {
        Queries.deleteQ(_)(q)
      }
      .withDiagnostic

    def deleteByIds(ids: Set[BSONObjectID])(implicit
      ec: ExecutionContext,
      position: Position
    ): Future[WriteResult] = dao
      .collection
      .flatMap {
        Queries.deleteByIdsQ(_)(ids)
      }
      .withDiagnostic

    def deleteById(id: BSONObjectID)(implicit
      ec: ExecutionContext,
      position: Position
    ): Future[WriteResult] = dao
      .collection
      .flatMap {
        Queries.deleteByIdQ(_)(id)
      }
      .withDiagnostic

  }

  def findAll(implicit
    r: BSONDocumentReader[T],
    position: Position
  ): Future[List[T]] = dao.findAll

  def findManyByIds(ids: Set[BSONObjectID])(implicit
    r: BSONDocumentReader[T],
    position: Position
  ): Future[List[T]] = dao.findManyByIds(ids)

  def findOneById(id: BSONObjectID)(implicit
    r: BSONDocumentReader[T],
    position: Position
  ): Future[Option[T]] = dao.findOneById(id)

  def insertMany(values: List[T])(implicit
    w: BSONDocumentWriter[T],
    position: Position
  ): Future[BSONCollection#MultiBulkWriteResult] = dao.insertMany(values)

  def insertOne(value: T)(implicit
    w: BSONDocumentWriter[T],
    position: Position
  ): Future[WriteResult] = dao.insertOne(value)

  def deleteByIds(ids: Set[BSONObjectID])(implicit
    ec: ExecutionContext,
    position: Position
  ): Future[WriteResult] = dao.deleteByIds(ids)

  def deleteById(id: BSONObjectID)(implicit
    ec: ExecutionContext,
    position: Position
  ): Future[WriteResult] = dao.deleteById(id)

  private def createIndexes(
    coll: BSONCollection,
    smartIndexes: Seq[SmartIndex]
  ): Future[Seq[Boolean]] = Future.sequence(
    smartIndexes.map { smartIndex =>
      coll
        .indexesManager
        .ensure(
          Index(
            key = smartIndex.key.toSeq,
            unique = smartIndex.unique,
            background = smartIndex.background
          )
        )
    }
  )

  private def dropIndexes(coll: BSONCollection, smartIndexes: Seq[SmartIndex]): Future[List[Int]] =
    coll
      .indexesManager
      .list()
      .flatMap { indexes =>
        Future.sequence(
          indexes
            .filterNot(index =>
              index.unique || smartIndexes.exists(_.key == index.key.toSet) ||
              index.name.contains("_id_")
            )
            .flatMap(_.name)
            .map { indexName =>
              coll.indexesManager.drop(indexName)
            }
        )
      }

  implicit protected class FutureSyntax[A](future: Future[A]) {

    def withDiagnostic(implicit
      position: Position
    ): Future[A] =
      if (diagnostic) ensureDiagnostic
      else future

    def ensureDiagnostic(implicit
      position: Position
    ): Future[A] = future.recoverWith { case ex: Throwable =>
      val diagnosticInfo = s"At (${position.fileName}:${position.lineNumber})"
      if (ex.getMessage.startsWith(diagnosticInfo)) Future.failed(ex)
      else Future.failed(new Throwable(diagnosticInfo + " " + ex.getMessage, ex))
    }

  }

}
