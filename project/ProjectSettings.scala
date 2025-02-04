import Dependencies.{Libraries, ShadingEntity}
import coursier.ShadingPlugin
import coursier.ShadingPlugin.autoImport.*
import org.typelevel.sbt.tpolecat.TpolecatPlugin.autoImport.tpolecatExcludeOptions
import org.typelevel.scalacoptions.ScalacOptions
import sbt.*
import sbt.Keys.*
import scalafix.sbt.ScalafixPlugin
import scoverage.ScoverageKeys.*

object ProjectSettings {

  private val namespace = "io.kinoplan.utils"

  val scala2_12 = "2.12.20"
  val scala2_13 = "2.13.16"

  val scala2Versions: Seq[String] = Seq(scala2_12, scala2_13)
  val scala2_13Versions: Seq[String] = Seq(scala2_13)

  lazy val commonProfile: Project => Project = _
    .enablePlugins(ScalafixPlugin)
    .settings(
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
      coverageHighlighting := true,
      libraryDependencies ++= Seq(Libraries.scalatest.value, Libraries.mockitoScala)
    )

  val scalaJsSettings = Seq(Test / fork := false)
  lazy val scalaJsProfile: Project => Project = _.settings(scalaJsSettings)

  lazy val zioTestProfile: Project => Project = _.settings(
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= Seq(Libraries.zioTest.value, Libraries.zioTestSbt.value)
  )

  lazy val kindProjectorProfile: Project => Project =
    _.settings(addCompilerPlugin(Libraries.kindProjector.cross(CrossVersion.full)))

  lazy val publishSkipProfile: Project => Project = _.settings(publish / skip := true)

  lazy val rootProfile: Project => Project = _
    .configure(commonProfile, publishSkipProfile)
    .settings(publish / skip := true)
    .settings(name := "utils")

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

  def unmanagedSourceProfile(path: String): Project => Project = _.settings(
    Compile / unmanagedSourceDirectories +=
      (Compile / sourceDirectory).value / path,
    Test / unmanagedSourceDirectories +=
      (Test / sourceDirectory).value / path
  )

}
