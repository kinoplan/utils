package io.kinoplan.utils.zio.reactivemongo.syntax

import scala.concurrent.{ExecutionContext, Future}

import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.api.bson.BSONDocumentReader
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import zio.{Task, ZIO}

trait ReactiveMongoSyntax {

  implicit class QueryBuilderOps(builder: BSONCollection#QueryBuilder) {

    def all[T: BSONDocumentReader](limit: Int = -1)(implicit
      ec: ExecutionContext
    ): Future[List[T]] = builder
      .cursor[T](ReadPreference.secondaryPreferred)
      .collect[List](limit, Cursor.FailOnError[List[T]]())

  }

  implicit class WriteResultFutureOps[A <: WriteResult](task: Task[A]) {

    def adaptError: Task[A] = task.flatMap { result =>
      if (result.writeErrors.isEmpty) ZIO.succeed(result)
      else ZIO.fail(new Throwable(result.writeErrors.map(_.errmsg).mkString(", ")))
    }

  }

  implicit class MultiBulkWriteResultFutureOps(task: Task[BSONCollection#MultiBulkWriteResult]) {

    def adaptError: Task[BSONCollection#MultiBulkWriteResult] = task.flatMap { result =>
      if (result.writeErrors.isEmpty) ZIO.succeed(result)
      else ZIO.fail(new Throwable(result.writeErrors.map(_.errmsg).mkString(", ")))
    }

  }

}
