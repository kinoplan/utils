package io.kinoplan.utils.play.reactivemongo

import java.util.concurrent.CompletionStage

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

import io.opentelemetry.semconv.incubating.DbIncubatingAttributes.{
  DB_COLLECTION_NAME,
  DB_NAMESPACE,
  DB_OPERATION_NAME,
  DB_QUERY_SUMMARY,
  DB_SYSTEM
}
import kamon.Kamon
import kamon.Kamon.runWithSpan
import kamon.trace.Span
import kamon.util.{CallingThreadExecutionContext, CompletionStageSpanFinisher}
import reactivemongo.api.bson.collection.BSONCollection

object KamonSupport {

  object CommandType {
    val FIND = "find"
    val INSERT = "insert"
    val UPDATE = "update"
    val DELETE = "delete"
    val COUNT = "count"
    val AGGREGATE = "aggregate"
    val DISTINCT = "distinct"
  }

  def span[A](collection: BSONCollection, commandType: String)(f: => A)(implicit
    enclosing: sourcecode.Enclosing
  ): A = {
    // tag names according to https://github.com/open-telemetry/semantic-conventions/blob/main/docs/database/mongodb.md
    val span = Kamon
      .spanBuilder(enclosing.value)
      .kind(Span.Kind.Client)
      .tagMetrics(Span.TagKeys.Component, "org.reactivemongo")
      .tagMetrics(DB_NAMESPACE.getKey, collection.db.name)
      .tagMetrics(DB_COLLECTION_NAME.getKey, collection.name)
      .tagMetrics(DB_SYSTEM.getKey, "mongodb")
      .tagMetrics(DB_OPERATION_NAME.getKey, commandType)
      .tag(DB_QUERY_SUMMARY.getKey, enclosing.value)
      .start()

    Try(runWithSpan(span, finishSpan = false)(f)) match {
      case Success(value) => value match {
          case future: Future[_] =>
            future.onComplete {
              case Failure(t) => span.fail(t).finish()
              case _          => span.finish()
            }(CallingThreadExecutionContext)

            future.asInstanceOf[A]

          case cs: CompletionStage[_] =>
            CompletionStageSpanFinisher.finishWhenDone(cs, span).asInstanceOf[A]

          case _ =>
            span.finish()
            value
        }
      case Failure(exception) =>
        span.finish()
        throw exception
    }
  }

  def withKamon[A](
    enabled: Boolean,
    collection: BSONCollection,
    commandType: String,
    before: Option[Span => Unit] = None
  )(f: => A)(implicit
    enclosing: sourcecode.Enclosing
  ): A =
    if (enabled) KamonSupport.span(collection: BSONCollection, commandType: String) {
      before.foreach(_.apply(Kamon.currentSpan()))
      f
    }
    else f

}
