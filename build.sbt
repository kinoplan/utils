resolvers in ThisBuild += "Artima Maven Repository" at "https://repo.artima.com/releases"

lazy val root = project.in(file(".")).aggregate(
  implicitsCollection
).configure(ProjectSettings.rootProfile)

// zzzzzzzzzzzzzzzzzzzz Implicits Modules zzzzzzzzzzzzzzzzzzzz

lazy val implicitsCollection = project.in(file("implicits/collection"))
  .configure(ImplicitsModules.collectionProfile)

onLoad in Global := (
  "project root" ::
  (_: State)
) compose (onLoad in Global).value