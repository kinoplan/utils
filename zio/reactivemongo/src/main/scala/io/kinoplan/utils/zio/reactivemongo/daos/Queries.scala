package io.kinoplan.utils.zio.reactivemongo.daos

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, document}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult

import io.kinoplan.utils.zio.reactivemongo.syntax.ReactiveMongoSyntax

object Queries extends ReactiveMongoSyntax {

  def findManyQ[T: BSONDocumentReader](
    collection: BSONCollection
  )(selector: BSONDocument = BSONDocument(), projection: Option[BSONDocument] = None)(implicit
    ec: ExecutionContext
  ): Future[List[T]] = collection.find(selector, projection).all[T]

  def findOneQ[T: BSONDocumentReader](
    collection: BSONCollection
  )(selector: BSONDocument = BSONDocument(), projection: Option[BSONDocument] = None)(implicit
    ec: ExecutionContext
  ): Future[Option[T]] = collection.find(selector, projection).one[T]

  def insertManyQ[T: BSONDocumentWriter](collection: BSONCollection)(values: List[T])(implicit
    ec: ExecutionContext
  ): Future[BSONCollection#MultiBulkWriteResult] = collection.insert(ordered = false).many(values)

  def insertOneQ[T: BSONDocumentWriter](collection: BSONCollection)(value: T)(implicit
    ec: ExecutionContext
  ): Future[WriteResult] = collection.insert(ordered = false).one(value)

  def updateQ(
    collection: BSONCollection
  )(q: BSONDocument, u: BSONDocument, multi: Boolean = false, upsert: Boolean = false)(implicit
    ec: ExecutionContext
  ): Future[BSONCollection#UpdateWriteResult] = collection.update(ordered = false)
    .one(q, u, multi = multi, upsert = upsert)

  def updateManyQ[T](
    collection: BSONCollection
  )(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean))(implicit
    ec: ExecutionContext
  ): Future[BSONCollection#MultiBulkWriteResult] = {
    val update = collection.update(ordered = false)
    val elements = Future.sequence(
      values.map(f).map { case (q, u, multi, upsert) =>
        update.element(q, u, multi = multi, upsert = upsert)
      }
    )
    elements.flatMap(element => update.many(element))
  }

  def saveQ[T: BSONDocumentWriter](
    collection: BSONCollection
  )(q: BSONDocument, u: T, multi: Boolean = false, upsert: Boolean = false)(implicit
    ec: ExecutionContext
  ): Future[BSONCollection#UpdateWriteResult] = collection.update(ordered = false)
    .one(q, document("$set" -> u), multi = multi, upsert = upsert)

  def saveWithoutIdQ[T: BSONDocumentWriter](
    collection: BSONCollection
  )(q: BSONDocument, u: T)(implicit
    ec: ExecutionContext,
    w: BSONDocumentWriter[T]
  ): Future[BSONCollection#UpdateWriteResult] = w.writeTry(u) match {
    case Success(bson) => collection.update(ordered = false)
        .one(q, u = bson -- "_id", multi = false, upsert = true)
    case Failure(ex) => throw ex
  }

  def saveManyQ[T: BSONDocumentWriter](
    collection: BSONCollection
  )(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
    ec: ExecutionContext
  ): Future[BSONCollection#MultiBulkWriteResult] = {
    val update = collection.update(ordered = false)
    val elements = Future.sequence(
      values.map(f).map { case (q, u, multi, upsert) =>
        update.element(q, document("$set" -> u), multi = multi, upsert = upsert)
      }
    )
    elements.flatMap(element => update.many(element))
  }

  def removeOneQ(collection: BSONCollection)(q: BSONDocument)(implicit
    ec: ExecutionContext
  ): Future[WriteResult] = collection.delete(ordered = false).one(q)

}
