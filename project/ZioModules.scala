import sbt.Keys._
import sbt.{Project, Provided}

object ZioModules {

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
          Dependencies.zioLoggingSlf4j,
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
