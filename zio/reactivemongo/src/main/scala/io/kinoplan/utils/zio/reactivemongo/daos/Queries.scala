package io.kinoplan.utils.zio.reactivemongo.daos

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, document}
import reactivemongo.api.bson.collection.BSONCollection

import io.kinoplan.utils.zio.reactivemongo.syntax.ReactiveMongoSyntax

object Queries extends ReactiveMongoSyntax {

  def countQ(
    collection: BSONCollection
  )(selector: Option[BSONDocument] = None, limit: Option[Int] = None, skip: Int = 0)(implicit
    ec: ExecutionContext
  ): Future[Long] = collection.count(selector, limit, skip)

  def findManyQ[T: BSONDocumentReader](collection: BSONCollection)(
    selector: BSONDocument = document,
    projection: Option[BSONDocument] = None,
    sort: BSONDocument = document,
    skip: Int = 0,
    limit: Int = -1
  )(implicit
    ec: ExecutionContext
  ): Future[List[T]] = collection.find(selector, projection).sort(sort).skip(skip).all[T](limit)

  def findOneQ[T: BSONDocumentReader](
    collection: BSONCollection
  )(selector: BSONDocument = BSONDocument(), projection: Option[BSONDocument] = None)(implicit
    ec: ExecutionContext
  ): Future[Option[T]] = collection.find(selector, projection).one[T]

  def insertManyQ[T: BSONDocumentWriter](collection: BSONCollection)(values: List[T])(implicit
    ec: ExecutionContext
  ) = collection.insert(ordered = false).many(values)

  def insertOneQ[T: BSONDocumentWriter](collection: BSONCollection)(value: T)(implicit
    ec: ExecutionContext
  ) = collection.insert(ordered = false).one(value)

  def updateQ(
    collection: BSONCollection
  )(q: BSONDocument, u: BSONDocument, multi: Boolean = false, upsert: Boolean = false)(implicit
    ec: ExecutionContext
  ) = collection.update(ordered = false).one(q, u, multi = multi, upsert = upsert)

  def updateManyQ[T](
    collection: BSONCollection
  )(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean))(implicit
    ec: ExecutionContext
  ) = {
    val update = collection.update(ordered = false)
    val elements = Future.sequence(
      values
        .map(f)
        .map { case (q, u, multi, upsert) =>
          update.element(q, u, multi = multi, upsert = upsert)
        }
    )
    elements.flatMap(element => update.many(element))
  }

  def saveQ[T: BSONDocumentWriter](
    collection: BSONCollection
  )(q: BSONDocument, u: T, multi: Boolean = false, upsert: Boolean = false)(implicit
    ec: ExecutionContext
  ) = collection
    .update(ordered = false)
    .one(q, document("$set" -> u), multi = multi, upsert = upsert)

  def saveWithoutIdQ[T: BSONDocumentWriter](
    collection: BSONCollection
  )(q: BSONDocument, u: T)(implicit
    ec: ExecutionContext,
    w: BSONDocumentWriter[T]
  ) = w.writeTry(u) match {
    case Success(bson) =>
      collection.update(ordered = false).one(q, u = bson -- "_id", multi = false, upsert = true)
    case Failure(ex) => throw ex
  }

  def saveManyQ[T: BSONDocumentWriter](
    collection: BSONCollection
  )(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
    ec: ExecutionContext
  ) = {
    val update = collection.update(ordered = false)
    val elements = Future.sequence(
      values
        .map(f)
        .map { case (q, u, multi, upsert) =>
          update.element(q, document("$set" -> u), multi = multi, upsert = upsert)
        }
    )
    elements.flatMap(element => update.many(element))
  }

  def removeOneQ(collection: BSONCollection)(q: BSONDocument)(implicit
    ec: ExecutionContext
  ) = collection.delete(ordered = false).one(q)

}
