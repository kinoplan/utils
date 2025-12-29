import Dependencies.Libraries
import org.typelevel.sbt.tpolecat.TpolecatPlugin.autoImport.{
  tpolecatExcludeOptions,
  tpolecatScalacOptions
}
import org.typelevel.scalacoptions.ScalacOptions
import sbt.*
import sbt.Keys.*
import sbtide.Keys.ideSkipProject
import scalafix.sbt.ScalafixPlugin
import scoverage.ScoverageKeys.*

object ProjectSettings {

  private val namespace = "io.kinoplan.utils"

  val scala2_12 = "2.12.21"
  val scala2_13 = "2.13.18"
  val scala3 = "3.3.7"

  val ideScalaVersion: String = scala2_13

  val scala2Versions: Seq[String] = Seq(scala2_12, scala2_13)
  val scala2_13Versions: Seq[String] = Seq(scala2_13)
  val scala2And3Versions: Seq[String] = scala2Versions ++ Seq(scala3)
  val scala2_13And3Versions: Seq[String] = Seq(scala2_13, scala3)
  val scala3Versions: Seq[String] = Seq(scala3)

  private def unmanaged(version: String, base: File): Seq[File] =
    CrossVersion.partialVersion(version) match {
      case Some((2, n)) if n < 13 => Seq(base / "scala-2.13-")
      case _                      => Seq(base / "scala-2.13+")
    }

  lazy val commonProfile: Project => Project = _
    .enablePlugins(ScalafixPlugin)
    .settings(
      Compile / unmanagedSourceDirectories ++=
        unmanaged(scalaVersion.value, (Compile / sourceDirectory).value),
      Test / unmanagedSourceDirectories ++=
        unmanaged(scalaVersion.value, (Test / sourceDirectory).value),
      ideSkipProject :=
        (scalaVersion.value != ideScalaVersion) ||
        thisProjectRef.value.project.contains("Native") ||
        thisProjectRef.value.project.contains("JS"),
      tpolecatScalacOptions ++= Set(ScalacOptions.explain),
      tpolecatExcludeOptions :=
        Set(
//          ScalacOptions.fatalWarnings,
          ScalacOptions.warnError,
          ScalacOptions.warnUnusedParams,
          ScalacOptions.warnUnusedPatVars,
          ScalacOptions.warnValueDiscard,
          ScalacOptions.warnDeadCode,
          ScalacOptions.lintInferAny,
          ScalacOptions.warnUnusedExplicits,
          ScalacOptions.warnNonUnitStatement
        ),
      scalacOptions ++= {
        if (ScalaArtifacts.isScala3(scalaVersion.value)) Seq("-Xmax-inlines", "64")
        else Nil
      },
      Test / tpolecatExcludeOptions ++=
        Set(ScalacOptions.privateWarnDeadCode, ScalacOptions.warnNonUnitStatement),
      Test / fork := true,
      Test / javaOptions += "-Duser.timezone=UTC",
      coverageHighlighting := true,
      libraryDependencies ++= Seq(Libraries.scalatest.value, Libraries.mockitoScala)
    )

  val scalaJsSettings = Seq(Test / fork := false)
  lazy val scalaJsProfile: Project => Project = _.settings(scalaJsSettings)

  lazy val zioTestProfile: Project => Project = _.settings(
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= Seq(Libraries.zioTest.value, Libraries.zioTestSbt.value)
  )

  lazy val kindProjectorProfile: Project => Project = _.settings(
    scalacOptions ++= {
      if (ScalaArtifacts.isScala3(scalaVersion.value)) Seq("-Ykind-projector")
      else Nil
    },
    libraryDependencies ++= {
      if (ScalaArtifacts.isScala3(scalaVersion.value)) Nil
      else Seq(compilerPlugin(Libraries.kindProjector.cross(CrossVersion.full)))
    }
  )

  lazy val publishSkipProfile: Project => Project = _.settings(publish / skip := true)

  lazy val rootProfile: Project => Project = _
    .configure(commonProfile, publishSkipProfile)
    .settings(name := "utils")
    .settings(ideSkipProject := false)

  def unmanagedSourceProfile(path: String): Project => Project = _.settings(
    Compile / unmanagedSourceDirectories += (Compile / sourceDirectory).value / path,
    Test / unmanagedSourceDirectories += (Test / sourceDirectory).value / path
  )

}
