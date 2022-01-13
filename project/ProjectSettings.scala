import sbt.Keys._
import sbt._
import scalafix.sbt.ScalafixPlugin
import scoverage.ScoverageKeys._

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
            "-Wunused:explicits"
          )
        )),
      libraryDependencies ++= Seq(Dependencies.scalatest, Dependencies.mockitoScala),
      coverageHighlighting := true
    )

}
