import Dependencies.Libraries
import sbt.Keys.*
import sbt.*

object ModulesPlay {

  // play 2.x.x

  lazy val errorHandler2Profile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play2-error-handler")
    .settings(libraryDependencies ++= Seq(Libraries.play2 % Provided, Libraries.scalatestPlay2))

  lazy val filtersLogging2Profile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.unmanagedSourceProfile("play-2"))
    .settings(name := "utils-play2-filters-logging")
    .settings(libraryDependencies ++= Seq(Libraries.play2 % Provided, Libraries.scalatestPlay2))

  lazy val reactivemongo2Profile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play2-reactivemongo")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.play2              % Provided,
          Libraries.play2ReactiveMongo % Provided,
          Libraries.kamonCore          % Provided
        )
    )

  lazy val requestMapContext2Profile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play2-request-map-context")
    .settings(libraryDependencies ++= Seq(Libraries.play2 % Provided))

  // play 3.x.x

  lazy val errorHandlerProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-error-handler")
    .settings(libraryDependencies ++= Seq(Libraries.play % Provided, Libraries.scalatestPlay))

  lazy val filtersLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile, ProjectSettings.unmanagedSourceProfile("play-3"))
    .settings(name := "utils-play-filters-logging")
    .settings(libraryDependencies ++= Seq(Libraries.play % Provided, Libraries.scalatestPlay))

  lazy val reactivemongoProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-reactivemongo")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.play              % Provided,
          Libraries.playReactiveMongo % Provided,
          Libraries.kamonCore         % Provided
        )
    )

  lazy val requestMapContextProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-request-map-context")
    .settings(libraryDependencies ++= Seq(Libraries.play % Provided))

}
