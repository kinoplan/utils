package io.kinoplan.utils.play.reactivemongo

import java.util.concurrent.CompletionStage

import scala.concurrent.Future
import scala.util.Failure
import scala.util.control.NonFatal

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
    val COMMAND = "command"
  }

  def span[A](collection: BSONCollection, commandType: String)(f: A)(implicit
    enclosing: sourcecode.Enclosing
  ): A = {
    val span = Kamon
      .spanBuilder(enclosing.value)
      .kind(Span.Kind.Client)
      .tagMetrics(Span.TagKeys.Component, "reactivemongo")
      .tagMetrics("db.instance", collection.db.name)
      .tagMetrics("db.collection", collection.name)
      .tagMetrics("command.type", commandType)
      .start()

    try runWithSpan(span, finishSpan = false)(f) match {
        case future: Future[_] =>
          future.onComplete {
            case Failure(t) => span.fail(t).finish()

            case _ => span.finish()
          }(CallingThreadExecutionContext)

          future.asInstanceOf[A]

        case cs: CompletionStage[_] =>
          CompletionStageSpanFinisher.finishWhenDone(cs, span).asInstanceOf[A]

        case other =>
          span.finish()
          other
      }
    catch {
      case NonFatal(t) =>
        span.finish()
        throw t
    }
  }

}
