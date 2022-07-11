ThisBuild / resolvers += "Artima Maven Repository".at("https://repo.artima.com/releases")

addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.12")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.0")

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.3.1")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.10.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")
