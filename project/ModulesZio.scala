import Dependencies.{Libraries, Shades}
import sbt.Keys.*
import sbt.*

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
          Libraries.zio.value,
          Libraries.zioInteropCats
        )
    )

  lazy val integrationCheckProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-integration-check")
    .settings(libraryDependencies ++= Seq(Libraries.zio.value))

  lazy val monitoringPrometheusProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.shadingProfile(Shades.zioConfig))
    .settings(name := "utils-zio-monitoring-prometheus")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.micrometerRegistryPrometheus,
          Libraries.prometheusExporterHttpServer,
          Libraries.zio.value,
          Libraries.zioMetricsConnectorsMicrometer
        )
    )

  lazy val openTelemetryProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.shadingProfile(Shades.zioConfig))
    .settings(name := "utils-zio-opentelemetry")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.openTelemetrySdk,
          Libraries.openTelemetryExporterOtlp,
          Libraries.openTelemetryExporterLoggingOtlp,
          Libraries.openTelemetrySemconvIncubating,
          Libraries.zio.value,
          Libraries.zioOpenTelemetry
        )
    )

  lazy val reactivemongoProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.shadingProfile(Shades.zioConfig))
    .settings(name := "utils-zio-reactivemongo")
    .settings(libraryDependencies ++= Seq(Libraries.reactiveMongo % Provided, Libraries.zio.value))

  lazy val redissonProfile: Project => Project = _
    .configure(
      ProjectSettings.commonProfile,
      ProjectSettings.zioTestProfile,
      ProjectSettings.shadingProfile(Shades.zioConfig)
    )
    .settings(name := "utils-zio-redisson")
    .settings(Test / testOptions += Tests.Filter(_.endsWith("MainSpec")))
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.logbackClassic % Test,
          Libraries.redisson,
          Libraries.testContainersRedis,
          Libraries.zio.value,
          Libraries.zioStreams.value
        )
    )

  lazy val sttpLoggingSlf4jProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-sttp-slf4j-backend")
    .settings(libraryDependencies ++= Seq(Libraries.sttpSlf4jBackend, Libraries.zio.value % Provided))

  lazy val sttpOpenTelemetryProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-sttp-opentelemetry-backend")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.sttpCore, Libraries.sttpZio, Libraries.zio.value, Libraries.zioOpenTelemetry)
    )

  lazy val tapirServerProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.kindProjectorProfile)
    .settings(name := "utils-zio-tapir-server")
    .settings(libraryDependencies ++= Seq(Libraries.tapirServer, Libraries.zio.value % Provided))

  lazy val tapirOpenTelemetryProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.kindProjectorProfile)
    .settings(name := "utils-zio-tapir-opentelemetry")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.openTelemetrySemconv,
          Libraries.tapirServer,
          Libraries.zio.value,
          Libraries.zioOpenTelemetry
        )
    )

}
