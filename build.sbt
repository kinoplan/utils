ThisBuild / resolvers += "Artima Maven Repository".at("https://repo.artima.com/releases")

// zzzzzzzzzzzzzzzzzzzz Common Modules zzzzzzzzzzzzzzzzzzzz

lazy val date = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("common/date"))
  .configure(ModulesCommon.dateProfile)
  .jsConfigure(ProjectSettings.scalaJsProfile)

lazy val dateJs = date.js
lazy val dateJvm = date.jvm

lazy val logbackConfig = project
  .in(file("common/logback-config"))
  .configure(ModulesCommon.logbackConfigProfile)

lazy val scalaLogging = project
  .in(file("common/scala-logging"))
  .configure(ModulesCommon.scalaLoggingProfile)

lazy val reactivemongoBsonJodaTime = project
  .in(file("common/reactivemongo/bson-joda-time"))
  .configure(ModulesCommon.reactivemongoBsonJodaTimeProfile)

lazy val reactivemongoBsonAny = project
  .in(file("common/reactivemongo/bson-any"))
  .configure(ModulesCommon.reactivemongoBsonAnyProfile)

// zzzzzzzzzzzzzzzzzzzz Implicits Modules zzzzzzzzzzzzzzzzzzzz

lazy val implicitsAny = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("implicits/any"))
  .configure(ModulesImplicits.anyProfile)
  .jsConfigure(ProjectSettings.scalaJsProfile)

lazy val implicitsAnyJs = implicitsAny.js
lazy val implicitsAnyJvm = implicitsAny.jvm

lazy val implicitsBoolean = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("implicits/boolean"))
  .configure(ModulesImplicits.booleanProfile)
  .jsConfigure(ProjectSettings.scalaJsProfile)

lazy val implicitsBooleanJs = implicitsBoolean.js
lazy val implicitsBooleanJvm = implicitsBoolean.jvm

lazy val implicitsCollection = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("implicits/collection"))
  .configure(ModulesImplicits.collectionProfile)
  .jsConfigure(ProjectSettings.scalaJsProfile)

lazy val implicitsCollectionJs = implicitsCollection.js
lazy val implicitsCollectionJvm = implicitsCollection.jvm

lazy val implicitsJodaTime = project
  .in(file("implicits/date/joda-time"))
  .configure(ModulesImplicits.jodaTimeProfile)
  .dependsOn(dateJvm)

// zzzzzzzzzzzzzzzzzzzz Play Modules zzzzzzzzzzzzzzzzzzzz

lazy val playReactivemongo = project
  .in(file("play/reactivemongo"))
  .configure(ModulesPlay.reactivemongoProfile)

// zzzzzzzzzzzzzzzzzzzz ZIO Modules zzzzzzzzzzzzzzzzzzzz

lazy val zioHttpHealthcheck = project
  .in(file("zio/http/healthcheck"))
  .configure(ModulesZio.httpHealthcheckProfile)

lazy val zioMonitoringPrometheus = project
  .in(file("zio/monitoring/prometheus"))
  .configure(ModulesZio.monitoringPrometheusProfile)

lazy val zioReactivemongo = project
  .in(file("zio/reactivemongo"))
  .configure(ModulesZio.reactivemongoProfile)

// format: off
inThisBuild(
  List(
    sonatypeCredentialHost := Sonatype.sonatype01,
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
