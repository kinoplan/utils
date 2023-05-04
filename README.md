# Utils

![build](https://github.com/kinoplan/utils/workflows/build/badge.svg)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/kinoplan/utils?style=flat)](https://mergify.com)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f9bc01e85f7045e886bb3ad92ebaf081)](https://www.codacy.com/gh/kinoplan/utils/dashboard?utm_source=github.com\&utm_medium=referral\&utm_content=kinoplan/utils\&utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/kinoplan/utils/branch/main/graph/badge.svg?token=O6X248F7TZ)](https://codecov.io/gh/kinoplan/utils)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat\&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.kinoplan/utils-implicits-collection_2.13.svg?label=Maven%20Central)](https://central.sonatype.com/search?q=utils-\&smo=true\&namespace=io.kinoplan)
[![vscode](https://img.shields.io/static/v1?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAACXBIWXMAAAsTAAALEwEAmpwYAAADv0lEQVR4nO2ZvWsUQRjGZ97VJogfaSSFQbC5bWyUYGMRsbWwsBObgIWFMWBmLid4gtGZBNTOzn9ABEHZGMzsLRo/Ck935hRUSBELQQIKIrkLEjMyG8479T5mde8L7oEpd+f57b4z88wMQn311VfH5FBxDKhY2sZfPXMfFA+jXhIQMQbUXwfqayctNty5ona94suUVzyFshpQNwuoGAcqNoz5cosAKu21ATmQ11tRd0ljTMRstXGoDRC11Nzqsuutjh+4pwc67RyhbLAFE3GrlnmoA/CrecWV1NzqBNIad8j8vQFMhFfPPDQDqIBk2m8+vbgLiHjSyDzYA3xsr/nMoyGgvmpmHmwB5oq6febP+/vMHG9jHloCYAYMD0cQC/fGNj8ZHAQiVmzNQ+IA194MYq4C4EoDU+uYyWl0+7Zj86gzKUaB+F/jmIdEAfibYeDybWS+qhkgNJ0famieiuNARCmWeSJKkM6dTgwAuFr803ylyU8Ok0dqPjeZOwPE/xHvy4t36LzYb55PDoDJz/UBNksKmLxYnVcgLWjcksFE3EF0YUf5HYkBYC4vNwQolxSTD9GVwm5MxPWY5tdMFvqzXzexMaA1xkxdsoEAJktwYTFOySwjIkZqdesmPY0Cl2eByx/NIZSG7HMNNNe4ZKh/F40HO+v157ZiIQMWngSmvlv9jct5Demg1lf/bsZIs77cVq3EDiscBS6/WUFcDTVkHlUDfEAkOGTTj9vSKMHDEeDyixWEadnnGhP/PpqYH7Ttwm0pABEjkAm+RF/YEgKz8L5Z0TsO4KQXjgIR36KSyAQarryyhgAuPyCuOldCQMXJaBBWD8opA5G3hzCTACu0fxADyZ2tGw3Suc1Zx/pPRAvfXXQjbMc0qjGm/qXmi5KBeFGKAwFMLUcRvZUAmPrTVlmG+g/RlNiNubweD0KuAVOtixJA/c9NzK8DFRdRNlsJc6xAY0FsxvM7iOeTD3NA/cf1s7v/yaF+7Tg9o85YRY/f/8Y7NFtINk4jujAMRLz9O/76gdmkN3rUYeFx4DLeuOCyBKyQ3IYm0sT8oDFcLhkzLtAJyy3lTDgKXH2NW1Ju8uuAxmZzji7M70FxNVM4CFytdBjgPzX7eh8wudS7AEbT+SFgUvUugBEr7AKuniQC4LX7aLGsbH4Ac+n9L0DKK051BiCCCLZgrm79E4AXHa+f69zxevWBAZeztgCprrrgqJLJQsDkRgOAbr1iqgi4HIsOybjSDpe9dclXlsPkMeDq/fabS0977pq1r75Qd+gnbk39qO0MuGMAAAAASUVORK5CYII=\&label=\&message=Open%20in%20Visual%20Studio%20Code\&labelColor=2c2c32\&color=007acc\&logoColor=007acc)](https://vscode.dev/github/kinoplan/utils)

A set of various libraries that encapsulate the methods of working with Scala and the ecosystem
to facilitate re-development and use.

##### Contents

* [utils-zio-redisson](docs/zio/redisson/redisson.md)
* [utils-zio-sttp-opentelemetry](docs/zio/sttp/opentelemetry.md)
* [utils-zio-tapir-opentelemetry](docs/zio/tapir/opentelemetry.md)
* [utils-zio-opentelemetry](docs/zio/opentelemetry.md)

## Usage

You can add a module to your build by adding the following line to `libraryDependencies`:

```scala
"io.kinoplan" %% "utils-${module}" % ${version}
```

Here is the complete list of published artifacts:

```scala
libraryDependencies ++= Seq(
  // base
  "io.kinoplan" %% "utils-chimney-zio-prelude" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-circe-reactivemongo-bson" % ${version}, // JVM only
  "io.kinoplan" %% "utils-circe-zio-prelude" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-date" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-http4s-server" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-integration-check" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-locales-minimal-db" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-logback-config" % ${version}, // JVM only
  "io.kinoplan" %% "utils-logback-layout" % ${version}, // JVM only
  "io.kinoplan" %% "utils-nullable-core" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-nullable-codec-circe" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-nullable-codec-tapir" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-reactivemongo-base" % ${version}, // JVM only
  "io.kinoplan" %% "utils-reactivemongo-bson" % ${version}, // JVM only
  "io.kinoplan" %% "utils-reactivemongo-bson-any" % ${version}, // JVM only
  "io.kinoplan" %% "utils-reactivemongo-bson-joda-time" % ${version}, // JVM only
  "io.kinoplan" %% "utils-reactivemongo-bson-refined" % ${version}, // JVM only
  "io.kinoplan" %% "utils-reactivemongo-bson-zio-prelude" % ${version}, // JVM only
  "io.kinoplan" %% "utils-reactivemongo-kamon-instrumentation" % ${version}, // JVM only
  "io.kinoplan" %% "utils-redisson-core" % ${version}, // JVM only
  "io.kinoplan" %% "utils-redisson-codec-base" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-redisson-codec-circe" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-redisson-codec-play-json" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-redisson-codec-play2-json" % ${version}, // JVM only
  "io.kinoplan" %% "utils-scala-logging" % ${version}, // JVM only
  "io.kinoplan" %% "utils-tapir-zio-prelude" % ${version}, // JVM and Scala.js
  // implicits
  "io.kinoplan" %% "utils-implicits-any" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-implicits-boolean" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-implicits-collection" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-implicits-java-time" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-implicits-joda-time" % ${version}, // JVM only
  "io.kinoplan" %% "utils-implicits-identity" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-implicits-zio" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-implicits-zio-prelude" % ${version}, // JVM and Scala.js
  // play 3.x.x
  "io.kinoplan" %% "utils-play-error-handler" % ${version}, // JVM only
  "io.kinoplan" %% "utils-play-filters-logging" % ${version}, // JVM only
  "io.kinoplan" %% "utils-play-reactivemongo" % ${version}, // JVM only
  // play 2.x.x
  "io.kinoplan" %% "utils-play2-error-handler" % ${version}, // JVM only
  "io.kinoplan" %% "utils-play2-filters-logging" % ${version}, // JVM only
  "io.kinoplan" %% "utils-play2-reactivemongo" % ${version}, // JVM only
  // zio
  "io.kinoplan" %% "utils-zio-integration-check" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-zio-http4s-healthcheck" % ${version}, // JVM only
  "io.kinoplan" %% "utils-zio-monitoring-prometheus" % ${version}, // JVM only
  "io.kinoplan" %% "utils-zio-opentelemetry" % ${version}, // JVM only
  "io.kinoplan" %% "utils-zio-reactivemongo" % ${version}, // JVM only
  "io.kinoplan" %% "utils-zio-redisson" % ${version}, // JVM only
  "io.kinoplan" %% "utils-zio-sttp-opentelemetry-backend" % ${version}, // JVM only
  "io.kinoplan" %% "utils-zio-sttp-slf4j-backend" % ${version}, // JVM only
  "io.kinoplan" %% "utils-zio-tapir-server" % ${version}, // JVM and Scala.js
  "io.kinoplan" %% "utils-zio-tapir-opentelemetry" % ${version}, // JVM and Scala.js
)
```

You need to replace `${version}` with the version of Utils you want to use.

## Contributing

See [CONTRIBUTING.md](/CONTRIBUTING.md) for more details about how to contribute.

## License

This project is licensed under the terms of the [Apache License, Version 2.0](/LICENSE).
