import sbt.Keys._
import sbt._
import sbtcrossproject.CrossProject
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._

object ModulesImplicits {

  lazy val anyProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-implicits-any")

  lazy val booleanProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-implicits-boolean")

  lazy val collectionProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-implicits-collection")

  lazy val javaTimeProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-implicits-java-time")
    .jsSettings(
      libraryDependencies ++=
        Seq(
          Dependencies.scalaJavaTime.value     % Provided,
          Dependencies.scalaJavaLocales.value  % Test,
          Dependencies.scalaJavaTimeZone.value % Test,
          Dependencies.localesFullDb.value     % Test
        )
    )

  lazy val jodaTimeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-implicits-joda-time")
    .settings(libraryDependencies ++= Seq(Dependencies.jodaTime))

}
