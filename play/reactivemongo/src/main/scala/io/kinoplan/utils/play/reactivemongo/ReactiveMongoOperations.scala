package io.kinoplan.utils.play.reactivemongo

import scala.concurrent.{ExecutionContext, Future}

import reactivemongo.api.{DB, FailoverStrategy}
import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection

import io.kinoplan.utils.reactivemongo.base.Queries

class ReactiveMongoOperations[T](coll: Future[BSONCollection])(implicit
  ec: ExecutionContext
) {
  def collection: Future[BSONCollection] = coll

  def insertMany(values: List[T])(implicit
    w: BSONDocumentWriter[T]
  ) = collection.flatMap {
    Queries.insertManyQ(_)(values)
  }

  def insertOne(value: T)(implicit
    w: BSONDocumentWriter[T]
  ) = collection.flatMap {
    Queries.insertOneQ(_)(value)
  }

  def update(q: BSONDocument, u: BSONDocument, multi: Boolean = false, upsert: Boolean = false) =
    collection.flatMap {
      Queries.updateQ(_)(q, u, multi = multi, upsert = upsert)
    }

  def updateMany(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean)) =
    collection.flatMap {
      Queries.updateManyQ(_)(values, f)
    }

  def upsert(q: BSONDocument, value: T)(implicit
    w: BSONDocumentWriter[T]
  ) = collection.flatMap {
    Queries.upsertQ(_)(q, value)
  }

  def saveOne(q: BSONDocument, value: T, multi: Boolean = false, upsert: Boolean = true)(implicit
    w: BSONDocumentWriter[T]
  ) = collection.flatMap {
    Queries.saveQ(_)(q, value, multi = multi, upsert = upsert)
  }

  def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
    w: BSONDocumentWriter[T]
  ) = collection.flatMap {
    Queries.saveManyQ(_)(values, f)
  }

  def delete(q: BSONDocument) = collection.flatMap {
    Queries.deleteQ(_)(q)
  }

  def deleteByIds(ids: Set[BSONObjectID])(implicit
    ec: ExecutionContext
  ) = collection.flatMap {
    Queries.deleteByIdsQ(_)(ids)
  }

  def deleteById(id: BSONObjectID)(implicit
    ec: ExecutionContext
  ) = collection.flatMap {
    Queries.deleteByIdQ(_)(id)
  }

}

object ReactiveMongoOperations {

  def apply[T](collection: Future[BSONCollection])(implicit
    ec: ExecutionContext
  ) = new ReactiveMongoOperations[T](collection)

  def apply[T](
    db: Future[DB],
    collectionName: String,
    failoverStrategyO: Option[FailoverStrategy] = None
  )(implicit
    ec: ExecutionContext
  ) = new ReactiveMongoOperations[T](
    db.map(db => failoverStrategyO.fold(db.collection(collectionName))(db.collection(collectionName, _)))
  )

}
