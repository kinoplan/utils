package reactivemongo.api

import io.opentelemetry.instrumentation.api.incubator.semconv.db.DbClientAttributesGetter
import reactivemongo.api.bson.BSONDocument.pretty

class ReactiveMongoDbClientAttributesGetter
    extends DbClientAttributesGetter[ExpectingResponseWrapper] {

  override def getUser(request: ExpectingResponseWrapper): String = null

  override def getConnectionString(request: ExpectingResponseWrapper): String = null

  override def getDbSystem(request: ExpectingResponseWrapper): String = "mongodb"

  override def getDbNamespace(request: ExpectingResponseWrapper): String = request
    .dbName
    .getOrElse("undefined")

  override def getDbQueryText(request: ExpectingResponseWrapper): String = request
    .statement
    .map(pretty)
    .orNull

  override def getDbOperationName(request: ExpectingResponseWrapper): String =
    request.operationName.orNull

}
