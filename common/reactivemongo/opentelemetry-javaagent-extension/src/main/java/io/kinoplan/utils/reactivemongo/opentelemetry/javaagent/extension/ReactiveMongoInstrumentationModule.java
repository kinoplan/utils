package io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers;
import net.bytebuddy.matcher.ElementMatcher;
import reactivemongo.api.ReactiveMongoInstrumentation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AutoService(InstrumentationModule.class)
public final class ReactiveMongoInstrumentationModule extends InstrumentationModule {
    public ReactiveMongoInstrumentationModule() {
        super("reactivemongo");
    }

    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return AgentElementMatchers.hasClassesNamed("reactivemongo.api.MongoConnection");
    }

    @Override
    public boolean isIndyModule() {
        return true;
    }

    public List<String> getAdditionalHelperClassNames() {
        return Arrays.asList(
                "io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension.ReactiveMongoSingletons",
                "reactivemongo.api.ResponseFutureWrapper",
                "reactivemongo.api.ResponseFutureWrapper$1",
                "reactivemongo.api.ResponseFutureWrapper$2",
                "reactivemongo.api.ReactiveMongoDbClientAttributesGetter",
                "reactivemongo.api.ReactiveMongoAttributesExtractor",
                "reactivemongo.api.ReactiveMongoSpanNameExtractor"
        );
    }


    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(new ReactiveMongoInstrumentation());
    }
}
