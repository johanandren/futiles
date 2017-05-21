resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.1.0")

// token for interaction is in travis-ci env variable
