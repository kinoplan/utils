import sbt.Keys._
import sbt.{Project, Provided}

object ModulesPlay {

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

}
