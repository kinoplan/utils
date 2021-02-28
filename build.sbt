resolvers in ThisBuild += "Artima Maven Repository" at "https://repo.artima.com/releases"

lazy val root = project.in(file(".")).aggregate(
  implicitsBoolean, implicitsCollection
).configure(ProjectSettings.rootProfile)

// zzzzzzzzzzzzzzzzzzzz Implicits Modules zzzzzzzzzzzzzzzzzzzz

lazy val implicitsBoolean = project.in(file("implicits/boolean"))
  .configure(ImplicitsModules.booleanProfile)

lazy val implicitsCollection = project.in(file("implicits/collection"))
  .configure(ImplicitsModules.collectionProfile)

inThisBuild(
  List(
    organization := "io.kinoplan",
    homepage := Some(url("https://github.com/kinoplan/utils")),
    licenses := Seq("Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0")),
    developers := List(
      Developer(
        "kinoplan",
        "Kinoplan",
        "job@kinoplan.ru",
        url("https://kinoplan.tech")
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/kinoplan/utils"),
        "scm:git:git@github.com:kinoplan/utils.git"
      )
    )
  )
)

onLoad in Global := (
  "project root" ::
  (_: State)
) compose (onLoad in Global).value