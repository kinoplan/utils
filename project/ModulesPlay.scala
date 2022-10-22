import sbt.Keys._
import sbt.{Project, Provided}

object ModulesPlay {

  lazy val errorHandlerProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-error-handler")
    .settings(libraryDependencies ++= Seq(Dependencies.play % Provided, Dependencies.scalatestPlay))

  lazy val filtersLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-filters-logging")
    .settings(libraryDependencies ++= Seq(Dependencies.play % Provided, Dependencies.scalatestPlay))

  lazy val reactivemongoProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-reactivemongo")
    .settings(
      libraryDependencies ++=
        Seq(
          Dependencies.play              % Provided,
          Dependencies.playReactiveMongo % Provided,
          Dependencies.scalastic
        )
    )

  lazy val requestMapContextProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-request-map-context")
    .settings(libraryDependencies ++= Seq(Dependencies.play % Provided))

}
