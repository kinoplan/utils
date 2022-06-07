ThisBuild / resolvers += "Artima Maven Repository".at("https://repo.artima.com/releases")

// zzzzzzzzzzzzzzzzzzzz Common Modules zzzzzzzzzzzzzzzzzzzz

lazy val date = project.in(file("base/date")).configure(BaseModules.dateProfile)

lazy val logbackConfig = project
  .in(file("base/logback-config"))
  .configure(BaseModules.logbackConfigProfile)

lazy val scalaLogging = project
  .in(file("base/scala-logging"))
  .configure(BaseModules.scalaLoggingProfile)

lazy val reactivemongoBsonJodaTime = project
  .in(file("base/reactivemongo/bson-joda-time"))
  .configure(BaseModules.reactivemongoBsonJodaTimeProfile)

lazy val reactivemongoBsonAny = project
  .in(file("base/reactivemongo/bson-any"))
  .configure(BaseModules.reactivemongoBsonAnyProfile)

// zzzzzzzzzzzzzzzzzzzz Implicits Modules zzzzzzzzzzzzzzzzzzzz

lazy val implicitsBoolean = project
  .in(file("implicits/boolean"))
  .configure(ImplicitsModules.booleanProfile)

lazy val implicitsCollection = project
  .in(file("implicits/collection"))
  .configure(ImplicitsModules.collectionProfile)

lazy val implicitsJodaTime = project
  .in(file("implicits/date/joda-time"))
  .configure(ImplicitsModules.jodaTimeProfile)
  .dependsOn(date)

// zzzzzzzzzzzzzzzzzzzz ZIO Modules zzzzzzzzzzzzzzzzzzzz

lazy val zioHttpHealthcheck = project
  .in(file("zio/http/healthcheck"))
  .configure(ZioModules.httpHealthcheckProfile)

lazy val zioMonitoringPrometheus = project
  .in(file("zio/monitoring/prometheus"))
  .configure(ZioModules.monitoringPrometheusProfile)

lazy val zioReactivemongo = project
  .in(file("zio/reactivemongo"))
  .configure(ZioModules.reactivemongoProfile)

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
