addCommandAlias("fix", "+scalafixAll")
addCommandAlias("fmt", "+scalafmtAll; +scalafmtSbt;")
addCommandAlias("format", "fix;fmt;")
addCommandAlias("report", "clean;coverage;test;coverageReport")
