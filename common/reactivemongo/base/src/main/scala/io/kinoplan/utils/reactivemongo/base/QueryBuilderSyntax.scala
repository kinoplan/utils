package io.kinoplan.utils.reactivemongo.base

import scala.concurrent.{ExecutionContext, Future}

import reactivemongo.api.{Cursor, ReadConcern, ReadPreference}
import reactivemongo.api.Cursor.ErrorHandler
import reactivemongo.api.bson.BSONDocumentReader
import reactivemongo.api.bson.collection.BSONCollection

private[utils] trait QueryBuilderSyntax {

  implicit class QueryBuilderOps(builder: BSONCollection#QueryBuilder) {

    def all[T: BSONDocumentReader](
      limit: Int = -1,
      readConcern: Option[ReadConcern] = None,
      readPreference: ReadPreference = ReadPreference.secondaryPreferred,
      err: ErrorHandler[List[T]] = Cursor.FailOnError[List[T]]()
    )(implicit
      ec: ExecutionContext
    ): Future[List[T]] = {
      val builderWithReadConcern = readConcern.fold(builder)(builder.readConcern(_))

      builderWithReadConcern.cursor[T](readPreference).collect[List](limit, err)
    }

  }

}
