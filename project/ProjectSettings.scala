import sbt.Keys._
import sbt._
import scalafix.sbt.ScalafixPlugin

object ProjectSettings {
  lazy val commonProfile: Project => Project =
    _.enablePlugins(ScalafixPlugin)
      .settings(
      crossScalaVersions := Seq("2.12.11", "2.13.4"),
      scalaVersion := crossScalaVersions.value.last,
      javacOptions ++= Seq(
        "-source", "1.8",
        "-target", "1.8",
        "-Xlint"
      ),
      scalacOptions ~= (_.filterNot(
        Set(
          "-Wdead-code",
          "-Wunused:params",
          "-Ywarn-dead-code",
          "-Ywarn-unused:params",
          "-Ywarn-unused:patvars",
          "-Wunused:explicits"
        )
      )),
      libraryDependencies ++= Seq(Dependencies.scalatest.value)
    )

  lazy val rootProfile: Project => Project = _.settings(
    name := "utils",
    skip in publish := true,
    skip in publishLocal := true,
    skip in publishArtifact := true,
    Keys.`package` := file("")
  )
}