package reactivemongo.api;


import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import reactivemongo.core.actors.ExpectingResponse;
import reactivemongo.core.protocol.Response;
import scala.concurrent.Future;

import static io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension.ReactiveMongoSingletons.instrumenter;
import static io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge.currentContext;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ReactiveMongoInstrumentation implements TypeInstrumentation {
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
                        .and(takesArgument(0, named("reactivemongo.core.actors.ExpectingResponse")))
                ,
                this.getClass().getName() + "$SendExpectingResponseAdvice"
        );
    }

    @SuppressWarnings("unused")
    public static class SendExpectingResponseAdvice {

        @Advice.AssignReturned.ToArguments(@Advice.AssignReturned.ToArguments.ToArgument(value = 0, index = 0))
        @Advice.OnMethodEnter(suppress = Throwable.class, inline = false)
        public static Object[] onEnter(
                @Advice.Argument(0) ExpectingResponse expectingResponse
        ) {
            Context parentContext = currentContext();
            ExpectingResponseWrapper wrapper = new ExpectingResponseWrapper(expectingResponse);
            if (!instrumenter().shouldStart(parentContext, wrapper)) {
                return new Object[]{null, null};
            }
            Context context = instrumenter().start(parentContext, wrapper);
            Scope scope = context.makeCurrent();

            return new Object[]{context, scope};
        }

        @Advice.AssignReturned.ToReturned
        @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
        public static Future<Response> onExit(
                @Advice.Argument(0) ExpectingResponse expectingResponse,
                @Advice.This MongoConnection thiz,
                @Advice.Return Future<Response> responseFuture,
                @Advice.Enter Object[] enter,
                @Advice.Thrown Throwable throwable
        ) {

            if (!(enter[0] instanceof Context context) || !(enter[1] instanceof Scope scope)) {
                return null;
            }
            scope.close();

            ExpectingResponseWrapper wrapper = new ExpectingResponseWrapper(expectingResponse);
            if (throwable != null) {
                instrumenter().end(context, wrapper, null, throwable);
                return responseFuture;
            }

            if (responseFuture == null) {
                return null;
            }
            return ResponseFutureWrapper.wrap(responseFuture, context, thiz.actorSystem().dispatcher());
        }
    }

}

