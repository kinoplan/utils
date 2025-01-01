package io.kinoplan.utils.play.reactivemongo

import scala.concurrent.{ExecutionContext, Future}

import kamon.trace.Span
import reactivemongo.api.{DB, FailoverStrategy}
import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection

import io.kinoplan.utils.play.reactivemongo.KamonSupport.CommandType
import io.kinoplan.utils.reactivemongo.base.Queries

class ReactiveMongoOperations[T](coll: Future[BSONCollection], kamonEnabled: Boolean = true)(
  implicit
  ec: ExecutionContext
) {
  def collection: Future[BSONCollection] = coll

  def insertMany(values: List[T])(implicit
    w: BSONDocumentWriter[T],
    enclosing: sourcecode.Enclosing
  ) = collection.flatMap { coll =>
    Queries.insertManyQ(coll)(values).withKamon(coll, CommandType.INSERT)
  }

  def insertOne(value: T)(implicit
    w: BSONDocumentWriter[T],
    enclosing: sourcecode.Enclosing
  ) = collection.flatMap { coll =>
    Queries.insertOneQ(coll)(value).withKamon(coll, CommandType.INSERT)
  }

  def update(q: BSONDocument, u: BSONDocument, multi: Boolean = false, upsert: Boolean = false)(
    implicit
    enclosing: sourcecode.Enclosing
  ) = collection.flatMap { coll =>
    Queries.updateQ(coll)(q, u, multi = multi, upsert = upsert).withKamon(coll, CommandType.UPDATE)
  }

  def updateMany(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean))(implicit
    enclosing: sourcecode.Enclosing
  ) = collection.flatMap { coll =>
    Queries.updateManyQ(coll)(values, f).withKamon(coll, CommandType.UPDATE)
  }

  def upsert(q: BSONDocument, value: T)(implicit
    w: BSONDocumentWriter[T],
    enclosing: sourcecode.Enclosing
  ) = collection.flatMap { coll =>
    Queries.upsertQ(coll)(q, value).withKamon(coll, CommandType.UPDATE)
  }

  def saveOne(q: BSONDocument, value: T, multi: Boolean = false, upsert: Boolean = true)(implicit
    w: BSONDocumentWriter[T],
    enclosing: sourcecode.Enclosing
  ) = collection.flatMap { coll =>
    Queries.saveQ(coll)(q, value, multi = multi, upsert = upsert).withKamon(coll, CommandType.UPDATE)
  }

  def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
    w: BSONDocumentWriter[T],
    enclosing: sourcecode.Enclosing
  ) = collection.flatMap { coll =>
    Queries.saveManyQ(coll)(values, f).withKamon(coll, CommandType.UPDATE)
  }

  def delete(q: BSONDocument)(implicit
    enclosing: sourcecode.Enclosing
  ) = collection.flatMap { coll =>
    Queries.deleteQ(coll)(q).withKamon(coll, CommandType.DELETE)
  }

  def deleteByIds(ids: Set[BSONObjectID])(implicit
    ec: ExecutionContext,
    enclosing: sourcecode.Enclosing
  ) = collection.flatMap { coll =>
    Queries.deleteByIdsQ(coll)(ids).withKamon(coll, CommandType.DELETE)
  }

  def deleteById(id: BSONObjectID)(implicit
    ec: ExecutionContext,
    enclosing: sourcecode.Enclosing
  ) = collection.flatMap { coll =>
    Queries.deleteByIdQ(coll)(id).withKamon(coll, CommandType.DELETE)
  }

  implicit protected class FutureSyntax[A](future: Future[A]) {

    def withKamon(
      collection: BSONCollection,
      commandType: String,
      before: Option[Span => Unit] = None
    )(implicit
      enclosing: sourcecode.Enclosing
    ): Future[A] = KamonSupport.withKamon(kamonEnabled, collection, commandType, before)(future)

  }

}

object ReactiveMongoOperations {

  def apply[T](collection: Future[BSONCollection], kamonEnabled: Boolean)(implicit
    ec: ExecutionContext
  ) = new ReactiveMongoOperations[T](collection, kamonEnabled)

  def apply[T](
    db: Future[DB],
    collectionName: String,
    failoverStrategyO: Option[FailoverStrategy] = None,
    kamonEnabled: Boolean = true
  )(implicit
    ec: ExecutionContext
  ) = new ReactiveMongoOperations[T](
    db.map(db =>
      failoverStrategyO.fold(db.collection(collectionName))(db.collection(collectionName, _))
    ),
    kamonEnabled
  )

}
