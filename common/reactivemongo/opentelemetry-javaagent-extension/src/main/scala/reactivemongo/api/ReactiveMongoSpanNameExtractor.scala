package reactivemongo.api

import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor

class ReactiveMongoSpanNameExtractor extends SpanNameExtractor[ExpectingResponseWrapper] {

  private val DEFAULT_SPAN_NAME = "DB Query"

  override def extract(wrapper: ExpectingResponseWrapper): String = {
    val operation = wrapper.operationName.orNull
    val dbName = wrapper.dbName.orNull

    if (operation == null) return if (dbName == null) DEFAULT_SPAN_NAME
    else dbName

    val table = wrapper.collectionName.orNull
    val name = new StringBuilder(operation)
    if (dbName != null || table != null) name.append(' ')
    // skip db name if table already has a db name prefixed to it// skip db name if table already has a db name prefixed to it
    if (dbName != null && (table == null || table.indexOf('.') == -1)) {
      name.append(dbName)
      if (table != null) name.append('.')
    }
    if (table != null) name.append(table)
    name.toString
  }

}
