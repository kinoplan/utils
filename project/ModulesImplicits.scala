import Dependencies.Libraries
import org.scalajs.sbtplugin.ScalaJSJUnitPlugin
import sbt.Keys.*
import sbt.*

object ModulesImplicits {

  lazy val anyProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-implicits-any")

  lazy val booleanProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-implicits-boolean")

  lazy val collectionProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-implicits-collection")

  lazy val identityProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-implicits-identity")

  lazy val javaTimeProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-implicits-java-time")

  val javaTimeJsProfile: Project => Project = _
    .enablePlugins(ScalaJSJUnitPlugin)
    .settings(
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

  lazy val zioProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.zioTestProfile)
    .settings(name := "utils-implicits-zio")
    .settings(libraryDependencies ++= Seq(Libraries.zio.value % Provided))

  lazy val zioJvmProfile: Project => Project =
    _.configure(ProjectSettings.unmanagedSourceProfile("scalajvm"))

  lazy val zioJsProfile: Project => Project =
    _.configure(ProjectSettings.scalaJsProfile, ProjectSettings.unmanagedSourceProfile("scalajs"))

  lazy val zioPreludeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-implicits-zio-prelude")
    .settings(libraryDependencies ++= Seq(Libraries.zioPrelude.value))

}
