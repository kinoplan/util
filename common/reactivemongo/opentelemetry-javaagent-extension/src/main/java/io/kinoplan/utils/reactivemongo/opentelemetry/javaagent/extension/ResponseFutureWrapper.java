package io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.opentelemetry.context.Context;
import reactivemongo.core.protocol.Response;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.runtime.AbstractFunction1;

import static io.kinoplan.utils.reactivemongo.opentelemetry.javaagent.extension.ReactiveMongoSingletons.instrumenter;

public final class ResponseFutureWrapper {

    public static Future<Response> wrap(
            Future<Response> future, Context context, ExecutionContext executionContext) {

        return future.transform(
                new AbstractFunction1<>() {
                    @Override
                    @CanIgnoreReturnValue
                    public Response apply(Response result) {
                        instrumenter().end(context, null, null, null);
                        return result;
                    }
                },
                new AbstractFunction1<>() {
                    @Override
                    @CanIgnoreReturnValue
                    public Throwable apply(Throwable throwable) {
                        instrumenter().end(context, null, null, throwable);
                        return throwable;
                    }
                },
                executionContext);
    }
}
