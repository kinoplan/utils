import sbt.Keys._
import sbt.Project

object ImplicitsModules {
  lazy val collectionProfile: Project => Project = project => {
    project
      .configure(ProjectSettings.commonProfile)
      .settings(name := "utils-implicits-collection")
  }
}
