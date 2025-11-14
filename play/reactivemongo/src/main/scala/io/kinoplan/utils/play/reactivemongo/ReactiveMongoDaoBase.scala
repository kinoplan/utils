package io.kinoplan.utils.play.reactivemongo

import scala.concurrent.{ExecutionContext, Future}

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api._
import reactivemongo.api.bson.{
  BSONDocument,
  BSONDocumentReader,
  BSONDocumentWriter,
  BSONObjectID,
  document
}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.collection.BSONSerializationPack.NarrowValueReader

import io.kinoplan.utils.reactivemongo.base._

abstract class ReactiveMongoDaoBase[T](
  reactiveMongoApi: ReactiveMongoApi,
  collectionName: String,
  diagnostic: Boolean = true,
  autoCommentQueries: Boolean = true,
  failoverStrategyO: Option[FailoverStrategy] = None,
  readPreferenceO: Option[ReadPreference] = None
)(implicit
  ec: ExecutionContext
) extends BsonNoneAsNullProducer
      with BsonDocumentSyntax {

  protected object dao {

    def collection: Future[BSONCollection] = reactiveMongoApi
      .database
      .map(db =>
        failoverStrategyO.fold(db.collection(collectionName))(db.collection(collectionName, _))
      )

    private def operations = ReactiveMongoOperations[T](collection)

    def smartEnsureIndexes(smartIndexes: Seq[SmartIndex], clearDiff: Boolean = false)(implicit
      enclosing: sourcecode.Enclosing
    ): Future[Unit] = (
      for {
        coll <- collection
        _ <-
          if (clearDiff) dropIndexes(coll, smartIndexes)
          else Future.successful(List.empty[Int])
        _ <- createIndexes(coll, smartIndexes)
      } yield ()
    ).withDiagnostic

    def count(
      selector: Option[BSONDocument] = None,
      limit: Option[Int] = None,
      skip: Int = 0,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = None
    )(implicit
      enclosing: sourcecode.Enclosing
    ): Future[Long] = collection
      .flatMap { coll =>
        Queries.countQ(coll)(selector, limit, skip, readConcern, readPreference, withQueryComment)
      }
      .withDiagnostic

    def countGrouped(
      groupBy: String,
      matchQuery: BSONDocument = document,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = None
    )(implicit
      enclosing: sourcecode.Enclosing
    ): Future[Map[String, Int]] = collection
      .flatMap { coll =>
        Queries.countGroupedQ(coll)(
          groupBy,
          matchQuery,
          readConcern,
          readPreference,
          withQueryComment
        )
      }
      .withDiagnostic

    def distinct[R](
      key: String,
      selector: Option[BSONDocument] = None,
      readConcern: Option[ReadConcern] = None,
      collation: Option[Collation] = None
    )(implicit
      reader: NarrowValueReader[R],
      enclosing: sourcecode.Enclosing
    ): Future[Set[R]] = collection
      .flatMap { coll =>
        Queries.distinctQ[R](coll)(key, selector, readConcern, collation, withQueryComment)
      }
      .withDiagnostic

    def findAll(
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
    )(implicit
      r: BSONDocumentReader[T],
      enclosing: sourcecode.Enclosing
    ): Future[List[T]] = findMany(readConcern = readConcern, readPreference = readPreference)

    def findMany[M <: T](
      selector: BSONDocument = document,
      projection: Option[BSONDocument] = None,
      sort: BSONDocument = document,
      hint: Option[BSONDocument] = None,
      skip: Int = 0,
      limit: Int = -1,
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred),
      collation: Option[Collation] = None
    )(implicit
      r: BSONDocumentReader[M],
      enclosing: sourcecode.Enclosing
    ): Future[List[M]] = collection
      .flatMap { coll =>
        Queries.findManyQ(coll)(
          selector,
          projection,
          sort,
          hint,
          skip,
          limit,
          readConcern,
          readPreference,
          collation,
          withQueryComment
        )

      }
      .withDiagnostic

    def findManyByIds(
      ids: Set[BSONObjectID],
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
    )(implicit
      r: BSONDocumentReader[T],
      enclosing: sourcecode.Enclosing
    ): Future[List[T]] = findMany(
      BSONDocument("_id" -> BSONDocument("$in" -> ids)),
      readConcern = readConcern,
      readPreference = readPreference
    )

    def findManyC[M <: T](
      selector: BSONDocument = document,
      projection: Option[BSONDocument] = None,
      sort: BSONDocument = document,
      batchSize: Int = 0,
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred),
      collation: Option[Collation] = None
    )(implicit
      r: BSONDocumentReader[M],
      enclosing: sourcecode.Enclosing,
      cursorProducer: CursorProducer[M]
    ): Future[cursorProducer.ProducedCursor] = collection
      .map { coll =>
        Queries.findManyCursorQ(coll)(
          selector,
          projection,
          sort,
          batchSize,
          readConcern,
          readPreference,
          collation,
          withQueryComment
        )(r, cursorProducer)

      }
      .withDiagnostic

    def findOne(
      selector: BSONDocument = BSONDocument(),
      projection: Option[BSONDocument] = None,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = readPreferenceO,
      collation: Option[Collation] = None
    )(implicit
      r: BSONDocumentReader[T],
      enclosing: sourcecode.Enclosing
    ): Future[Option[T]] = collection
      .flatMap { coll =>
        Queries.findOneQ(coll)(
          selector,
          projection,
          readConcern,
          readPreference,
          collation,
          withQueryComment
        )

      }
      .withDiagnostic

    def findOneById(
      id: BSONObjectID,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = readPreferenceO
    )(implicit
      r: BSONDocumentReader[T],
      enclosing: sourcecode.Enclosing
    ): Future[Option[T]] =
      findOne(BSONDocument("_id" -> id), readConcern = readConcern, readPreference = readPreference)

    def insertMany(values: List[T])(implicit
      w: BSONDocumentWriter[T],
      enclosing: sourcecode.Enclosing
    ) = operations.insertMany(values).withDiagnostic

    def insertOne(value: T)(implicit
      w: BSONDocumentWriter[T],
      enclosing: sourcecode.Enclosing
    ) = operations.insertOne(value).withDiagnostic

    def update(q: BSONDocument, u: BSONDocument, multi: Boolean = false, upsert: Boolean = false)(
      implicit
      enclosing: sourcecode.Enclosing
    ) = operations.update(q, u, multi = multi, upsert = upsert).withDiagnostic

    def updateMany(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean))(implicit
      enclosing: sourcecode.Enclosing
    ) = operations.updateMany(values, f).withDiagnostic

    def upsert(q: BSONDocument, value: T)(implicit
      w: BSONDocumentWriter[T],
      enclosing: sourcecode.Enclosing
    ) = operations.upsert(q, value).withDiagnostic

    def saveOne(q: BSONDocument, value: T, multi: Boolean = false, upsert: Boolean = true)(implicit
      w: BSONDocumentWriter[T],
      enclosing: sourcecode.Enclosing
    ) = operations.saveOne(q, value, multi = multi, upsert = upsert).withDiagnostic

    def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
      w: BSONDocumentWriter[T],
      enclosing: sourcecode.Enclosing
    ) = operations.saveMany(values, f).withDiagnostic

    def delete(q: BSONDocument)(implicit
      enclosing: sourcecode.Enclosing
    ) = operations.delete(q).withDiagnostic

    def deleteByIds(ids: Set[BSONObjectID])(implicit
      ec: ExecutionContext,
      enclosing: sourcecode.Enclosing
    ) = operations.deleteByIds(ids).withDiagnostic

    def deleteById(id: BSONObjectID)(implicit
      ec: ExecutionContext,
      enclosing: sourcecode.Enclosing
    ) = operations.deleteById(id).withDiagnostic

    private def withQueryComment(implicit
      enclosing: sourcecode.Enclosing
    ): Option[String] =
      if (autoCommentQueries) Some(QueryComment.make)
      else None

  }

  private def createIndexes(
    coll: BSONCollection,
    smartIndexes: Seq[SmartIndex]
  ): Future[Seq[Boolean]] =
    Future.sequence(smartIndexes.map(_.toIndex).map(coll.indexesManager.ensure))

  private def dropIndexes(coll: BSONCollection, smartIndexes: Seq[SmartIndex]): Future[List[Int]] =
    coll
      .indexesManager
      .list()
      .flatMap { indexes =>
        val incomingIndexes = smartIndexes.map(_.toIndex)

        Future.sequence(
          indexes
            .filterNot(index =>
              index.name.contains("_id_") ||
              incomingIndexes.exists(_.eventualName == index.eventualName)
            )
            .flatMap(_.name)
            .map(coll.indexesManager.drop)
        )
      }

  implicit protected class FutureSyntax[A](future: Future[A]) {

    def withDiagnostic(implicit
      enclosing: sourcecode.Enclosing
    ): Future[A] =
      if (diagnostic) ensureDiagnostic
      else future

    def ensureDiagnostic(implicit
      enclosing: sourcecode.Enclosing
    ): Future[A] = future.recoverWith { case ex: Throwable =>
      val diagnosticInfo = s"At (${enclosing.value})"
      if (ex.getMessage != null && ex.getMessage.startsWith(diagnosticInfo)) Future.failed(ex)
      else Future.failed(new Throwable(diagnosticInfo + " " + ex.getMessage, ex))
    }

  }

}
