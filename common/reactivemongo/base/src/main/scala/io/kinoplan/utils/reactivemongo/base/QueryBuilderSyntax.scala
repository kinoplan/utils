package io.kinoplan.utils.reactivemongo.base

import scala.concurrent.{ExecutionContext, Future}

import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.api.bson.BSONDocumentReader
import reactivemongo.api.bson.collection.BSONCollection

private[utils] trait QueryBuilderSyntax {

  implicit class QueryBuilderOps(builder: BSONCollection#QueryBuilder) {

    def all[T: BSONDocumentReader](limit: Int = -1)(implicit
      ec: ExecutionContext
    ): Future[List[T]] = builder
      .cursor[T](ReadPreference.secondaryPreferred)
      .collect[List](limit, Cursor.FailOnError[List[T]]())

  }

}
