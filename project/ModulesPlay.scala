import sbt.Keys._
import sbt.{Defaults, IntegrationTest, Project, Provided, Test, inConfig}

object ModulesPlay {

  lazy val reactivemongoProfile: Project => Project = _
    .configs(IntegrationTest)
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-play-reactivemongo")
    .settings(
      Defaults.itSettings,
      libraryDependencies ++=
        Seq(
          Dependencies.play              % Provided,
          Dependencies.playReactiveMongo % Provided,
          Dependencies.scalastic,
          Dependencies.logback         % IntegrationTest,
          Dependencies.scalatest.value % IntegrationTest,
          Dependencies.testcontainersScalatest,
          Dependencies.testcontainersMongodb
        )
    )

}
