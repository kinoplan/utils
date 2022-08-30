import sbt._

object Dependencies {

  object Versions {
    val playV          = "2.8.16"
    val reactivemongoV = "1.0.10"
    val zioConfigV     = "3.0.0-RC9"
  }

  import Versions._

  // A -> Z
  val jodaTime             = "joda-time"                   % "joda-time"              % "2.11.1"
  val logback              = "ch.qos.logback"              % "logback-classic"        % "1.4.0"
  val logbackCore          = "ch.qos.logback"              % "logback-core"           % "1.4.0"
  val mockitoScala         = "org.scalatestplus"          %% "mockito-3-4"            % "3.2.10.0" % Test
  val play                 = "com.typesafe.play"          %% "play"                   % playV
  val playReactiveMongo    = "org.reactivemongo"          %% "play2-reactivemongo"    % s"$reactivemongoV-play28"
  val reactiveMongo        = "org.reactivemongo"          %% "reactivemongo"          % reactivemongoV
  val scalaLogging         = "com.typesafe.scala-logging" %% "scala-logging"          % "3.9.5"
  val scalatest            = "org.scalatest"              %% "scalatest"              % "3.2.13"   % Test
  val scalastic            = "org.scalactic"              %% "scalactic"              % "3.2.13"
  val typesafeConfig       = "com.typesafe"                % "config"                 % "1.4.2"
  val zio                  = "dev.zio"                    %% "zio"                    % "2.0.0-RC6"
  val zioConfig            = "dev.zio"                    %% "zio-config"             % zioConfigV
  val zioConfigTypesafe    = "dev.zio"                    %% "zio-config-typesafe"    % zioConfigV
  val zioHttp              = "io.d11"                     %% "zhttp"                  % "2.0.0-RC9"
  val zioMetricsPrometheus = "dev.zio"                    %% "zio-metrics-prometheus" % "2.0.0-RC6"
  val zioPrelude           = "dev.zio"                    %% "zio-prelude"            % "1.0.0-RC14"
}
