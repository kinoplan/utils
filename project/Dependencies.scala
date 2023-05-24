import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {

  object Versions {
    val circeV         = "0.14.5"
    val playV          = "2.8.19"
    val reactivemongoV = "1.0.10"
    val scalaJavaTimeV = "2.5.0"
    val sttpV          = "3.8.15"
    val tapirV         = "1.3.0"
    val zioConfigV     = "3.0.7"
  }

  import Versions._

  // Cross-platform dependencies
  val localesFullDb     = Def.setting("io.github.cquiroz" %%% "locales-full-db" % "1.5.1")
  val scalaJavaLocales  = Def.setting("io.github.cquiroz" %%% "scala-java-locales" % "1.5.1")
  val scalaJavaTime     = Def.setting("io.github.cquiroz" %%% "scala-java-time" % scalaJavaTimeV)
  val scalaJavaTimeZone = Def.setting("io.github.cquiroz" %%% "scala-java-time-tzdb" % scalaJavaTimeV)
  val scalatest         = Def.setting("org.scalatest" %%% "scalatest" % "3.2.15" % Test)

  // A -> Z
  val circeCore            = "io.circe"                      %% "circe-core"             % circeV
  val circeParser          = "io.circe"                      %% "circe-parser"           % circeV
  val http4sBlazeServer    = "org.http4s"                    %% "http4s-blaze-server"    % "0.23.14"
  val http4sDsl            = "org.http4s"                    %% "http4s-dsl"             % "0.23.18"
  val http4sServer         = "org.http4s"                    %% "http4s-server"          % "0.23.18"
  val jacksonModule        = "com.fasterxml.jackson.module"  %% "jackson-module-scala"   % "2.15.0"
  val jodaTime             = "joda-time"                      % "joda-time"              % "2.12.5"
  val kindProjector        = "org.typelevel"                 %% "kind-projector"         % "0.13.2"
  val logback              = "ch.qos.logback"                 % "logback-classic"        % "1.2.12"
  val logbackCore          = "ch.qos.logback"                 % "logback-core"           % "1.2.12"
  val mockitoScala         = "org.scalatestplus"             %% "mockito-3-4"            % "3.2.10.0" % Test
  val play                 = "com.typesafe.play"             %% "play"                   % playV
  val playJson             = "com.typesafe.play"             %% "play-json"              % "2.9.4"
  val playReactiveMongo    = "org.reactivemongo"             %% "play2-reactivemongo"    % s"$reactivemongoV-play28"
  val reactiveMongo        = "org.reactivemongo"             %% "reactivemongo"          % reactivemongoV
  val redisson             = "org.redisson"                   % "redisson"               % "3.21.0"
  val refined              = "eu.timepit"                    %% "refined"                % "0.10.3"
  val scalaLogging         = "com.typesafe.scala-logging"    %% "scala-logging"          % "3.9.5"
  val scalastic            = "org.scalactic"                 %% "scalactic"              % "3.2.15"
  val scalatestPlay        = "org.scalatestplus.play"        %% "scalatestplus-play"     % "5.1.0"    % Test
  val sttpSlf4jBackend     = "com.softwaremill.sttp.client3" %% "slf4j-backend"          % sttpV
  val tapirServer          = "com.softwaremill.sttp.tapir"   %% "tapir-server"           % tapirV
  val typesafeConfig       = "com.typesafe"                   % "config"                 % "1.4.2"
  val zio                  = "dev.zio"                       %% "zio"                    % "2.0.13"
  val zioConfig            = "dev.zio"                       %% "zio-config"             % zioConfigV
  val zioConfigTypesafe    = "dev.zio"                       %% "zio-config-typesafe"    % zioConfigV
  val zioConfigMagnolia    = "dev.zio"                       %% "zio-config-magnolia"    % zioConfigV
  val zioInteropCats       = "dev.zio"                       %% "zio-interop-cats"       % "23.0.0.5"
  val zioMetricsPrometheus = "dev.zio"                       %% "zio-metrics-prometheus" % "2.0.1"
}
