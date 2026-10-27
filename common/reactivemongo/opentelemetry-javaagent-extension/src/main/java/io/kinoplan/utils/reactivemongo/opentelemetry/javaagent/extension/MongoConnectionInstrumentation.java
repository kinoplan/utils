package io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import reactivemongo.kinoplan.opentelemetry.ReactiveMongoAdviceScope;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

public final class MongoConnectionInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return named("reactivemongo.api.MongoConnection");
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(named("sendExpectingResponse"))
                        .and(takesArguments(1))
                        .and(takesArgument(0, named("reactivemongo.core.actors.ExpectingResponse"))),
                MongoConnectionInstrumentation.class.getName() + "$SendExpectingResponseAdvice");
    }

    @SuppressWarnings("unused")
    public static class SendExpectingResponseAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class, inline = false)
        public static ReactiveMongoAdviceScope onEnter(@Advice.Argument(0) Object expectingResponse) {
            return ReactiveMongoAdviceScope.start(expectingResponse);
        }

        // The returned future is replaced with one that completes in the caller's context
        @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class, inline = false)
        @Advice.AssignReturned.ToReturned(typing = Assigner.Typing.DYNAMIC)
        public static Object onExit(
                @Advice.Return Object future,
                @Advice.Thrown Throwable throwable,
                @Advice.Enter ReactiveMongoAdviceScope adviceScope) {
            return adviceScope == null ? future : adviceScope.end(future, throwable);
        }
    }
}
