package io.kinoplan.utils.reactivemongo.base

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import reactivemongo.api.Cursor
import reactivemongo.api.bson.{
  BSONDocument,
  BSONDocumentReader,
  BSONDocumentWriter,
  BSONObjectID,
  BSONString,
  document
}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult

private[utils] object Queries extends QueryBuilderSyntax {

  def countQ(
    collection: BSONCollection
  )(selector: Option[BSONDocument] = None, limit: Option[Int] = None, skip: Int = 0)(implicit
    ec: ExecutionContext
  ): Future[Long] = collection.count(selector, limit, skip)

  def countGroupedQ(collection: BSONCollection)(groupBy: String, matchQuery: BSONDocument)(implicit
    ec: ExecutionContext
  ): Future[Map[String, Int]] = {
    implicit val resultTupleReader: BSONDocumentReader[(String, Int)] = BSONDocumentReader
      .from[(String, Int)](doc =>
        for {
          groupId <- doc.getAsTry[String]("_id")
          count <- doc.getAsTry[Int]("count")
        } yield groupId -> count
      )

    collection
      .aggregateWith[(String, Int)]() { framework =>
        import framework.{Group, Match, SumAll}

        List(Match(matchQuery), Group(BSONString(s"$groupBy"))("count" -> SumAll))
      }
      .collect[Seq](-1, Cursor.FailOnError[Seq[(String, Int)]]())
      .map(_.toMap)
  }

  def findManyQ[T: BSONDocumentReader](collection: BSONCollection)(
    selector: BSONDocument = document,
    projection: Option[BSONDocument] = None,
    sort: BSONDocument = document,
    hint: Option[BSONDocument] = None,
    skip: Int = 0,
    limit: Int = -1
  )(implicit
    ec: ExecutionContext
  ): Future[List[T]] = {
    val queryBuilder = collection.find(selector, projection).sort(sort).skip(skip)
    val queryBuilderWithHint = hint
      .fold(queryBuilder)(specification => queryBuilder.hint(collection.hint(specification)))

    queryBuilderWithHint.all[T](limit)
  }

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
  ): Future[BSONCollection#UpdateWriteResult] = collection
    .update(ordered = false)
    .one(q, u, multi = multi, upsert = upsert)

  def updateManyQ[T](
    collection: BSONCollection
  )(values: List[T], f: T => (BSONDocument, BSONDocument, Boolean, Boolean))(implicit
    ec: ExecutionContext
  ): Future[BSONCollection#MultiBulkWriteResult] = {
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

  def upsertQ[T: BSONDocumentWriter](collection: BSONCollection)(q: BSONDocument, u: T)(implicit
    ec: ExecutionContext,
    w: BSONDocumentWriter[T]
  ): Future[BSONCollection#UpdateWriteResult] = w.writeTry(u) match {
    case Success(bson) =>
      collection.update(ordered = false).one(q, u = bson -- "_id", multi = false, upsert = true)
    case Failure(ex) => throw ex
  }

  def saveQ[T: BSONDocumentWriter](
    collection: BSONCollection
  )(q: BSONDocument, u: T, multi: Boolean = false, upsert: Boolean = false)(implicit
    ec: ExecutionContext
  ): Future[BSONCollection#UpdateWriteResult] = collection
    .update(ordered = false)
    .one(q, document("$set" -> u), multi = multi, upsert = upsert)

  def saveManyQ[T: BSONDocumentWriter](
    collection: BSONCollection
  )(values: List[T], f: T => (BSONDocument, T, Boolean, Boolean))(implicit
    ec: ExecutionContext
  ): Future[BSONCollection#MultiBulkWriteResult] = {
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

  def deleteQ(collection: BSONCollection)(q: BSONDocument)(implicit
    ec: ExecutionContext
  ): Future[WriteResult] = collection.delete(ordered = false).one(q)

  def deleteByIdQ(collection: BSONCollection)(id: BSONObjectID)(implicit
    ec: ExecutionContext
  ): Future[WriteResult] = collection.delete(ordered = false).one(document("_id" -> id))

  def deleteByIdsQ(collection: BSONCollection)(ids: Set[BSONObjectID])(implicit
    ec: ExecutionContext
  ): Future[WriteResult] = collection
    .delete(ordered = false)
    .one(document("_id" -> document("$in" -> ids)))

}
