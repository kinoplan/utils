package reactivemongo.kinoplan.opentelemetry

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.instrumentation.api.incubator.semconv.db.{
  DbClientAttributesExtractor,
  DbClientSpanNameExtractor
}
import io.opentelemetry.instrumentation.api.instrumenter.{Instrumenter, SpanKindExtractor}
import reactivemongo.core.protocol.Response

object ReactiveMongoSingletons {

  private val InstrumentationName = "io.kinoplan.utils.reactivemongo"

  val instrumenter: Instrumenter[ReactiveMongoRequest, Response] = {
    val attributesGetter = new ReactiveMongoDbClientAttributesGetter

    Instrumenter
      .builder[ReactiveMongoRequest, Response](
        GlobalOpenTelemetry.get(),
        InstrumentationName,
        DbClientSpanNameExtractor.create(attributesGetter)
      )
      .addAttributesExtractor(DbClientAttributesExtractor.create(attributesGetter))
      .addAttributesExtractor(new ReactiveMongoAttributesExtractor)
      .buildInstrumenter(SpanKindExtractor.alwaysClient[ReactiveMongoRequest]())
  }

}
