import Dependencies.Libraries
import sbt.Keys.*
import sbt.{Project, Provided}

object ModulesPlay {

  lazy val errorHandlerProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-error-handler")
    .settings(libraryDependencies ++= Seq(Libraries.play % Provided, Libraries.scalatestPlay))

  lazy val filtersLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-filters-logging")
    .settings(libraryDependencies ++= Seq(Libraries.play % Provided, Libraries.scalatestPlay))

  lazy val reactivemongoProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-reactivemongo")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.play % Provided, Libraries.playReactiveMongo % Provided, Libraries.kamonCore)
    )

  lazy val requestMapContextProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-request-map-context")
    .settings(libraryDependencies ++= Seq(Libraries.play % Provided))

}
