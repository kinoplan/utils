resolvers in ThisBuild += "Artima Maven Repository".at("https://repo.artima.com/releases")

addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.12")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.9.2")

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.20")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.31")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.4")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")
