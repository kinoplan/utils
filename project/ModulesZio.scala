import sbt.Keys._
import sbt.{Project, Provided}

object ModulesZio {

  lazy val httpHealthcheckProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-http-healthcheck")
    .settings(libraryDependencies ++= Seq(Dependencies.zio, Dependencies.zioHttp))

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
