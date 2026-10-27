# Utils ReactiveMongo OpenTelemetry Javaagent Extension

This module is an extension for the [OpenTelemetry Java agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation)
that adds automatic tracing of ReactiveMongo driver operations, which the agent does not support out of the box.

For every request sent through `reactivemongo.api.MongoConnection` a `CLIENT` span is created with
the [database semantic conventions](https://opentelemetry.io/docs/specs/semconv/database/mongodb/) attributes:

* `db.system.name` - always `mongodb`
* `db.namespace` - database name
* `db.collection.name` - collection name
* `db.operation.name` - command name, e.g. `find`, `insert`, `getMore`
* `db.query.text` - command document, truncated to the configured length
* `db.response.returned_rows` - number of returned documents (`find`, `aggregate` and `getMore`)

The span name is `{db.operation.name} {db.collection.name}`, e.g. `find users`. Failed commands
(including MongoDB errors in a successful response) mark the span as an error.

The extension follows the stable conventions only, as the agent does since 3.0. Agent 2.x emits
the old ones by default (`db.system`, `db.name`, `db.operation`, no collection), so set
`otel.semconv-stability.opt-in=database` there.

## Installation

The extension is not a regular library dependency: the agent loads it from a jar file passed
with the `otel.javaagent.extensions` property.

```shell
java -javaagent:opentelemetry-javaagent.jar \
  -Dotel.javaagent.extensions=utils-reactivemongo-opentelemetry-javaagent-extension_2.13-${version}.jar \
  -jar app.jar
```

The jar must match the Scala version of your application (`_2.12`, `_2.13` or `_3`).
You can download it from Maven Central, or let sbt fetch it, e.g. with `sbt-native-packager`:

```scala
lazy val OtelExtensions = config("otel-extensions").hide

ivyConfigurations += OtelExtensions

libraryDependencies += ("io.kinoplan" %% "utils-reactivemongo-opentelemetry-javaagent-extension" % ${version} % OtelExtensions).intransitive()

Universal / mappings ++= update
  .value
  .select(configurationFilter(OtelExtensions.name))
  .map(file => file -> s"otel/${file.getName}")
```

## Configuration

| System property                                             | Environment variable                                        | Default | Description                                        |
|-------------------------------------------------------------|-------------------------------------------------------------|---------|----------------------------------------------------|
| `otel.instrumentation.reactivemongo.enabled`                | `OTEL_INSTRUMENTATION_REACTIVEMONGO_ENABLED`                | `true`  | Enables the instrumentation                        |
| `otel.instrumentation.reactivemongo.query-text.max-length`  | `OTEL_INSTRUMENTATION_REACTIVEMONGO_QUERY_TEXT_MAX_LENGTH`  | `16384` | Maximum length of `db.query.text`                  |

Note: `db.query.text` contains the command as is, values are not sanitized.

To keep the overhead low, the command is printed only for sampled spans, after the sampling decision
(so samplers do not see `db.query.text`).

## Context propagation

A span is a child of the context that is current when the command is sent. The future returned by
the driver completes in the caller's context, so sequential operations in a for-comprehension stay
in the caller's trace.

ReactiveMongo sends commands from callbacks on the `ExecutionContext` it is given, so this context
must propagate the OpenTelemetry context. The agent does it for the standard executors (e.g.
`ExecutionContext.global`), but not for ZIO: with `ZIO.fromFuture(implicit ec => ...)` every command
starts a new trace. Add the ZIO execution context to the instrumented executors (ZIO 2.1.25+; in earlier
versions it is not a `java.util.concurrent.Executor` and cannot be instrumented this way):

```shell
java -javaagent:opentelemetry-javaagent.jar \
  -Dotel.instrumentation.executors.include='zio.Executor$Interop' \
  ...
```

Or `OTEL_INSTRUMENTATION_EXECUTORS_INCLUDE='zio.Executor$Interop'`. Other custom executors can be added
the same way, separated by commas.

Spans created with `zio-opentelemetry` are parents of the ReactiveMongo spans only when it uses
the agent's instance and keeps the context in the thread-local storage of OpenTelemetry
(`OpenTelemetry.global` and `OpenTelemetry.contextJVM`), see
[zio-opentelemetry documentation](https://zio.dev/zio-telemetry/opentelemetry).

## Compatibility

The extension is built and tested with OpenTelemetry Java agent 2.33.0 and ReactiveMongo 1.1.0-RC19,
against MongoDB 5.0 (commands are sent with `OP_QUERY`) and 7.0 (`OP_MSG`).

The advice is compatible with the agent's invokedynamic mode (`otel.javaagent.experimental.indy=true`,
the default since the agent 3.0), where the extension classes are not injected into the application class loader.
