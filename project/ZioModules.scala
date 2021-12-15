import sbt.Keys._
import sbt.{Project, Provided}

object ZioModules {

  lazy val httpHealthcheckProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-http-healthcheck")
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.zio % Provided,
          Dependencies.zioHttp,
          Dependencies.zioLogging
        )
    )

  lazy val monitoringPrometheusProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-monitoring-prometheus")
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.distageCore % Provided,
          Dependencies.zio         % Provided,
          Dependencies.zioConfig,
          Dependencies.zioConfigTypesafe,
          Dependencies.zioLogging,
          Dependencies.zioMetricsPrometheus
        )
    )

  lazy val reactivemongoProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-reactivemongo")
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.distageCore % Provided,
          Dependencies.reactiveMongo,
          Dependencies.zio % Provided,
          Dependencies.zioConfig,
          Dependencies.zioConfigTypesafe,
          Dependencies.zioPrelude
        )
    )

}
