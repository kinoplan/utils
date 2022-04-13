import sbt.Keys._
import sbt.Project

object ImplicitsModules {

  lazy val booleanProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-implicits-boolean")

  lazy val collectionProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-implicits-collection")

}
