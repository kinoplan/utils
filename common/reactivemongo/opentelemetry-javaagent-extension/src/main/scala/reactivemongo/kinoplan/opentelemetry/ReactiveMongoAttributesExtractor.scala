package reactivemongo.kinoplan.opentelemetry

import io.opentelemetry.api.common.{AttributeKey, AttributesBuilder}
import io.opentelemetry.api.trace.Span
import io.opentelemetry.context.Context
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor
import reactivemongo.core.protocol.Response

final class ReactiveMongoAttributesExtractor
    extends AttributesExtractor[ReactiveMongoRequest, Response] {

  // copied from DbIncubatingAttributes
  private val DbResponseReturnedRows = AttributeKey.longKey("db.response.returned_rows")

  // Other commands are replied with a single document, not with the found ones
  private val ReturningRowsOperations = Set("find", "aggregate", "getMore")

  override def onStart(
    attributes: AttributesBuilder,
    parentContext: Context,
    request: ReactiveMongoRequest
  ): Unit = ()

  override def onEnd(
    attributes: AttributesBuilder,
    context: Context,
    request: ReactiveMongoRequest,
    response: Response,
    error: Throwable
  ): Unit =
    if (
      response != null && error == null && request.operationName.exists(ReturningRowsOperations)
    ) {
      attributes.put(DbResponseReturnedRows, Long.box(response.reply.numberReturned.toLong))
      ()
    }

}

object ReactiveMongoAttributesExtractor {

  // copied from DbIncubatingAttributes
  private val DbQueryText = AttributeKey.stringKey("db.query.text")

  // Only the stable name, as in agent 3.0
  def setQueryText(span: Span, queryText: String): Unit = {
    span.setAttribute(DbQueryText, queryText)
    ()
  }

}
