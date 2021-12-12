import sbt.Keys._
import sbt.{Project, Provided}

object BaseModules {

  lazy val reactivemongoBsonJodaTimeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-joda-time")
    .settings(
      libraryDependencies ++= Seq(Dependencies.reactiveMongo % Provided, Dependencies.jodaTime)
    )

}
