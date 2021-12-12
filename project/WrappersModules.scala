import sbt.Keys._
import sbt._

object WrappersModules {

  lazy val baseLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-wrappers-scala-logging")
    .settings(Test / parallelExecution := false)
    .settings(
      libraryDependencies ++= Seq(Dependencies.logback, Dependencies.scalaLogging)
    )

}
