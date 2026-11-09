import sbt.*

object Dependencies {

  object Versions {
    val circeV         = "0.14.17"
    val kamonV         = "2.7.7"
    val logbackV       = "1.6.5"
    val openTelemetryV = "1.66.0"
    val reactivemongoV = "1.1.0-RC19"
    val scalaJavaTimeV = "2.7.0"
    val sttpV          = "4.1.0"
    val tapirV         = "1.13.33"
    val zioV           = "2.1.26"
    val zioConfigV     = "4.1.0"
  }

  import Versions.*

  object Libraries {
    // A -> Z
    val chimney                          = "io.scalaland"                  %% "chimney"                                % "1.11.0"
    val circeCore                        = "io.circe"                      %% "circe-core"                             % circeV
    val circeGeneric                     = "io.circe"                      %% "circe-generic"                          % circeV
    val circeParser                      = "io.circe"                      %% "circe-parser"                           % circeV
    val http4sBlazeServer                = "org.http4s"                    %% "http4s-blaze-server"                    % "0.23.18"
    val http4sDsl                        = "org.http4s"                    %% "http4s-dsl"                             % "0.23.38"
    val http4sServer                     = "org.http4s"                    %% "http4s-server"                          % "0.23.38"
    val jacksonModule                    = "com.fasterxml.jackson.module"  %% "jackson-module-scala"                   % "2.22.3.1"
    val jodaTime                         = "joda-time"                      % "joda-time"                              % "2.15.0"
    val kamonCore                        = "io.kamon"                      %% "kamon-core"                             % kamonV
    val kamonInstrumentationCommon       = "io.kamon"                      %% "kamon-instrumentation-common"           % kamonV
    val kamonTestkit                     = "io.kamon"                      %% "kamon-testkit"                          % kamonV     % Test
    val kanelaAgent                      = "io.kamon"                       % "kanela-agent"                           % "1.0.18"
    val kindProjector                    = "org.typelevel"                 %% "kind-projector"                         % "0.13.4"
    val localesFullDb                    = "io.github.cquiroz"             %% "locales-full-db"                        % "1.5.4"
    val logbackClassic                   = "ch.qos.logback"                 % "logback-classic"                        % logbackV
    val logbackCore                      = "ch.qos.logback"                 % "logback-core"                           % logbackV
    val micrometerRegistryPrometheus     = "io.micrometer"                  % "micrometer-registry-prometheus"         % "1.17.1"
    val mockitoScala                     = "org.scalatestplus"             %% "mockito-3-4"                            % "3.2.10.0" % Test
    val openTelemetryApi                 = "io.opentelemetry"               % "opentelemetry-api"                      % openTelemetryV
    val openTelemetryExporterLoggingOtlp = "io.opentelemetry"               % "opentelemetry-exporter-logging-otlp"    % openTelemetryV
    val openTelemetryExporterOtlp        = "io.opentelemetry"               % "opentelemetry-exporter-otlp"            % openTelemetryV
    val openTelemetrySdk                 = "io.opentelemetry"               % "opentelemetry-sdk"                      % openTelemetryV
    val openTelemetrySemconv             = "io.opentelemetry.semconv"       % "opentelemetry-semconv"                  % "1.44.0"
    val openTelemetrySemconvIncubating   = "io.opentelemetry.semconv"       % "opentelemetry-semconv-incubating"       % "1.30.0-alpha"
    val play                             = "org.playframework"             %% "play"                                   % "3.0.12"
    val play2                            = "com.typesafe.play"             %% "play"                                   % "2.8.22"
    val play2Json                        = "com.typesafe.play"             %% "play-json"                              % "2.10.8"
    val play2ReactiveMongo               = "org.reactivemongo"             %% "play2-reactivemongo"                    % "1.1.0-play28.RC13"
    val playJson                         = "org.playframework"             %% "play-json"                              % "3.0.6"
    val playReactiveMongo                = "org.reactivemongo"             %% "play2-reactivemongo"                    % "1.1.0-play30.RC21"
    val prometheusExporterHttpServer     = "io.prometheus"                  % "prometheus-metrics-exporter-httpserver" % "1.9.0"
    val reactiveMongo                    = "org.reactivemongo"             %% "reactivemongo"                          % "1.1.0-pekko.RC19"
    val reactiveMongoBsonApi             = "org.reactivemongo"             %% "reactivemongo-bson-api"                 % reactivemongoV
    val redisson                         = "org.redisson"                   % "redisson"                               % "4.8.0"
    val refined                          = "eu.timepit"                    %% "refined"                                % "0.11.3"
    val scalaCollectionCompat            = "org.scala-lang.modules"        %% "scala-collection-compat"                % "2.14.0"
    val scalaJavaCompat                  = "org.scala-lang.modules"        %% "scala-java8-compat"                     % "1.0.2"
    val scalaJavaLocales                 = "io.github.cquiroz"             %% "scala-java-locales"                     % "1.5.4"
    val scalaJavaTime                    = "io.github.cquiroz"             %% "scala-java-time"                        % scalaJavaTimeV
    val scalaJavaTimeZone                = "io.github.cquiroz"             %% "scala-java-time-tzdb"                   % scalaJavaTimeV
    val scalaLogging                     = "com.typesafe.scala-logging"    %% "scala-logging"                          % "3.9.6"
    val scalaReflect                     = "org.scala-lang"                 % "scala-reflect"
    val scalatest                        = "org.scalatest"                 %% "scalatest"                              % "3.2.20"   % Test
    val scalatestPlay                    = "org.scalatestplus.play"        %% "scalatestplus-play"                     % "7.0.2"    % Test
    val scalatestPlay2                   = "org.scalatestplus.play"        %% "scalatestplus-play"                     % "5.1.0"    % Test
    val scalatestPlusScalacheck          = "org.scalatestplus"             %% "scalacheck-1-19"                        % "3.2.20.0" % Test
    val sourcecode                       = "com.lihaoyi"                   %% "sourcecode"                             % "0.4.4"
    val sttpSlf4jBackend                 = "com.softwaremill.sttp.client4" %% "slf4j-backend"                          % sttpV
    val tapirCore                        = "com.softwaremill.sttp.tapir"   %% "tapir-core"                             % tapirV
    val tapirServer                      = "com.softwaremill.sttp.tapir"   %% "tapir-server"                           % tapirV
    val testContainers                   = "org.testcontainers"             % "testcontainers"                         % "2.0.5"    % Test
    val testContainersMongodb            = "org.testcontainers"             % "mongodb"                                % "1.21.4"   % Test
    val testContainersRedis              = "com.redis"                      % "testcontainers-redis"                   % "3.0.0"    % Test
    val typesafeConfig                   = "com.typesafe"                   % "config"                                 % "1.4.9"
    val zio                              = "dev.zio"                       %% "zio"                                    % zioV
    val zioConfig                        = "dev.zio"                       %% "zio-config"                             % zioConfigV
    val zioConfigMagnolia                = "dev.zio"                       %% "zio-config-magnolia"                    % zioConfigV
    val zioInteropCats                   = "dev.zio"                       %% "zio-interop-cats"                       % "23.1.0.14"
    val zioJson                          = "dev.zio"                       %% "zio-json"                               % "1.1.0"
    val zioMetricsConnectorsMicrometer   = "dev.zio"                       %% "zio-metrics-connectors-micrometer"      % "2.6.0"
    val zioOpenTelemetry                 = "dev.zio"                       %% "zio-opentelemetry"                      % "3.1.20"
    val zioPrelude                       = "dev.zio"                       %% "zio-prelude"                            % "1.0.0-RC48"
    val zioStreams                       = "dev.zio"                       %% "zio-streams"                            % zioV
    val zioTest                          = "dev.zio"                       %% "zio-test"                               % zioV
    val zioTestSbt                       = "dev.zio"                       %% "zio-test-sbt"                           % zioV
  }

  object Batches {

    val zioConfig: Seq[ModuleID] = Seq(Libraries.zioConfig, Libraries.zioConfigMagnolia)

  }

  object Schemes {

    val common: Seq[ModuleID] = Seq("org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always)

  }

}
