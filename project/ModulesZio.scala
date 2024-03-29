import Dependencies.{Libraries, Shades}
import sbt.Keys.*
import sbt.{Project, Provided}

object ModulesZio {

  lazy val http4sHealthcheckProfile: Project => Project = _
    .configure(
      ProjectSettings.commonProfile,
      ProjectSettings.kindProjectorProfile,
      ProjectSettings.shadingProfile(Shades.zioConfig)
    )
    .settings(name := "utils-zio-http4s-healthcheck")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.http4sBlazeServer,
          Libraries.http4sDsl,
          Libraries.zio,
          Libraries.zioInteropCats
        )
    )

  lazy val integrationCheckProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.scalaJsProfile)
    .settings(name := "utils-zio-integration-check")
    .settings(libraryDependencies ++= Seq(Libraries.zio))

  lazy val monitoringPrometheusProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.shadingProfile(Shades.zioConfig))
    .settings(name := "utils-zio-monitoring-prometheus")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.micrometerRegistryPrometheus,
          Libraries.prometheusSimpleClientHttpServer,
          Libraries.zio,
          Libraries.zioMetricsConnectorsMicrometer
        )
    )

  lazy val reactivemongoProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.shadingProfile(Shades.zioConfig))
    .settings(name := "utils-zio-reactivemongo")
    .settings(libraryDependencies ++= Seq(Libraries.reactiveMongo, Libraries.zio))

  lazy val sttpLoggingSlf4jProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-sttp-slf4j-backend")
    .settings(libraryDependencies ++= Seq(Libraries.sttpSlf4jBackend, Libraries.zio % Provided))

  lazy val tapirServerProfile: Project => Project = _
    .configure(
      ProjectSettings.commonProfile,
      ProjectSettings.scalaJsProfile,
      ProjectSettings.kindProjectorProfile
    )
    .settings(name := "utils-zio-tapir-server")
    .settings(libraryDependencies ++= Seq(Libraries.tapirServer, Libraries.zio % Provided))

}
