package reactivemongo.kinoplan.opentelemetry

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

import io.opentelemetry.api.trace.Span
import io.opentelemetry.context.{Context, Scope}
import reactivemongo.core.actors.ExpectingResponse
import reactivemongo.core.protocol.Response
import reactivemongo.kinoplan.opentelemetry.ReactiveMongoAttributesExtractor.setQueryText
import reactivemongo.kinoplan.opentelemetry.ReactiveMongoSingletons.instrumenter

final class ReactiveMongoAdviceScope private (
  request: ReactiveMongoRequest,
  parentContext: Context,
  context: Context,
  scope: Scope
) {

  def end(future: Any, throwable: Throwable): Any = {
    implicit val ec: ExecutionContext = CallingThreadExecutionContext

    scope.close()

    if (throwable != null || future == null) {
      instrumenter.end(context, request, null, throwable)
      future
    } else {
      val promise = Promise[Response]()

      future
        .asInstanceOf[Future[Response]]
        .onComplete { result =>
          // The result is passed to the caller even if ending the span fails
          try result match {
              case Success(response) => instrumenter.end(
                  context,
                  request,
                  response,
                  response.error.fold[Throwable](null)(e => e)
                )
              case Failure(exception) => instrumenter.end(context, request, null, exception)
            }
          finally {
            // The response future is completed by a driver thread without the caller's context,
            // so the returned future is completed with it: callbacks of the caller (e.g. the next
            // operation in a for-comprehension) are scheduled from this context and continue the trace
            val parentScope = parentContext.makeCurrent()
            try promise.complete(result)
            finally parentScope.close()
          }
        }

      promise.future
    }
  }

}

object ReactiveMongoAdviceScope {

  def start(expectingResponse: Any): ReactiveMongoAdviceScope = {
    val parentContext = Context.current()
    val requestMaker = expectingResponse.asInstanceOf[ExpectingResponse].requestMaker
    val statement = ReactiveMongoRequest.readStatement(requestMaker)
    val request = ReactiveMongoRequest(requestMaker, statement)

    if (!instrumenter.shouldStart(parentContext, request)) null
    else {
      val context = instrumenter.start(parentContext, request)
      val span = Span.fromContext(context)
      if (span.isRecording) statement
        .map(ReactiveMongoRequest.queryText)
        .foreach(setQueryText(span, _))
      new ReactiveMongoAdviceScope(request, parentContext, context, context.makeCurrent())
    }
  }

}
