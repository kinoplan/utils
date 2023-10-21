import org.typelevel.sbt.tpolecat.TpolecatPlugin.autoImport.tpolecatExcludeOptions
import org.typelevel.scalacoptions.ScalacOptions
import sbt.Keys.*
import sbt.*
import scalafix.sbt.ScalafixPlugin
import scoverage.ScoverageKeys.*

object ProjectSettings {

  lazy val commonProfile: Project => Project = _
    .enablePlugins(ScalafixPlugin)
    .settings(
      crossScalaVersions := Seq("2.12.18", "2.13.12"),
      scalaVersion := crossScalaVersions.value.last,
      tpolecatExcludeOptions :=
        Set(
          ScalacOptions.warnError,
          ScalacOptions.warnUnusedParams,
          ScalacOptions.warnUnusedPatVars,
          ScalacOptions.warnValueDiscard,
          ScalacOptions.warnDeadCode,
          ScalacOptions.lintInferAny,
          ScalacOptions.warnUnusedExplicits,
          ScalacOptions.warnNonUnitStatement
        ),
      Test / fork := true,
      Test / javaOptions += "-Duser.timezone=UTC",
      libraryDependencies ++= Seq(Dependencies.scalatest.value, Dependencies.mockitoScala),
      coverageHighlighting := true
    )

  lazy val scalaJsProfile: Project => Project = _.settings(Test / fork := false)

  lazy val kindProjectorProfile: Project => Project =
    _.settings(addCompilerPlugin(Dependencies.kindProjector.cross(CrossVersion.full)))

}
