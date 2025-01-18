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
  ) = collection.flatMap { coll =>
    Queries.insertManyQ(coll)(values)
  }

  def insertOne(value: T)(implicit
    w: BSONDocumentWriter[T]
  ) = collection.flatMap { coll =>
    Queries.insertOneQ(coll)(value)
  }

  def update(q: BSONDocument, u: BSONDocument, multi: Boolean = false, upsert: Boolean = false) =
    collection.flatMap { coll =>
      Queries.updateQ(coll)(q, u, multi = multi, upsert = upsert)
    }

  def updateMany(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean)) =
    collection.flatMap { coll =>
      Queries.updateManyQ(coll)(values, f)
    }

  def upsert(q: BSONDocument, value: T)(implicit
    w: BSONDocumentWriter[T]
  ) = collection.flatMap { coll =>
    Queries.upsertQ(coll)(q, value)
  }

  def saveOne(q: BSONDocument, value: T, multi: Boolean = false, upsert: Boolean = true)(implicit
    w: BSONDocumentWriter[T]
  ) = collection.flatMap { coll =>
    Queries.saveQ(coll)(q, value, multi = multi, upsert = upsert)
  }

  def saveMany(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
    w: BSONDocumentWriter[T]
  ) = collection.flatMap { coll =>
    Queries.saveManyQ(coll)(values, f)
  }

  def delete(q: BSONDocument) = collection.flatMap { coll =>
    Queries.deleteQ(coll)(q)
  }

  def deleteByIds(ids: Set[BSONObjectID])(implicit
    ec: ExecutionContext
  ) = collection.flatMap { coll =>
    Queries.deleteByIdsQ(coll)(ids)
  }

  def deleteById(id: BSONObjectID)(implicit
    ec: ExecutionContext
  ) = collection.flatMap { coll =>
    Queries.deleteByIdQ(coll)(id)
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
    db.map(db =>
      failoverStrategyO.fold(db.collection(collectionName))(db.collection(collectionName, _))
    )
  )

}
