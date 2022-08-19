ThisBuild / resolvers += "Artima Maven Repository".at("https://repo.artima.com/releases")

// zzzzzzzzzzzzzzzzzzzz Common Modules zzzzzzzzzzzzzzzzzzzz

lazy val date = project.in(file("common/date")).configure(ModulesCommon.dateProfile)

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

lazy val implicitsBoolean = project
  .in(file("implicits/boolean"))
  .configure(ModulesImplicits.booleanProfile)

lazy val implicitsCollection = project
  .in(file("implicits/collection"))
  .configure(ModulesImplicits.collectionProfile)

lazy val implicitsJodaTime = project
  .in(file("implicits/date/joda-time"))
  .configure(ModulesImplicits.jodaTimeProfile)
  .dependsOn(date)

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
