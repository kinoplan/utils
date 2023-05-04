# Utils ZIO OpenTelemetry

This module is a wrapper over `zio-opentelemetry` that facilitates the integration
of OpenTelemetry with ZIO applications, simplifying the tracing process.

The module provides:

* **OpenTelemetrySdk** - pre-configured `api.OpenTelemetry` ZIO layer based on configuration data
* **TracerSampler** - an internal customizable `Sampler` that allows you to define templates for operation
  names that you prefer to not trace using regex patterns. Optimizes tracing by avoiding unnecessary traces
  and reducing noise, potentially improving application performance.

Note: At the moment the module is only implemented to work with `Tracing`

## Installation

Add the following line to the `libraryDependencies` in your `build.sbt`:

```scala
"io.kinoplan" %% "utils-zio-opentelemetry" % ${version}
```

the dependency already comes with all necessary dependencies including `io.opentelemetry` and `zio-opentelemetry`

## Configuration

You can configure the OpenTelemetry tracing in your configuration data as follows:

```hocon
opentelemetry {
  tracing {
    provider = "jaeger" // Available: "noop", "stdout", "jaeger"
    serviceName = "your-service-name"
    serviceName = ${?JAEGER_SERVICE_NAME} // Can be overridden with an environment variable
    endpoint = "http://your-jaeger-endpoint:4317"
    endpoint = ${?JAEGER_OPENTELEMETRY_ENDPOINT} // Can be overridden with an environment variable
    ignoreNamePatterns = ["GET /health", "GET /docs.*"] // Regex patterns for operation names to ignore
  }
}
```

## Usage

Add the necessary layers to your ZIO application, for example:

```scala
import zio._
import zio.telemetry.opentelemetry.OpenTelemetry

import io.kinoplan.utils.zio.opentelemetry.OpenTelemetrySdk

object MainApp extends ZIOAppDefault {
  
  def program = ???

  override def run: URIO[Any, ExitCode] = program
    .provide(
      OpenTelemetrySdk.live,
      OpenTelemetry.tracing(getClass.getName),
      // other layers
    )
    .logError
    .exitCode

}
```

`Tracing` is now enabled and can be used, for example:

```scala
import zio._
import zio.macros.accessible
import zio.telemetry.opentelemetry.tracing.Tracing

@accessible
trait FooService {
  def example1: Task[???]

  def example2: Task[???]
}

case class FooServiceLive(tracing: Tracing)
  extends FooService {

  override def example1: Task[Unit] = tracing
    .span("example1-span")(
      // logic
    )

  override def example2: Task[Unit] = {
    // logic
  } @@ tracing.span("example2-span")

}

object FooService {

  val live = ZLayer.fromZIO(
    for {
      tracing <- ZIO.service[Tracing]
    } yield FooServiceLive(tracing)
  )

}
```

For more information on how to use `opentelemetry` in ZIO applications, see the [zio-opentelemetry documentation](https://zio.dev/zio-telemetry/opentelemetry)
