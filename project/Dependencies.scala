import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.*
import sbt.librarymanagement.DependencyBuilders
import sbt.{Def, *}

object Dependencies {

  object Versions {
    val circeV         = "0.14.15"
    val kamonV         = "2.7.7"
    val logbackV       = "1.5.19"
    val openTelemetryV = "1.55.0"
    val reactivemongoV = "1.1.0-RC13"
    val scalaJavaTimeV = "2.6.0"
    val sttpV          = "3.11.0"
    val tapirV         = "1.11.50"
    val zioV           = "2.1.22"
    val zioConfigV     = "4.0.5"
  }

  import Versions.*

  object Libraries {
    val scalaReflect = "org.scala-lang" % "scala-reflect"
    // Cross-platform dependencies
    val chimney                 = Def.setting("io.scalaland" %%% "chimney" % "1.8.2")
    val circeCore               = Def.setting("io.circe" %%% "circe-core" % circeV)
    val circeGeneric            = Def.setting("io.circe" %%% "circe-generic" % circeV)
    val circeParser             = Def.setting("io.circe" %%% "circe-parser" % circeV)
    val localesFullDb           = Def.setting("io.github.cquiroz" %%% "locales-full-db" % "1.5.4")
    val scalaJavaLocales        = Def.setting("io.github.cquiroz" %%% "scala-java-locales" % "1.5.4")
    val scalaJavaTime           = Def.setting("io.github.cquiroz" %%% "scala-java-time" % scalaJavaTimeV)
    val scalaJavaTimeZone       = Def.setting("io.github.cquiroz" %%% "scala-java-time-tzdb" % scalaJavaTimeV)
    val scalatest               = Def.setting("org.scalatest" %%% "scalatest" % "3.2.19" % Test)
    val scalatestPlusScalacheck = Def.setting("org.scalatestplus" %%% "scalacheck-1-18" % "3.2.19.0" % Test)
    val tapirCore               = Def.setting("com.softwaremill.sttp.tapir" %%% "tapir-core" % tapirV)
    val zio                     = Def.setting("dev.zio" %%% "zio" % zioV)
    val zioPrelude              = Def.setting("dev.zio" %%% "zio-prelude" % "1.0.0-RC42")
    val zioTest                 = Def.setting("dev.zio" %%% "zio-test" % zioV)
    val zioTestSbt              = Def.setting("dev.zio" %%% "zio-test-sbt" % zioV)

    // A -> Z
    val http4sBlazeServer                = "org.http4s"                    %% "http4s-blaze-server"                    % "0.23.17"
    val http4sDsl                        = "org.http4s"                    %% "http4s-dsl"                             % "0.23.32"
    val http4sServer                     = "org.http4s"                    %% "http4s-server"                          % "0.23.32"
    val jacksonModule                    = "com.fasterxml.jackson.module"  %% "jackson-module-scala"                   % "2.20.0"
    val jodaTime                         = "joda-time"                      % "joda-time"                              % "2.14.0"
    val kamonCore                        = "io.kamon"                      %% "kamon-core"                             % kamonV
    val kamonInstrumentationCommon       = "io.kamon"                      %% "kamon-instrumentation-common"           % kamonV
    val kamonTestkit                     = "io.kamon"                      %% "kamon-testkit"                          % kamonV     % Test
    val kanelaAgent                      = "io.kamon"                       % "kanela-agent"                           % "1.0.18"
    val kindProjector                    = "org.typelevel"                 %% "kind-projector"                         % "0.13.4"
    val logbackClassic                   = "ch.qos.logback"                 % "logback-classic"                        % logbackV
    val logbackCore                      = "ch.qos.logback"                 % "logback-core"                           % logbackV
    val micrometerRegistryPrometheus     = "io.micrometer"                  % "micrometer-registry-prometheus"         % "1.15.5"
    val mockitoScala                     = "org.scalatestplus"             %% "mockito-3-4"                            % "3.2.10.0" % Test
    val openTelemetryApi                 = "io.opentelemetry"               % "opentelemetry-api"                      % openTelemetryV
    val openTelemetryExporterOtlp        = "io.opentelemetry"               % "opentelemetry-exporter-otlp"            % openTelemetryV
    val openTelemetryExporterLoggingOtlp = "io.opentelemetry"               % "opentelemetry-exporter-logging-otlp"    % openTelemetryV
    val openTelemetrySdk                 = "io.opentelemetry"               % "opentelemetry-sdk"                      % openTelemetryV
    val openTelemetrySemconv             = "io.opentelemetry.semconv"       % "opentelemetry-semconv"                  % "1.37.0"
    val openTelemetrySemconvIncubating   = "io.opentelemetry.semconv"       % "opentelemetry-semconv-incubating"       % "1.30.0-alpha"
    val play                             = "org.playframework"             %% "play"                                   % "3.0.9"
    val play2                            = "com.typesafe.play"             %% "play"                                   % "2.8.22"
    val playJson                         = "org.playframework"             %% "play-json"                              % "3.0.6"
    val play2Json                        = "com.typesafe.play"             %% "play-json"                              % "2.10.8"
    val playReactiveMongo                = "org.reactivemongo"             %% "play2-reactivemongo"                    % "1.1.0-play30.RC18"
    val play2ReactiveMongo               = "org.reactivemongo"             %% "play2-reactivemongo"                    % "1.1.0-play28.RC13"
    val prometheusExporterHttpServer     = "io.prometheus"                  % "prometheus-metrics-exporter-httpserver" % "1.4.1"
    val reactiveMongo                    = "org.reactivemongo"             %% "reactivemongo"                          % "1.1.0-pekko.RC13"
    val reactiveMongoBsonApi             = "org.reactivemongo"             %% "reactivemongo-bson-api"                 % reactivemongoV
    val redisson                         = "org.redisson"                   % "redisson"                               % "3.24.2"
    val refined                          = "eu.timepit"                    %% "refined"                                % "0.11.3"
    val scalaCollectionCompat            = "org.scala-lang.modules"        %% "scala-collection-compat"                % "2.14.0"
    val scalaJavaCompat                  = "org.scala-lang.modules"        %% "scala-java8-compat"                     % "1.0.2"
    val scalaLogging                     = "com.typesafe.scala-logging"    %% "scala-logging"                          % "3.9.6"
    val scalatestPlay                    = "org.scalatestplus.play"        %% "scalatestplus-play"                     % "7.0.2"    % Test
    val scalatestPlay2                   = "org.scalatestplus.play"        %% "scalatestplus-play"                     % "5.1.0"    % Test
    val sourcecode                       = "com.lihaoyi"                   %% "sourcecode"                             % "0.4.4"
    val sttpCore                         = "com.softwaremill.sttp.client3" %% "core"                                   % sttpV
    val sttpSlf4jBackend                 = "com.softwaremill.sttp.client3" %% "slf4j-backend"                          % sttpV
    val sttpZio                          = "com.softwaremill.sttp.client3" %% "zio"                                    % sttpV
    val tapirServer                      = "com.softwaremill.sttp.tapir"   %% "tapir-server"                           % tapirV
    val testContainersMongodb            = "org.testcontainers"             % "mongodb"                                % "1.21.3"   % Test
    val typesafeConfig                   = "com.typesafe"                   % "config"                                 % "1.4.5"
    val zioConfig                        = "dev.zio"                       %% "zio-config"                             % zioConfigV
    val zioConfigMagnolia                = "dev.zio"                       %% "zio-config-magnolia"                    % zioConfigV
    val zioConfigEnumeratum              = "dev.zio"                       %% "zio-config-enumeratum"                  % zioConfigV
    val zioInteropCats                   = "dev.zio"                       %% "zio-interop-cats"                       % "23.1.0.5"
    val zioMetricsConnectorsMicrometer   = "dev.zio"                       %% "zio-metrics-connectors-micrometer"      % "2.5.0"
    val zioOpenTelemetry                 = "dev.zio"                       %% "zio-opentelemetry"                      % "3.1.10"
  }

  object Batches {

    val zioConfig: Seq[ModuleID] = Seq(Libraries.zioConfig, Libraries.zioConfigMagnolia, Libraries.zioConfigEnumeratum)

  }

  case class ShadingEntity(
    dependencies: Seq[sbt.ModuleID],
    modulePackages: Seq[String],
    notShadedDependencies: Seq[sbt.ModuleID] = Seq.empty,
    notShadedScalaVersionDependencies: Seq[DependencyBuilders.OrganizationArtifactName] = Seq.empty
  ) {

    def libraryDependencies(scalaVersion: String): Seq[ModuleID] = dependencies ++ notShadedDependencies ++
      notShadedScalaVersionDependencies.map(_ % scalaVersion)

  }

  object Shades {

    val zioConfig: ShadingEntity = ShadingEntity(
      Batches.zioConfig,
      Seq("zio.config", "mercator", "magnolia", "enumeratum"),
      Seq(Libraries.scalaCollectionCompat),
      Seq(Libraries.scalaReflect)
    )

  }

}
