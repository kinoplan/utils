import Dependencies.{Batches, Libraries, ShadingEntity}
import coursier.ShadingPlugin
import coursier.ShadingPlugin.autoImport.*
import org.typelevel.sbt.tpolecat.TpolecatPlugin.autoImport.tpolecatExcludeOptions
import org.typelevel.scalacoptions.ScalacOptions
import sbt.*
import sbt.Keys.*
import scalafix.sbt.ScalafixPlugin
import scoverage.ScoverageKeys.*

object ProjectSettings {

  lazy val commonProfile: Project => Project = _
    .enablePlugins(ScalafixPlugin)
    .settings(
      crossScalaVersions := Seq("2.12.20", "2.13.13"),
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
      Test / tpolecatExcludeOptions ++=
        Set(ScalacOptions.privateWarnDeadCode, ScalacOptions.warnNonUnitStatement),
      Test / fork := true,
      Test / javaOptions += "-Duser.timezone=UTC",
      libraryDependencies ++= Seq(Libraries.scalatest.value, Libraries.mockitoScala),
      coverageHighlighting := true
    )

  lazy val scalaJsProfile: Project => Project = _.settings(Test / fork := false)

  lazy val zioTestProfile: Project => Project = _.settings(
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= Seq(Libraries.zioTest.value, Libraries.zioTestSbt.value)
  )

  lazy val kindProjectorProfile: Project => Project =
    _.settings(addCompilerPlugin(Libraries.kindProjector.cross(CrossVersion.full)))

  private val namespace = "io.kinoplan.utils"

  def shadingProfile(shadingEntities: ShadingEntity*): Project => Project = _
    .enablePlugins(ShadingPlugin)
    .settings(
      libraryDependencies ++= shadingEntities.flatMap(_.libraryDependencies(scalaVersion.value)),
      shadedModules ++= shadingEntities.flatMap(_.dependencies).map(_.module).toSet,
      shadingRules ++=
        shadingEntities
          .flatMap(_.modulePackages)
          .map(modulePackage => ShadingRule.moveUnder(modulePackage, s"$namespace.shaded")),
      validNamespaces ++= Set(namespace, "io"),
      validEntries ++= Set("LICENSE", "NOTICE", "README")
    )

}
