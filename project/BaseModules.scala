import sbt.Keys._
import sbt.{Project, Provided, Test}

object BaseModules {

  lazy val logbackConfigProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-logback-config")
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.logbackCore    % Provided,
          Dependencies.typesafeConfig % Provided
        )
    )

  lazy val scalaLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-scala-logging")
    .settings(Test / parallelExecution := false)
    .settings(
      libraryDependencies ++= Seq(Dependencies.logback, Dependencies.scalaLogging)
    )

  lazy val reactivemongoBsonJodaTimeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-joda-time")
    .settings(
      libraryDependencies ++= Seq(Dependencies.reactiveMongo % Provided, Dependencies.jodaTime)
    )

}
