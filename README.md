# Utils

![build](https://github.com/kinoplan/utils/workflows/build/badge.svg)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/kinoplan/utils?style=flat)](https://mergify.com)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f9bc01e85f7045e886bb3ad92ebaf081)](https://www.codacy.com/gh/kinoplan/utils/dashboard?utm_source=github.com\&utm_medium=referral\&utm_content=kinoplan/utils\&utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/kinoplan/utils/branch/main/graph/badge.svg?token=O6X248F7TZ)](https://codecov.io/gh/kinoplan/utils)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat\&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.kinoplan/utils-implicits-collection_2.13.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.kinoplan%22%20AND%20a:%22utils-implicits-collection_2.13%22)

A set of various libraries that encapsulate the methods of working with Scala and the ecosystem
to facilitate re-development and use.

## Usage

You can add a module to your build by adding the following line to `libraryDependencies`:

```scala
"io.kinoplan" %% "utils-${module}" % ${version}
```

Here is the complete list of published artifacts:

```scala
libraryDependencies ++= Seq(
  // base
  "io.kinoplan" %% "utils-date" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-logback-config" % ${version}, // JVM only
  "io.kinoplan" %% "utils-scala-logging" % ${version}, // JVM only
  "io.kinoplan" %% "utils-reactivemongo-bson-any" % ${version}, // JVM only
  "io.kinoplan" %% "utils-reactivemongo-bson-joda-time" % ${version}, // JVM only
  // implicits
  "io.kinoplan" %% "utils-implicits-any" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-implicits-boolean" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-implicits-collection" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-implicits-joda-time" % ${version}, // JVM only
  // play
  "io.kinoplan" %% "utils-play-reactivemongo" % ${version}, // JVM only
  // zio
  "io.kinoplan" %% "utils-zio-http-healthcheck" % ${version}, // JVM only
  "io.kinoplan" %% "utils-zio-monitoring-prometheus" % ${version}, // JVM only
  "io.kinoplan" %% "utils-zio-reactivemongo" % ${version}, // JVM only
)
```

You need to replace `${version}` with the version of Utils you want to use.

## Contributing

See [CONTRIBUTING.md](/CONTRIBUTING.md) for more details about how to contribute.

## License

This project is licensed under the terms of the [Apache License, Version 2.0](/LICENSE).
