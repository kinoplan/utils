import sbt.Keys._
import locales.{CLDRVersion, LocalesFilter}
import sbt.{Project, Provided, Test}
import sbtcrossproject.CrossProject
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import locales.LocalesPlugin.autoImport._

object ModulesCommon {

  lazy val dateProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-date")

  lazy val http4sServerProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-http4s-server")
    .settings(libraryDependencies ++= Seq(Dependencies.http4sServer))

  lazy val integrationCheckProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-integration-check")

  lazy val localesMinimalDbProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-locales-minimal-db")
    .settings(
      cldrVersion := CLDRVersion.Version("43.1"), // http://unicode.org/Public/cldr/
      localesFilter := LocalesFilter.Selection("ru", "en"),
      libraryDependencies ++= Seq(Dependencies.scalaJavaLocales.value)
    )

  lazy val logbackConfigProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-logback-config")
    .settings(
      libraryDependencies ++=
        Seq(Dependencies.logbackCore % Provided, Dependencies.typesafeConfig % Provided)
    )

  lazy val scalaLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-scala-logging")
    .settings(Test / parallelExecution := false)
    .settings(libraryDependencies ++= Seq(Dependencies.logback, Dependencies.scalaLogging))

  lazy val reactivemongoBaseProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-base")
    .settings(
      libraryDependencies ++= Seq(Dependencies.reactiveMongo % Provided, Dependencies.sourcecode)
    )

  lazy val reactivemongoBsonAnyProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-any")
    .settings(libraryDependencies ++= Seq(Dependencies.reactiveMongo % Provided))

  lazy val reactivemongoBsonJodaTimeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-joda-time")
    .settings(
      libraryDependencies ++= Seq(Dependencies.reactiveMongo % Provided, Dependencies.jodaTime)
    )

  lazy val reactivemongoBsonRefinedProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-refined")
    .settings(
      libraryDependencies ++= Seq(Dependencies.reactiveMongo % Provided, Dependencies.refined)
    )

  lazy val redissonProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .configure(ProjectSettings.integrationTestProfile)
    .settings(name := "utils-redisson-core")
    .settings(libraryDependencies ++= Seq(Dependencies.redisson, Dependencies.jacksonModule))

  lazy val redissonCodecCirceProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-codec-circe")
    .settings(
      libraryDependencies ++=
        Seq(Dependencies.circeCore % Provided, Dependencies.circeParser % Provided)
    )

  lazy val redissonCodecPlayJsonProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-codec-play-json")
    .settings(libraryDependencies ++= Seq(Dependencies.playJson % Provided))

}
