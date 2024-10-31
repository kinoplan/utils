import Dependencies.Libraries
import sbt.Keys.*
import locales.{CLDRVersion, LocalesFilter, LocalesPlugin}
import sbt.*
import locales.LocalesPlugin.autoImport.*

object ModulesCommon {

  lazy val chimneyZioPreludeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-chimney-zio-prelude")
    .settings(libraryDependencies ++= Seq(Libraries.chimney.value, Libraries.zioPrelude.value))

  lazy val circeReactivemongoBsonProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-circe-reactivemongo-bson")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.circeCore.value      % Provided,
          Libraries.reactiveMongoBsonApi % Provided,
          Libraries.circeGeneric.value   % Test,
          Libraries.circeParser.value    % Test
        )
    )

  lazy val circeZioPreludeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-circe-zio-prelude")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.circeCore.value    % Provided,
          Libraries.circeGeneric.value % Test,
          Libraries.circeParser.value  % Test,
          Libraries.zioPrelude.value
        )
    )

  lazy val crossCollectionProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-cross-collection")

  lazy val dateProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-date")

  lazy val http4sServerProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-http4s-server")
    .settings(libraryDependencies ++= Seq(Libraries.http4sServer))

  lazy val integrationCheckProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-integration-check")

  lazy val localesMinimalDbProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .enablePlugins(LocalesPlugin)
    .settings(name := "utils-locales-minimal-db")
    .settings(
      cldrVersion := CLDRVersion.Version("45.0"), // http://unicode.org/Public/cldr/
      localesFilter := LocalesFilter.Selection("ru", "en"),
      libraryDependencies ++= Seq(Libraries.scalaJavaLocales.value)
    )

  lazy val logbackConfigProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-logback-config")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.logbackCore % Provided, Libraries.typesafeConfig % Provided)
    )

  lazy val logbackLayoutProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-logback-layout")
    .settings(libraryDependencies ++= Seq(Libraries.logbackClassic % Provided))

  lazy val nullableCoreProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-nullable-core")

  lazy val nullableCodecCirceProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-nullable-codec-circe")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.circeCore.value % Provided, Libraries.circeGeneric.value % Test)
    )

  lazy val nullableCodecTapirProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-nullable-codec-tapir")
    .settings(libraryDependencies ++= Seq(Libraries.tapirCore.value % Provided))

  lazy val scalaLoggingProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-scala-logging")
    .settings(Test / parallelExecution := false)
    .settings(libraryDependencies ++= Seq(Libraries.logbackClassic, Libraries.scalaLogging))

  lazy val reactivemongoBaseProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-base")
    .settings(libraryDependencies ++= Seq(Libraries.reactiveMongo % Provided, Libraries.sourcecode))

  lazy val reactivemongoBsonProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-reactivemongo-bson")

  lazy val reactivemongoBsonAnyProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-any")
    .settings(libraryDependencies ++= Seq(Libraries.reactiveMongoBsonApi % Provided))

  lazy val reactivemongoBsonJodaTimeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-joda-time")
    .settings(
      libraryDependencies ++= Seq(Libraries.reactiveMongoBsonApi % Provided, Libraries.jodaTime)
    )

  lazy val reactivemongoBsonRefinedProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-refined")
    .settings(
      libraryDependencies ++= Seq(Libraries.reactiveMongoBsonApi % Provided, Libraries.refined)
    )

  lazy val reactivemongoBsonZioPreludeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-reactivemongo-bson-zio-prelude")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.reactiveMongoBsonApi % Provided, Libraries.zioPrelude.value)
    )

  lazy val redissonCoreProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-core")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.redisson, Libraries.jacksonModule, Libraries.scalaCollectionCompat)
    )

  lazy val redissonCodecBaseProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-redisson-codec-base")

  lazy val redissonCodecCirceProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-codec-circe")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.circeCore.value % Provided, Libraries.circeParser.value % Provided)
    )

  lazy val redissonCodecPlayJsonProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-codec-play-json")
    .settings(target := target.value / "play3")
    .settings(libraryDependencies ++= Seq(Libraries.playJson % Provided))

  lazy val redissonCodecPlay2JsonProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-codec-play2-json")
    .settings(target := target.value / "play2")
    .settings(libraryDependencies ++= Seq(Libraries.play2Json % Provided))

  lazy val tapirZioPreludeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tapir-zio-prelude")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.tapirCore.value % Provided,
          Libraries.zioPrelude.value,
          Libraries.scalatestPlusScalacheck.value
        )
    )

}
