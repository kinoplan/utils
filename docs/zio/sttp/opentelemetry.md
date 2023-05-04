# Utils ZIO Sttp OpenTelemetry Backend

The module is a copy of

```scala
"com.softwaremill.sttp.client3" %% "opentelemetry-tracing-zio-backend" % ${version}
```

but with early support

```scala
"dev.zio" %% "zio-opentelemetry" % "3.x.x"
```

## Installation

Add the following line to the `libraryDependencies` in your `build.sbt`:

```scala
"io.kinoplan" %% "utils-zio-sttp-opentelemetry-backend" % ${version}
```

the dependency already comes with all necessary dependencies including `sttp` and `zio-opentelemetry`

## Usage

Provide `OpenTelemetryTracingZioBackend` to your `SttpBackend` ZIO Layer, for example:

```scala
import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio.{Task, ZIO, ZLayer}
import zio.telemetry.opentelemetry.tracing.Tracing

import io.kinoplan.utils.zio.sttp.opentelemetry._

object SttpBackendService {

  type SttpBackendService = SttpBackend[Task, ZioStreams with WebSockets]

  val live: ZLayer[Tracing, Throwable, SttpBackend[Task, ZioStreams with WebSockets]] = ZLayer
    .scoped(
      for {
        tracing <- ZIO.service[Tracing]
        backend <- AsyncHttpClientZioBackend
          .apply()
          .map(backend =>
            OpenTelemetryTracingZioBackend(
              backend,
              tracing,
              OpenTelemetryZioTracer.default(tracing)
            )
          )
      } yield backend
    )

}
```

You can also implement your own `OpenTelemetryZioTracer`.
