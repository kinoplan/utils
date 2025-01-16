package io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.incubator.semconv.db.DbClientAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import reactivemongo.api.ExpectingResponseWrapper;
import reactivemongo.api.ReactiveMongoAttributesExtractor;
import reactivemongo.api.ReactiveMongoDbClientAttributesGetter;
import reactivemongo.api.ReactiveMongoSpanNameExtractor;

public final class ReactiveMongoSingletons {
    private static final String INSTRUMENTATION_NAME = "io.kinoplan.utils.reactivemongo";

    private static final Instrumenter<ExpectingResponseWrapper, Void> INSTRUMENTER;

    static {
        ReactiveMongoDbClientAttributesGetter dbAttributesGetter = new ReactiveMongoDbClientAttributesGetter();
        ReactiveMongoAttributesExtractor attributesExtractor = new ReactiveMongoAttributesExtractor();
        ReactiveMongoSpanNameExtractor spanNameExtractor = new ReactiveMongoSpanNameExtractor();

        INSTRUMENTER =
                Instrumenter.<ExpectingResponseWrapper, Void>builder(
                                GlobalOpenTelemetry.get(),
                                INSTRUMENTATION_NAME,
                                spanNameExtractor)
                        .addAttributesExtractor(DbClientAttributesExtractor.create(dbAttributesGetter))
                        .addAttributesExtractor(attributesExtractor)
                        .buildInstrumenter(SpanKindExtractor.alwaysClient());
    }

    public static Instrumenter<ExpectingResponseWrapper, Void> instrumenter() {
        return INSTRUMENTER;
    }

    private ReactiveMongoSingletons() {
    }
}
