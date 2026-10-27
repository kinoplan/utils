package reactivemongo.kinoplan.opentelemetry

import io.opentelemetry.instrumentation.api.incubator.semconv.db.DbClientAttributesGetter
import reactivemongo.core.protocol.Response

final class ReactiveMongoDbClientAttributesGetter
    extends DbClientAttributesGetter[ReactiveMongoRequest, Response] {

  override def getDbSystemName(request: ReactiveMongoRequest): String = "mongodb"

  override def getDbNamespace(request: ReactiveMongoRequest): String = request.dbName.orNull

  override def getDbCollectionName(request: ReactiveMongoRequest): String =
    request.collectionName.orNull

  override def getDbOperationName(request: ReactiveMongoRequest): String =
    request.operationName.orNull

  override def getDbQueryText(request: ReactiveMongoRequest): String = null

}
