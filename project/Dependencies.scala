import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.*
import sbt.{Def, *}
import sbt.librarymanagement.DependencyBuilders

object Dependencies {

  object Versions {
    val circeV         = "0.14.7"
    val openTelemetryV = "1.37.0"
    val playV          = "2.8.21"
    val reactivemongoV = "1.0.10"
    val scalaJavaTimeV = "2.5.0"
    val sttpV          = "3.9.5"
    val tapirV         = "1.10.6"
    val zioV           = "2.0.22"
    val zioConfigV     = "4.0.1"
  }

  import Versions.*

  object Libraries {
    val scalaReflect = "org.scala-lang" % "scala-reflect"
    // Cross-platform dependencies
    val circeCore         = Def.setting("io.circe" %%% "circe-core" % circeV)
    val circeGeneric      = Def.setting("io.circe" %%% "circe-generic" % circeV)
    val localesFullDb     = Def.setting("io.github.cquiroz" %%% "locales-full-db" % "1.5.1")
    val scalaJavaLocales  = Def.setting("io.github.cquiroz" %%% "scala-java-locales" % "1.5.1")
    val scalaJavaTime     = Def.setting("io.github.cquiroz" %%% "scala-java-time" % scalaJavaTimeV)
    val scalaJavaTimeZone = Def.setting("io.github.cquiroz" %%% "scala-java-time-tzdb" % scalaJavaTimeV)
    val scalatest         = Def.setting("org.scalatest" %%% "scalatest" % "3.2.18" % Test)
    val tapirCore         = Def.setting("com.softwaremill.sttp.tapir" %%% "tapir-core" % tapirV)
    val zio               = Def.setting("dev.zio" %%% "zio" % zioV)
    val zioTest           = Def.setting("dev.zio" %%% "zio-test" % zioV)
    val zioTestSbt        = Def.setting("dev.zio" %%% "zio-test-sbt" % zioV)

    // A -> Z
    val circeParser                      = "io.circe"                      %% "circe-parser"                        % circeV
    val http4sBlazeServer                = "org.http4s"                    %% "http4s-blaze-server"                 % "0.23.16"
    val http4sDsl                        = "org.http4s"                    %% "http4s-dsl"                          % "0.23.27"
    val http4sServer                     = "org.http4s"                    %% "http4s-server"                       % "0.23.27"
    val jacksonModule                    = "com.fasterxml.jackson.module"  %% "jackson-module-scala"                % "2.17.0"
    val jodaTime                         = "joda-time"                      % "joda-time"                           % "2.12.7"
    val kindProjector                    = "org.typelevel"                 %% "kind-projector"                      % "0.13.3"
    val logback                          = "ch.qos.logback"                 % "logback-classic"                     % "1.2.13"
    val logbackCore                      = "ch.qos.logback"                 % "logback-core"                        % "1.2.13"
    val micrometerRegistryPrometheus     = "io.micrometer"                  % "micrometer-registry-prometheus"      % "1.12.5"
    val mockitoScala                     = "org.scalatestplus"             %% "mockito-3-4"                         % "3.2.10.0" % Test
    val openTelemetryExporterOtlp        = "io.opentelemetry"               % "opentelemetry-exporter-otlp"         % openTelemetryV
    val openTelemetryExporterLoggingOtlp = "io.opentelemetry"               % "opentelemetry-exporter-logging-otlp" % openTelemetryV
    val openTelemetrySdk                 = "io.opentelemetry"               % "opentelemetry-sdk"                   % openTelemetryV
    val openTelemetrySemconvIncubating   = "io.opentelemetry.semconv"       % "opentelemetry-semconv-incubating"    % "1.24.0-alpha"
    val play                             = "com.typesafe.play"             %% "play"                                % playV
    val playJson                         = "com.typesafe.play"             %% "play-json"                           % "2.10.4"
    val playReactiveMongo                = "org.reactivemongo"             %% "play2-reactivemongo"                 % s"$reactivemongoV-play28"
    val prometheusSimpleClientHttpServer = "io.prometheus"                  % "simpleclient_httpserver"             % "0.16.0"
    val reactiveMongo                    = "org.reactivemongo"             %% "reactivemongo"                       % reactivemongoV
    val redisson                         = "org.redisson"                   % "redisson"                            % "3.24.2"
    val refined                          = "eu.timepit"                    %% "refined"                             % "0.11.1"
    val scalaCollectionCompat            = "org.scala-lang.modules"        %% "scala-collection-compat"             % "2.12.0"
    val scalaLogging                     = "com.typesafe.scala-logging"    %% "scala-logging"                       % "3.9.5"
    val scalatestPlay                    = "org.scalatestplus.play"        %% "scalatestplus-play"                  % "5.1.0"    % Test
    val sourcecode                       = "com.lihaoyi"                   %% "sourcecode"                          % "0.4.1"
    val sttpCore                         = "com.softwaremill.sttp.client3" %% "core"                                % sttpV
    val sttpSlf4jBackend                 = "com.softwaremill.sttp.client3" %% "slf4j-backend"                       % sttpV
    val sttpZio                          = "com.softwaremill.sttp.client3" %% "zio"                                 % sttpV
    val tapirServer                      = "com.softwaremill.sttp.tapir"   %% "tapir-server"                        % tapirV
    val typesafeConfig                   = "com.typesafe"                   % "config"                              % "1.4.3"
    val zioConfig                        = "dev.zio"                       %% "zio-config"                          % zioConfigV
    val zioConfigMagnolia                = "dev.zio"                       %% "zio-config-magnolia"                 % zioConfigV
    val zioConfigEnumeratum              = "dev.zio"                       %% "zio-config-enumeratum"               % zioConfigV
    val zioInteropCats                   = "dev.zio"                       %% "zio-interop-cats"                    % "23.1.0.2"
    val zioMetricsConnectorsMicrometer   = "dev.zio"                       %% "zio-metrics-connectors-micrometer"   % "2.3.1"
    val zioOpenTelemetry                 = "dev.zio"                       %% "zio-opentelemetry"                   % "3.0.0-RC22"
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
