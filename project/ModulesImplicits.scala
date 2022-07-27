import sbt.Keys._
import sbt.{Project, Test}

object ModulesImplicits {

  lazy val booleanProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-implicits-boolean")

  lazy val collectionProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-implicits-collection")

  lazy val jodaTimeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-implicits-joda-time")
    .settings(
      libraryDependencies ++= Seq(Dependencies.jodaTime),
      Test / javaOptions ++= Seq("-Duser.language=ru")
    )

}
