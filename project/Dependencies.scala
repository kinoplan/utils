import sbt.*

object Dependencies {

  object Versions {
    val reactivemongoV = "1.0.10"
    val zioConfigV     = "3.0.1"
  }

  import Versions.*

  // A -> Z
//  val distageCore          = "io.7mind.izumi"             %% "distage-core"           % "1.0.10"
  val jodaTime             = "joda-time"                   % "joda-time"              % "2.10.14"
  val logback              = "ch.qos.logback"              % "logback-classic"        % "1.2.11"
  val logbackCore          = "ch.qos.logback"              % "logback-core"           % "1.2.11"
  val mockitoScala         = "org.scalatestplus"          %% "mockito-3-4"            % "3.2.10.0" % Test
  val reactiveMongo        = "org.reactivemongo"          %% "reactivemongo"          % reactivemongoV
  val scalaLogging         = "com.typesafe.scala-logging" %% "scala-logging"          % "3.9.5"
  val scalatest            = "org.scalatest"              %% "scalatest"              % "3.2.12"   % Test
  val typesafeConfig       = "com.typesafe"                % "config"                 % "1.4.2"
  val zio                  = "dev.zio"                    %% "zio"                    % "2.0.0-RC6"
  val zioConfig            = "dev.zio"                    %% "zio-config"             % zioConfigV
  val zioConfigTypesafe    = "dev.zio"                    %% "zio-config-typesafe"    % zioConfigV
  val zioHttp              = "io.d11"                     %% "zhttp"                  % "2.0.0-RC9"
  val zioMetricsPrometheus = "dev.zio"                    %% "zio-metrics-prometheus" % "2.0.0-RC6"
  val zioPrelude           = "dev.zio"                    %% "zio-prelude"            % "1.0.0-RC14"
}
