package io.kinoplan.utils.play.reactivemongo

import scala.concurrent.{ExecutionContext, Future}

import kamon.Kamon
import kamon.trace.Span
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api._
import reactivemongo.api.bson.{
  BSONDocument,
  BSONDocumentReader,
  BSONDocumentWriter,
  BSONObjectID,
  document
}
import reactivemongo.api.bson.BSONDocument.pretty
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.collection.BSONSerializationPack.NarrowValueReader
import reactivemongo.api.indexes.Index

import io.kinoplan.utils.play.reactivemongo.KamonSupport.CommandType
import io.kinoplan.utils.reactivemongo.base._

abstract class ReactiveMongoDaoBase[T](
  reactiveMongoApi: ReactiveMongoApi,
  collectionName: String,
  diagnostic: Boolean = true,
  autoCommentQueries: Boolean = true,
  kamonEnabled: Boolean = true,
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

    private def operations = ReactiveMongoOperations[T](collection, kamonEnabled)

    def smartEnsureIndexes(smartIndexes: Seq[SmartIndex], drop: Boolean = false)(implicit
      enclosing: sourcecode.Enclosing
    ): Future[Unit] = (
      for {
        coll <- collection
        _ <-
          if (drop) dropIndexes(coll, smartIndexes)
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
        Queries
          .countQ(coll)(selector, limit, skip, readConcern, readPreference)
          .withKamon(
            coll,
            CommandType.COMMAND,
            before = Some { span =>
              span.tag("skip", skip.toLong)
              selector.foreach(s => span.tag("selector", pretty(s)))
              limit.foreach(l => span.tag("limit", l.toLong))
            }
          )
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
        Queries
          .countGroupedQ(coll)(groupBy, matchQuery, readConcern, readPreference)
          .withKamon(
            coll,
            CommandType.COMMAND,
            before = Some { span =>
              span.tag("groupBy", groupBy).tag("matchQuery", pretty(matchQuery))
              ()
            }
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
    ): Future[Set[R]] = collection.flatMap { coll =>
      Queries
        .distinctQ[R](coll)(key, selector, readConcern, collation)
        .withKamon(
          coll,
          CommandType.COMMAND,
          before = Some { span =>
            span.tag("key", key)
            selector.foreach(s => span.tag("selector", pretty(s)))
          }
        )
    }

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
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
    )(implicit
      r: BSONDocumentReader[M],
      enclosing: sourcecode.Enclosing
    ): Future[List[M]] = collection
      .flatMap { coll =>
        Queries
          .findManyQ(coll)(
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
          .withKamon(
            coll,
            CommandType.FIND,
            before = Some { span =>
              span
                .tag("selector", pretty(selector))
                .tag("sort", pretty(sort))
                .tag("skip", skip.toLong)
                .tag("limit", limit.toLong)

              projection.foreach(pr => span.tag("projection", pretty(pr)))
            }
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
      readPreference: ReadPreference = readPreferenceO.getOrElse(ReadPreference.secondaryPreferred)
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
          withQueryComment
        )(r, cursorProducer)
      }
      .withDiagnostic

    def findOne(
      selector: BSONDocument = BSONDocument(),
      projection: Option[BSONDocument] = None,
      readConcern: Option[ReadConcern] = None,
      readPreference: Option[ReadPreference] = readPreferenceO
    )(implicit
      r: BSONDocumentReader[T],
      enclosing: sourcecode.Enclosing
    ): Future[Option[T]] = collection
      .flatMap { coll =>
        Queries
          .findOneQ(coll)(selector, projection, readConcern, readPreference, withQueryComment)
          .withKamon(
            coll,
            CommandType.FIND,
            before = Some { span =>
              span.tag("selector", pretty(selector))
              projection.foreach(pr => span.tag("projection", pretty(pr)))
            }
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
  ): Future[Seq[Boolean]] = Future.sequence(
    smartIndexes.map { smartIndex =>
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
              smartIndexes.exists(smartIndex =>
                smartIndex.key == index.key &&
                ((smartIndex.name.nonEmpty && smartIndex.name.exists(index.name.contains(_))) ||
                smartIndex.name.isEmpty)
              ) || index.name.contains("_id_")
            )
            .flatMap(_.name)
            .map { indexName =>
              coll.indexesManager.drop(indexName)
            }
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

    def withKamon(
      collection: BSONCollection,
      commandType: String,
      before: Option[Span => Unit] = None
    )(implicit
      enclosing: sourcecode.Enclosing
    ): Future[A] =
      if (kamonEnabled) KamonSupport.span(collection: BSONCollection, commandType: String) {
        before.foreach(_.apply(Kamon.currentSpan()))
        future
      }
      else future

  }

}
