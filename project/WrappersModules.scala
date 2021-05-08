import sbt.Keys._
import sbt.Project

object WrappersModules {

  lazy val baseLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-wrappers-scala-logging")
    .settings(
      libraryDependencies ++= Seq(Dependencies.logback, Dependencies.scalaLogging)
    )

}
