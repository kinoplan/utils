import sbt.Keys.*
import sbt.*
import scalafix.sbt.ScalafixPlugin
import scoverage.ScoverageKeys.*

object ProjectSettings {

  lazy val commonProfile: Project => Project = _
    .enablePlugins(ScalafixPlugin)
    .settings(
      crossScalaVersions := Seq("2.12.15", "2.13.8"),
      scalaVersion := crossScalaVersions.value.last,
      scalacOptions ~=
        (_.filterNot(
          Set(
            "-Wdead-code",
            "-Wunused:params",
            "-Ywarn-dead-code",
            "-Ywarn-unused:params",
            "-Ywarn-unused:patvars",
            "-Wunused:explicits",
            "-Xlint:infer-any",
            "-Ywarn-infer-any"
          )
        )),
      Test / fork := true,
      Test / javaOptions += "-Duser.timezone=UTC",
      libraryDependencies ++= Seq(Dependencies.scalatest, Dependencies.mockitoScala),
      coverageHighlighting := true
    )

}
