import sbt.Keys._
import sbt.Project

object ImplicitsModules {
  lazy val booleanProfile: Project => Project = project => {
    project
      .configure(ProjectSettings.commonProfile)
      .settings(name := "utils-implicits-boolean")
  }

  lazy val collectionProfile: Project => Project = project => {
    project
      .configure(ProjectSettings.commonProfile)
      .settings(name := "utils-implicits-collection")
  }
}
