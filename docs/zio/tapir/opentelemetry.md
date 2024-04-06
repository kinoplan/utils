# Utils ZIO Tapir OpenTelemetry

This module provides an integration of `zio-opentelemetry` with `tapir`.

## Installation
Add the following line to the `libraryDependencies` in your `build.sbt`:

```scala
"io.kinoplan" %% "utils-zio-tapir-opentelemetry" % ${version}
```

the dependency already comes with all necessary dependencies including `tapir` and `zio-opentelemetry`

## Usage
Provide `TracingInterceptor` to your `TapirMiddleware`, for example:

```scala
import zio.telemetry.opentelemetry.tracing.Tracing
import io.kinoplan.utils.zio.tapir.opentelemetry.TracingInterceptor

for {
  tracing <- ZIO.service[Tracing]
  options <- ZIO
    .serviceWithZIO[TapirMiddleware](_.serverOptions)
    .map(_.appendInterceptor(TracingInterceptor(tracing)))
  openapiConfig <- getConfig[OpenapiConfig]
} yield ()
```
