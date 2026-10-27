import Dependencies.Libraries
import com.lightbend.sbt.javaagent.JavaAgent
import com.lightbend.sbt.javaagent.JavaAgent.JavaAgentKeys.javaAgents
import sbt.Keys.*
import locales.{CLDRVersion, LocalesFilter, LocalesPlugin}
import sbt.*
import locales.LocalesPlugin.autoImport.*

object ModulesCommon {

  lazy val chimneyZioPreludeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-chimney-zio-prelude")
    .settings(
      libraryDependencies ++= Seq(Libraries.chimney % Provided, Libraries.zioPrelude % Provided)
    )

  lazy val circeReactivemongoBsonProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-circe-reactivemongo-bson")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.circeCore            % Provided,
          Libraries.reactiveMongoBsonApi % Provided,
          Libraries.circeGeneric         % Test,
          Libraries.circeParser          % Test
        )
    )

  lazy val circeZioPreludeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-circe-zio-prelude")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.circeCore    % Provided,
          Libraries.circeGeneric % Test,
          Libraries.circeParser  % Test,
          Libraries.zioPrelude   % Provided
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
      libraryDependencies ++= Seq(Libraries.scalaJavaLocales)
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
      libraryDependencies ++= Seq(Libraries.circeCore % Provided, Libraries.circeGeneric % Test)
    )

  lazy val nullableCodecTapirProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-nullable-codec-tapir")
    .settings(libraryDependencies ++= Seq(Libraries.tapirCore % Provided))

  lazy val nullableCodecZioJsonProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-nullable-codec-zio-json")
    .settings(libraryDependencies ++= Seq(Libraries.zioJson % Provided))

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
      libraryDependencies ++= Seq(Libraries.reactiveMongoBsonApi % Provided, Libraries.zioPrelude)
    )

  lazy val reactivemongoKamonInstrumentationProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .enablePlugins(JavaAgent)
    .settings(name := "utils-reactivemongo-kamon-instrumentation")
    .settings(javaAgents := Seq(Libraries.kanelaAgent % "runtime;test"))
    .settings(
      // Kanela must see instrumentation classes and reference.conf when the test JVM starts.
      Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Raw,
      libraryDependencies ++=
        Seq(
          Libraries.kamonCore % Provided,
          Libraries.kamonInstrumentationCommon,
          Libraries.kamonTestkit,
          Libraries.kanelaAgent,
          Libraries.logbackClassic % Test,
          Libraries.openTelemetryApi,
          Libraries.openTelemetrySemconvIncubating,
          Libraries.reactiveMongo % Provided,
          Libraries.testContainers,
          Libraries.testContainersMongodb
        )
    )

  lazy val redissonCoreProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-core")
    .settings(
      libraryDependencies ++=
        Seq(
          Libraries.redisson,
          Libraries.jacksonModule,
          Libraries.scalaCollectionCompat,
          Libraries.scalaJavaCompat
        )
    )

  lazy val redissonCodecBaseProfile: Project => Project =
    _.configure(ProjectSettings.commonProfile).settings(name := "utils-redisson-codec-base")

  lazy val redissonCodecCirceProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-codec-circe")
    .settings(
      libraryDependencies ++= Seq(Libraries.circeCore % Provided, Libraries.circeParser % Provided)
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

  lazy val redissonCodecZioJsonProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-redisson-codec-zio-json")
    .settings(libraryDependencies ++= Seq(Libraries.zioJson % Provided))

  lazy val tapirZioPreludeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-tapir-zio-prelude")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.tapirCore % Provided, Libraries.zioPrelude, Libraries.scalatestPlusScalacheck)
    )

  lazy val zioJsonPreludeProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-json-prelude")
    .settings(
      libraryDependencies ++= Seq(Libraries.zioJson % Provided, Libraries.zioPrelude % Provided)
    )

  lazy val zioJsonReactivemongoBsonProfile: Project => Project = _
    .configure(ProjectSettings.commonProfile)
    .settings(name := "utils-zio-json-reactivemongo-bson")
    .settings(
      libraryDependencies ++=
        Seq(Libraries.zioJson % Provided, Libraries.reactiveMongoBsonApi % Provided)
    )

}
