import sbt.Keys._
import sbt._
import scalafix.sbt.ScalafixPlugin
import scoverage.ScoverageKeys._

object ProjectSettings {

  lazy val commonProfile: Project => Project = _.enablePlugins(ScalafixPlugin).settings(
    crossScalaVersions := Seq("2.12.11", "2.13.5"),
    scalaVersion := crossScalaVersions.value.last,
    scalacOptions ~=
      (_.filterNot(Set(
        "-Wdead-code",
        "-Wunused:params",
        "-Ywarn-dead-code",
        "-Ywarn-unused:params",
        "-Ywarn-unused:patvars",
        "-Wunused:explicits"
      ))),
    libraryDependencies ++= Seq(Dependencies.scalatest.value),
    coverageMinimum := 80,
    coverageFailOnMinimum := true,
    coverageHighlighting := true,
    testFrameworks += new TestFramework("munit.Framework")
  )

  lazy val rootProfile: Project => Project = _.settings(
    name := "utils",
    skip in publish := true,
    skip in publishLocal := true,
    skip in publishArtifact := true,
    Keys.`package` := file("")
  )

}
