package io.kinoplan.utils.reactivemongo.base

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import reactivemongo.api.{Collation, Cursor, CursorProducer, ReadConcern, ReadPreference}
import reactivemongo.api.bson.{
  BSONDocument,
  BSONDocumentReader,
  BSONDocumentWriter,
  BSONObjectID,
  document
}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.collection.BSONSerializationPack.NarrowValueReader
import reactivemongo.api.commands.WriteResult

private[utils] object Queries extends QueryBuilderSyntax{

  def countQ(collection: BSONCollection)(
    selector: Option[BSONDocument] = None,
    limit: Option[Int] = None,
    skip: Int = 0,
    readConcern: Option[ReadConcern] = None,
    readPreference: Option[ReadPreference] = None,
    comment: Option[String] = None
  )(implicit
    ec: ExecutionContext
  ): Future[Long] = {

    val selectorWithComment = comment match {
      case Some(c) => selector.map(_ ++ BSONDocument(f"$$comment" -> c)).orElse(Some(BSONDocument(f"$$comment" -> c)))
      case None    => selector
    }

    (readConcern, readPreference) match {
      case (Some(readConcern), Some(readPreference)) => collection.count(
          selectorWithComment,
          limit,
          skip,
          readConcern = readConcern,
          readPreference = readPreference
        )
      case (Some(readConcern), None) =>
        collection.count(selectorWithComment, limit, skip, readConcern = readConcern)
      case (None, Some(readPreference)) =>
        collection.count(selectorWithComment, limit, skip, readPreference = readPreference)
      case _ => collection.count(selectorWithComment, limit, skip)
    }
  }

  def countGroupedQ(collection: BSONCollection)(
    groupBy: String,
    matchQuery: BSONDocument,
    readConcern: Option[ReadConcern] = None,
    readPreference: Option[ReadPreference] = None,
    comment: Option[String] = None
  )(implicit
    ec: ExecutionContext
  ): Future[Map[String, Int]] = {
    val matchQueryWithComment = comment match {
      case Some(c) => matchQuery ++ BSONDocument(f"$$comment" -> c)
      case None    => matchQuery
    }

    implicit val resultTupleReader: BSONDocumentReader[(String, Int)] =
      BSONDocumentReader.from[(String, Int)](doc =>
        for {
          groupId <- doc.getAsTry[String]("_id")
          count <- doc.getAsTry[Int]("count")
        } yield groupId -> count
      )

    (
      (readConcern, readPreference) match {
        case (Some(readConcern), Some(readPreference)) => collection.aggregateWith[(String, Int)](
            readConcern = readConcern,
            readPreference = readPreference
          ) { framework =>
            import framework.{GroupField, Match, SumAll}

            List(Match(matchQueryWithComment), GroupField(groupBy)("count" -> SumAll))
          }
        case (Some(readConcern), None) =>
          collection.aggregateWith[(String, Int)](readConcern = readConcern) { framework =>
            import framework.{GroupField, Match, SumAll}

            List(Match(matchQueryWithComment), GroupField(groupBy)("count" -> SumAll))
          }
        case (None, Some(readPreference)) =>
          collection.aggregateWith[(String, Int)](readPreference = readPreference) { framework =>
            import framework.{GroupField, Match, SumAll}

            List(Match(matchQueryWithComment), GroupField(groupBy)("count" -> SumAll))
          }
        case _ => collection.aggregateWith[(String, Int)]() { framework =>
            import framework.{GroupField, Match, SumAll}

            List(Match(matchQueryWithComment), GroupField(groupBy)("count" -> SumAll))
          }
      }
    ).collect[Seq](-1, Cursor.FailOnError[Seq[(String, Int)]]()).map(_.toMap)
  }

  def distinctQ[R](collection: BSONCollection)(
    key: String,
    selector: Option[BSONDocument] = None,
    readConcern: Option[ReadConcern] = None,
    collation: Option[Collation] = None,
    comment: Option[String] = None
  )(implicit
    reader: NarrowValueReader[R],
    ec: ExecutionContext
  ): Future[Set[R]] = {
    val selectorWithComment = comment match {
      case Some(c) => selector.map(_ ++ BSONDocument(f"$$comment" -> c)).orElse(Some(BSONDocument(f"$$comment" -> c)))
      case None    => selector
    }

    readConcern.fold(
      collection.distinct[R, Set](key = key, selector = selectorWithComment, collation = collation)
    )(rc => collection.distinct[R, Set](key, selectorWithComment, rc, collation))
  }

  def findManyQ[T: BSONDocumentReader](collection: BSONCollection)(
    selector: BSONDocument = document,
    projection: Option[BSONDocument] = None,
    sort: BSONDocument = document,
    hint: Option[BSONDocument] = None,
    skip: Int = 0,
    limit: Int = -1,
    readConcern: Option[ReadConcern] = None,
    readPreference: ReadPreference = ReadPreference.secondaryPreferred,
    comment: Option[String] = None
  )(implicit
    ec: ExecutionContext
  ): Future[List[T]] = {
    val queryBuilder = collection.find(selector, projection).sort(sort).skip(skip)
    val queryBuilderWithHint =
      hint.fold(queryBuilder)(specification => queryBuilder.hint(collection.hint(specification)))
    val queryBuilderWithComment = comment.fold(queryBuilderWithHint)(queryBuilderWithHint.comment(_))

    queryBuilderWithComment.all[T](limit, readConcern = readConcern, readPreference = readPreference)
  }

  def findManyCursorQ[T](collection: BSONCollection)(
    selector: BSONDocument = document,
    projection: Option[BSONDocument] = None,
    sort: BSONDocument = document,
    batchSize: Int = 0,
    readConcern: Option[ReadConcern] = None,
    readPreference: ReadPreference = ReadPreference.secondaryPreferred,
    comment: Option[String] = None
  )(implicit
    r: BSONDocumentReader[T],
    cursorProducer: CursorProducer[T]
  ): cursorProducer.ProducedCursor = {
    val queryBuilder = collection.find(selector, projection).sort(sort).batchSize(batchSize)
    val queryBuilderWithComment = comment.fold(queryBuilder)(queryBuilder.comment(_))

    queryBuilderWithComment.allCursor[T](readConcern, readPreference)(r, cursorProducer)
  }

  def findOneQ[T: BSONDocumentReader](collection: BSONCollection)(
    selector: BSONDocument = BSONDocument(),
    projection: Option[BSONDocument] = None,
    readConcern: Option[ReadConcern] = None,
    readPreference: Option[ReadPreference] = None,
    comment: Option[String] = None
  )(implicit
    ec: ExecutionContext
  ): Future[Option[T]] = {
    val queryBuilder = collection.find(selector, projection)
    val queryBuilderWithReadConcern = readConcern.fold(queryBuilder)(queryBuilder.readConcern(_))
    val queryBuilderWithComment =
      comment.fold(queryBuilderWithReadConcern)(queryBuilderWithReadConcern.comment(_))

    readPreference.fold(queryBuilderWithComment.one[T])(rp =>
      queryBuilderWithComment.one[T](readPreference = rp)
    )
  }

  def insertManyQ[T: BSONDocumentWriter](collection: BSONCollection)(values: List[T])(implicit
    ec: ExecutionContext
  ) = collection.insert(ordered = false).many(values)

  def insertOneQ[T: BSONDocumentWriter](collection: BSONCollection)(value: T)(implicit
    ec: ExecutionContext
  ): Future[WriteResult] = collection.insert(ordered = false).one(value)

  def updateQ(collection: BSONCollection)(
    q: BSONDocument,
    u: BSONDocument,
    multi: Boolean = false,
    upsert: Boolean = false,
    arrayFilters: Seq[BSONDocument] = Seq.empty
  )(implicit
    ec: ExecutionContext
  ) = collection
    .update(ordered = false)
    .one(q, u, multi = multi, upsert = upsert, collation = None, arrayFilters = arrayFilters)

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

  def upsertQ[T: BSONDocumentWriter](collection: BSONCollection)(q: BSONDocument, u: T)(implicit
    ec: ExecutionContext,
    w: BSONDocumentWriter[T]
  ) = w.writeTry(u) match {
    case Success(bson) =>
      collection.update(ordered = false).one(q, u = bson -- "_id", multi = false, upsert = true)
    case Failure(ex) => throw ex
  }

  def saveQ[T: BSONDocumentWriter](
    collection: BSONCollection
  )(q: BSONDocument, u: T, multi: Boolean = false, upsert: Boolean = false)(implicit
    ec: ExecutionContext
  ) = collection
    .update(ordered = false)
    .one(q, document("$set" -> u), multi = multi, upsert = upsert)

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
