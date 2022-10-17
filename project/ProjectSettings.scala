import sbt.Keys._
import sbt._
import scalafix.sbt.ScalafixPlugin
import scoverage.ScoverageKeys._

object ProjectSettings {

  lazy val commonProfile: Project => Project = _
    .enablePlugins(ScalafixPlugin)
    .settings(
      crossScalaVersions := Seq("2.12.16", "2.13.10"),
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
      libraryDependencies ++= Seq(Dependencies.scalatest.value, Dependencies.mockitoScala),
      coverageHighlighting := true
    )

  lazy val scalaJsProfile: Project => Project = _.settings(Test / fork := false)

}
