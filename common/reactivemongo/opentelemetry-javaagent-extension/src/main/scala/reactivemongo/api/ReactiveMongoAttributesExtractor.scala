/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package reactivemongo.api

import io.opentelemetry.api.common.{AttributeKey, AttributesBuilder}
import io.opentelemetry.context.Context
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor
import io.opentelemetry.instrumentation.api.internal.SemconvStability.{
  emitOldDatabaseSemconv,
  emitStableDatabaseSemconv
};

class ReactiveMongoAttributesExtractor extends AttributesExtractor[ExpectingResponseWrapper, Void] {

  // copied from DbIncubatingAttributes
  private val DB_COLLECTION_NAME = AttributeKey.stringKey("db.collection.name");
  private val DB_MONGODB_COLLECTION = AttributeKey.stringKey("db.mongodb.collection");

  override def onStart(
    attributes: AttributesBuilder,
    parentContext: Context,
    request: ExpectingResponseWrapper
  ): Unit = request
    .collectionName
    .foreach { collectionName =>
      if (emitStableDatabaseSemconv()) attributes.put(DB_COLLECTION_NAME, collectionName)
      if (emitOldDatabaseSemconv()) attributes.put(DB_MONGODB_COLLECTION, collectionName)
    }

  override def onEnd(
    attributes: AttributesBuilder,
    context: Context,
    request: ExpectingResponseWrapper,
    response: Void,
    error: Throwable
  ): Unit = {}

}
