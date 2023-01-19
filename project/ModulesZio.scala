import sbt.Keys._
import sbt.Project

object ModulesZio {

  lazy val http4sHealthcheckProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.kindProjectorProfile)
    .settings(name := "utils-zio-http4s-healthcheck")
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.http4sBlazeServer,
          Dependencies.http4sDsl,
          Dependencies.zio,
          Dependencies.zioInteropCats,
          Dependencies.zioConfig,
          Dependencies.zioConfigTypesafe,
          Dependencies.zioConfigMagnolia
        )
    )

  lazy val integrationCheckProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.scalaJsProfile)
    .settings(name := "utils-zio-integration-check")
    .settings(libraryDependencies ++= Seq(Dependencies.zio))

  lazy val monitoringPrometheusProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-monitoring-prometheus")
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.zio,
          Dependencies.zioConfig,
          Dependencies.zioConfigTypesafe,
          Dependencies.zioMetricsPrometheus
        )
    )

  lazy val reactivemongoProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-reactivemongo")
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.reactiveMongo,
          Dependencies.zio,
          Dependencies.zioConfig,
          Dependencies.zioConfigTypesafe,
          Dependencies.zioPrelude
        )
    )

}
