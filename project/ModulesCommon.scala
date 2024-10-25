import Dependencies.Libraries
import sbt.Keys.*
import locales.{CLDRVersion, LocalesFilter}
import sbt.{Project, Provided, Test}
import sbtcrossproject.CrossProject
import scalajscrossproject.ScalaJSCrossPlugin.autoImport.*
import locales.LocalesPlugin.autoImport.*

object ModulesCommon {

  lazy val crossCollectionProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-cross-collection")

  lazy val dateProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-date")

  lazy val http4sServerProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-http4s-server")
    .settings(libraryDependencies ++= Seq(Libraries.http4sServer))

  lazy val integrationCheckProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-integration-check")

  lazy val localesMinimalDbProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-locales-minimal-db")
    .settings(
      cldrVersion := CLDRVersion.Version("45.0"), // http://unicode.org/Public/cldr/
      localesFilter := LocalesFilter.Selection("ru", "en"),
      libraryDependencies ++= Seq(Libraries.scalaJavaLocales.value)
    )

  lazy val logbackConfigProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-logback-config")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.logbackCore % Provided, Libraries.typesafeConfig % Provided)
    )

  lazy val logbackLayoutProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-logback-layout")
    .settings(libraryDependencies ++= Seq(Libraries.logbackClassic % Provided))

  lazy val nullableCoreProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-nullable-core")

  lazy val nullableCodecCirceProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-nullable-codec-circe")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.circeCore.value % Provided, Libraries.circeGeneric.value % Test)
    )

  lazy val nullableCodecTapirProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-nullable-codec-tapir")
    .settings(libraryDependencies ++= Seq(Libraries.tapirCore.value % Provided))

  lazy val scalaLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-scala-logging")
    .settings(Test / parallelExecution := false)
    .settings(libraryDependencies ++= Seq(Libraries.logbackClassic, Libraries.scalaLogging))

  lazy val reactivemongoBaseProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-base")
    .settings(libraryDependencies ++= Seq(Libraries.reactiveMongo % Provided, Libraries.sourcecode))

  lazy val reactivemongoBsonProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-reactivemongo-bson")

  lazy val reactivemongoBsonAnyProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-any")
    .settings(libraryDependencies ++= Seq(Libraries.reactiveMongo % Provided))

  lazy val reactivemongoBsonJodaTimeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-joda-time")
    .settings(libraryDependencies ++= Seq(Libraries.reactiveMongo % Provided, Libraries.jodaTime))

  lazy val reactivemongoBsonRefinedProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-refined")
    .settings(libraryDependencies ++= Seq(Libraries.reactiveMongo % Provided, Libraries.refined))

  lazy val reactivemongoBsonZioPreludeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-zio-prelude")
    .settings(
      libraryDependencies ++= Seq(Libraries.reactiveMongo % Provided, Libraries.zioPrelude.value)
    )

  lazy val redissonCoreProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-core")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.redisson, Libraries.jacksonModule, Libraries.scalaCollectionCompat)
    )

  lazy val redissonCodecBaseProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-redisson-codec-base")

  lazy val redissonCodecCirceProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-redisson-codec-circe")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.circeCore.value % Provided, Libraries.circeParser % Provided)
    )

  lazy val redissonCodecPlayJsonProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-codec-play-json")
    .settings(libraryDependencies ++= Seq(Libraries.playJson % Provided))

}
