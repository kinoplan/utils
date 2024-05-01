import Dependencies.{Batches, Libraries}
import sbt.Keys.*
import sbt.*
import sbtcrossproject.CrossProject
import scalajscrossproject.ScalaJSCrossPlugin.autoImport.*

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
          Libraries.scalaJavaTime.value     % Provided,
          Libraries.scalaJavaLocales.value  % Test,
          Libraries.scalaJavaTimeZone.value % Test,
          Libraries.localesFullDb.value     % Test
        )
    )

  lazy val jodaTimeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-implicits-joda-time")
    .settings(libraryDependencies ++= Seq(Libraries.jodaTime))

  lazy val zioProfile: CrossProject => CrossProject = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.zioTestProfile)
    .jsConfigure(ProjectSettings.scalaJsProfile)
    .settings(name := "utils-implicits-zio")
    .settings(libraryDependencies ++= Seq(Libraries.zio.value % Provided))

}
