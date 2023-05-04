import sbt.Def.spaceDelimited
import sbt.Reference.display

ThisBuild / resolvers += "Artima Maven Repository".at("https://repo.artima.com/releases")

ThisBuild / scalaVersion := ProjectSettings.scala2_13

lazy val rawAllAggregates = chimneyZioPrelude.projectRefs ++ circeReactivemongoBson.projectRefs ++
  circeZioPrelude.projectRefs ++ crossCollection.projectRefs ++ date.projectRefs ++
  http4sServer.projectRefs ++ integrationCheck.projectRefs ++ localesMinimalDb.projectRefs ++
  logbackConfig.projectRefs ++ logbackLayout.projectRefs ++ nullableCore.projectRefs ++
  nullableCodecCirce.projectRefs ++ nullableCodecTapir.projectRefs ++
  reactivemongoBase.projectRefs ++ reactivemongoBson.projectRefs ++
  reactivemongoBsonAny.projectRefs ++ reactivemongoBsonJodaTime.projectRefs ++
  reactivemongoBsonRefined.projectRefs ++ reactivemongoBsonZioPrelude.projectRefs ++
  reactivemongoKamonInstrumentation.projectRefs ++ redissonCore.projectRefs ++
  redissonCodecBase.projectRefs ++ redissonCodecCirce.projectRefs ++
  redissonCodecPlayJson.projectRefs ++ redissonCodecPlay2Json.projectRefs ++
  scalaLogging.projectRefs ++ tapirZioPrelude.projectRefs ++ implicitsAny.projectRefs ++
  implicitsBoolean.projectRefs ++ implicitsCollection.projectRefs ++
  implicitsJavaTime.projectRefs ++ implicitsJodaTime.projectRefs ++ implicitsIdentity.projectRefs ++
  implicitsZio.projectRefs ++ implicitsZioPrelude.projectRefs ++ playErrorHandler.projectRefs ++
  playFiltersLogging.projectRefs ++ playReactivemongo.projectRefs ++
  playRequestMapContext.projectRefs ++ zioHttp4sHealthCheck.projectRefs ++
  zioIntegrationCheck.projectRefs ++ zioMonitoringPrometheus.projectRefs ++
  zioOpenTelemetry.projectRefs ++ zioReactivemongo.projectRefs ++ zioRedisson.projectRefs ++
  zioSttpLoggingSlf4j.projectRefs ++ zioSttpOpenTelemetry.projectRefs ++
  zioTapirServer.projectRefs ++ zioTapirOpenTelemetry.projectRefs

lazy val allAggregates = rawAllAggregates

val commonJsSettings = ProjectSettings.scalaJsSettings

val scopesDescription = "Scala version can be: 2.12, 2.13; platform: JVM, JS"

val cleanScoped = inputKey[Unit](
  s"Run clean in the given scope. Usage: cleanScoped [scala version] [platform]. $scopesDescription"
)

val compileScoped = inputKey[Unit](
  s"Compiles sources in the given scope. Usage: compileScoped [scala version] [platform]. $scopesDescription"
)

val testScoped = inputKey[Unit](
  s"Run tests in the given scope. Usage: testScoped [scala version] [platform]. $scopesDescription"
)

val scalafmtCheckScoped = inputKey[Unit](
  s"Check sources by scalafmt in the given scope. Usage: scalafmtCheckScoped [scala version] [platform]. $scopesDescription"
)

val scalafixCheckScoped = inputKey[Unit](
  s"Check sources by scalafix in the given scope. Usage: scalafixCheckScoped [scala version] [platform]. $scopesDescription"
)

val coverageReportScoped = inputKey[Unit](
  s"Run generate coverage report in the given scope. Usage: coverageReportScoped [scala version] [platform]. $scopesDescription"
)

def filterProject(p: String => Boolean) =
  ScopeFilter(inProjects(allAggregates.filter(pr => p(display(pr.project))) *))

def filterByVersionAndPlatform(scalaVersionFilter: String, platformFilter: String) =
  filterProject { projectName =>
    val byPlatform =
      if (platformFilter == "JVM") !projectName.contains("JS")
      else projectName.contains(platformFilter)
    val byVersion = scalaVersionFilter match {
      case "2.13" => !projectName.contains("2_12") && !projectName.contains("3")
      case "2.12" => projectName.contains("2_12")
      case "3"    => projectName.contains("3")
    }

    byPlatform && byVersion
  }

lazy val root = project
  .in(file("."))
  .configure(ProjectSettings.rootProfile)
  .settings(
    cleanScoped :=
      Def
        .inputTaskDyn {
          val args = spaceDelimited("<arg>").parsed
          Def.taskDyn(clean.all(filterByVersionAndPlatform(args.head, args(1))))
        }
        .evaluated,
    compileScoped :=
      Def
        .inputTaskDyn {
          val args = spaceDelimited("<arg>").parsed
          Def.taskDyn((Compile / compile).all(filterByVersionAndPlatform(args.head, args(1))))
        }
        .evaluated,
    testScoped :=
      Def
        .inputTaskDyn {
          val args = spaceDelimited("<arg>").parsed
          Def.taskDyn((Test / test).all(filterByVersionAndPlatform(args.head, args(1))))
        }
        .evaluated,
    scalafmtCheckScoped :=
      Def
        .inputTaskDyn {
          val args = spaceDelimited("<arg>").parsed
          Def.taskDyn((Compile / scalafmtCheck).all(filterByVersionAndPlatform(args.head, args(1))))
        }
        .evaluated,
    scalafixCheckScoped :=
      Def
        .inputTaskDyn {
          val args = spaceDelimited("<arg>").parsed
          Def.taskDyn(
            (Compile / scalafix).toTask(" --check").all(filterByVersionAndPlatform(args.head, args(1)))
          )
        }
        .evaluated,
    coverageReportScoped :=
      Def
        .inputTaskDyn {
          val args = spaceDelimited("<arg>").parsed
          Def.taskDyn(coverageReport.all(filterByVersionAndPlatform(args.head, args(1))))
        }
        .evaluated
  )
  .aggregate(allAggregates *)

// zzzzzzzzzzzzzzzzzzzz Common Modules zzzzzzzzzzzzzzzzzzzz

lazy val chimneyZioPrelude = projectMatrix
  .in(file("common/chimney/zio-prelude"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.chimneyZioPreludeProfile)

lazy val circeReactivemongoBson = projectMatrix
  .in(file("common/circe/reactivemongo-bson"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.circeReactivemongoBsonProfile)

lazy val circeZioPrelude = projectMatrix
  .in(file("common/circe/zio-prelude"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.circeZioPreludeProfile)

lazy val crossCollection = projectMatrix
  .in(file("common/cross/collection"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.crossCollectionProfile)

lazy val date = projectMatrix
  .in(file("common/date"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.dateProfile)

lazy val http4sServer = projectMatrix
  .in(file("common/http4s/server"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.http4sServerProfile)

lazy val integrationCheck = projectMatrix
  .in(file("common/integration-check"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.integrationCheckProfile)

lazy val localesMinimalDb = projectMatrix
  .in(file("common/locales-minimal-db"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.localesMinimalDbProfile)

lazy val logbackConfig = projectMatrix
  .in(file("common/logback/config"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.logbackConfigProfile)

lazy val logbackLayout = projectMatrix
  .in(file("common/logback/layout"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.logbackLayoutProfile)

lazy val nullableCore = projectMatrix
  .in(file("common/nullable/core"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.nullableCoreProfile)

lazy val nullableCodecCirce = projectMatrix
  .in(file("common/nullable/codec/circe"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.nullableCodecCirceProfile)
  .dependsOn(nullableCore)

lazy val nullableCodecTapir = projectMatrix
  .in(file("common/nullable/codec/tapir"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.nullableCodecTapirProfile)
  .dependsOn(nullableCore)

lazy val reactivemongoBase = projectMatrix
  .in(file("common/reactivemongo/base"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.reactivemongoBaseProfile)

lazy val reactivemongoBson = projectMatrix
  .in(file("common/reactivemongo/bson"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.reactivemongoBsonProfile)

lazy val reactivemongoBsonAny = projectMatrix
  .in(file("common/reactivemongo/bson-any"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.reactivemongoBsonAnyProfile)

lazy val reactivemongoBsonJodaTime = projectMatrix
  .in(file("common/reactivemongo/bson-joda-time"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.reactivemongoBsonJodaTimeProfile)
  .dependsOn(reactivemongoBson)

lazy val reactivemongoBsonRefined = projectMatrix
  .in(file("common/reactivemongo/bson-refined"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.reactivemongoBsonRefinedProfile)

lazy val reactivemongoBsonZioPrelude = projectMatrix
  .in(file("common/reactivemongo/bson-zio-prelude"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.reactivemongoBsonZioPreludeProfile)

lazy val reactivemongoKamonInstrumentation = projectMatrix
  .in(file("common/reactivemongo/kamon-instrumentation"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.reactivemongoKamonInstrumentationProfile)

lazy val redissonCore = projectMatrix
  .in(file("common/redisson/core"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.redissonCoreProfile)
  .dependsOn(crossCollection, redissonCodecBase)

lazy val redissonCodecBase = projectMatrix
  .in(file("common/redisson/codec/base"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.redissonCodecBaseProfile)

lazy val redissonCodecCirce = projectMatrix
  .in(file("common/redisson/codec/circe"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.redissonCodecCirceProfile)
  .dependsOn(redissonCodecBase)

lazy val redissonCodecPlayJson = projectMatrix
  .in(file("common/redisson/codec/play-json"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.redissonCodecPlayJsonProfile)
  .dependsOn(redissonCodecBase)

lazy val redissonCodecPlay2Json = projectMatrix
  .in(file("common/redisson/codec/play-json"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.redissonCodecPlay2JsonProfile)
  .dependsOn(redissonCodecBase)

lazy val scalaLogging = projectMatrix
  .in(file("common/scala-logging"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesCommon.scalaLoggingProfile)

lazy val tapirZioPrelude = projectMatrix
  .in(file("common/tapir/zio-prelude"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesCommon.tapirZioPreludeProfile)

// zzzzzzzzzzzzzzzzzzzz Implicits Modules zzzzzzzzzzzzzzzzzzzz

lazy val implicitsAny = projectMatrix
  .in(file("implicits/any"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesImplicits.anyProfile)

lazy val implicitsBoolean = projectMatrix
  .in(file("implicits/boolean"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesImplicits.booleanProfile)

lazy val implicitsCollection = projectMatrix
  .in(file("implicits/collection"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesImplicits.collectionProfile)

lazy val implicitsIdentity = projectMatrix
  .in(file("implicits/identity"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesImplicits.identityProfile)

lazy val implicitsJavaTime = projectMatrix
  .in(file("implicits/date/java-time"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(
    ProjectSettings.scala2Versions,
    Nil,
    ModulesImplicits.javaTimeJsProfile(_).settings(commonJsSettings)
  )
  .configure(ModulesImplicits.javaTimeProfile)
  .dependsOn(date)

lazy val implicitsJodaTime = projectMatrix
  .in(file("implicits/date/joda-time"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesImplicits.jodaTimeProfile)
  .dependsOn(date)

lazy val implicitsZio = projectMatrix
  .in(file("implicits/zio"))
  .configure(ModulesImplicits.zioProfile)
  .jvmPlatform(ProjectSettings.scala2Versions, Nil, ModulesImplicits.zioJvmProfile)
  .jsPlatform(ProjectSettings.scala2Versions, Nil, ModulesImplicits.zioJsProfile)

lazy val implicitsZioPrelude = projectMatrix
  .in(file("implicits/zio-prelude"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesImplicits.zioPreludeProfile)

// zzzzzzzzzzzzzzzzzzzz Play Modules zzzzzzzzzzzzzzzzzzzz

lazy val play2Axis = CustomAxis("Play_2", "play2")
lazy val play3Axis = CustomAxis("Play", "play3")

lazy val playErrorHandler = projectMatrix
  .in(file("play/error-handler"))
  .customRow(
    ProjectSettings.scala2Versions,
    Seq(play2Axis, VirtualAxis.jvm),
    ModulesPlay.errorHandler2Profile
  )
  .customRow(
    ProjectSettings.scala2_13Versions,
    Seq(play3Axis, VirtualAxis.jvm),
    ModulesPlay.errorHandlerProfile
  )
  .dependsOn(scalaLogging, playRequestMapContext, playFiltersLogging % "test->test")

lazy val playFiltersLogging = projectMatrix
  .in(file("play/filters/logging"))
  .customRow(
    ProjectSettings.scala2Versions,
    Seq(play2Axis, VirtualAxis.jvm),
    ModulesPlay.filtersLogging2Profile
  )
  .customRow(
    ProjectSettings.scala2_13Versions,
    Seq(play3Axis, VirtualAxis.jvm),
    ModulesPlay.filtersLoggingProfile
  )
  .dependsOn(scalaLogging, playRequestMapContext)

lazy val playReactivemongo = projectMatrix
  .in(file("play/reactivemongo"))
  .customRow(
    ProjectSettings.scala2Versions,
    Seq(play2Axis, VirtualAxis.jvm),
    ModulesPlay.reactivemongo2Profile
  )
  .customRow(
    ProjectSettings.scala2_13Versions,
    Seq(play3Axis, VirtualAxis.jvm),
    ModulesPlay.reactivemongoProfile
  )
  .dependsOn(reactivemongoBase)

lazy val playRequestMapContext = projectMatrix
  .in(file("play/request/map-context"))
  .customRow(
    ProjectSettings.scala2Versions,
    Seq(play2Axis, VirtualAxis.jvm),
    ModulesPlay.requestMapContext2Profile
  )
  .customRow(
    ProjectSettings.scala2_13Versions,
    Seq(play3Axis, VirtualAxis.jvm),
    ModulesPlay.requestMapContextProfile
  )
  .dependsOn(scalaLogging)

// zzzzzzzzzzzzzzzzzzzz ZIO Modules zzzzzzzzzzzzzzzzzzzz

lazy val zioHttp4sHealthCheck = projectMatrix
  .in(file("zio/http4s/healthcheck"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesZio.http4sHealthcheckProfile)
  .dependsOn(zioIntegrationCheck)

lazy val zioIntegrationCheck = projectMatrix
  .in(file("zio/integration-check"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesZio.integrationCheckProfile)
  .dependsOn(integrationCheck)

lazy val zioMonitoringPrometheus = projectMatrix
  .in(file("zio/monitoring/prometheus"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesZio.monitoringPrometheusProfile)

lazy val zioOpenTelemetry = projectMatrix
  .in(file("zio/opentelemetry"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesZio.openTelemetryProfile)

lazy val zioReactivemongo = projectMatrix
  .in(file("zio/reactivemongo"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesZio.reactivemongoProfile)
  .dependsOn(zioIntegrationCheck, reactivemongoBase)

lazy val zioRedisson = projectMatrix
  .in(file("zio/redisson"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesZio.redissonProfile)
  .dependsOn(crossCollection, zioIntegrationCheck, redissonCodecBase)

lazy val zioSttpLoggingSlf4j = projectMatrix
  .in(file("zio/sttp/logging/slf4j"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesZio.sttpLoggingSlf4jProfile)

lazy val zioSttpOpenTelemetry = projectMatrix
  .in(file("zio/sttp/opentelemetry"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesZio.sttpOpenTelemetryProfile)

lazy val zioTapirServer = projectMatrix
  .in(file("zio/tapir/server"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .jsPlatform(ProjectSettings.scala2Versions, commonJsSettings)
  .configure(ModulesZio.tapirServerProfile)

lazy val zioTapirOpenTelemetry = projectMatrix
  .in(file("zio/tapir/opentelemetry"))
  .jvmPlatform(ProjectSettings.scala2Versions)
  .configure(ModulesZio.tapirOpenTelemetryProfile)

// format: off
inThisBuild(
  List(
    versionScheme := Some(VersionScheme.EarlySemVer),
    organization := "io.kinoplan",
    homepage := Some(url("https://github.com/kinoplan/utils")),
    licenses := Seq("Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0")),
    developers := List(Developer("kinoplan", "Kinoplan", "job@kinoplan.ru", url("https://kinoplan.tech"))),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/kinoplan/utils"),
        "scm:git:git@github.com:kinoplan/utils.git"
      )
    )
  )
)

Global / onChangedBuildSource := ReloadOnSourceChanges
// format: on
