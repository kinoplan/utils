package io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers;
import net.bytebuddy.matcher.ElementMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AutoService(InstrumentationModule.class)
public final class ReactiveMongoInstrumentationModule extends InstrumentationModule {

    // Helpers live inside the reactivemongo package to access its private[reactivemongo] API
    static final String HELPERS_PACKAGE = "reactivemongo.kinoplan.opentelemetry.";

    // Generated at build time from the compiled helpers package: besides the source classes,
    // Scala emits synthetic ones (companions, anonymous classes), which a hardcoded list would miss.
    static final String HELPERS_RESOURCE =
            "META-INF/io/kinoplan/utils/reactivemongo/opentelemetry/javaagent/extension/helpers";

    public ReactiveMongoInstrumentationModule() {
        super("reactivemongo", "reactivemongo-1.1");
    }

    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return AgentElementMatchers.hasClassesNamed("reactivemongo.api.MongoConnection");
    }

    @Override
    public boolean isHelperClass(String className) {
        return className.startsWith(HELPERS_PACKAGE);
    }

    @Override
    public List<String> getAdditionalHelperClassNames() {
        try (InputStream stream =
                     ReactiveMongoInstrumentationModule.class.getClassLoader().getResourceAsStream(HELPERS_RESOURCE)) {
            if (stream == null) {
                throw new IllegalStateException("Missing resource " + HELPERS_RESOURCE);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            return reader.lines().filter(line -> !line.isEmpty()).collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(new MongoConnectionInstrumentation());
    }
}
