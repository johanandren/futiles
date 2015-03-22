import de.heikoseeberger.sbtheader.license.Apache2_0

scalaVersion := "2.11.5"
version := "1.0-SNAPSHOT"
name := "futiles"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

headers := Map(
  "scala" -> Apache2_0("2015", "Johan Andrén")
)

// releasing
publishMavenStyle := true
pomIncludeRepository := { _ => false }
publishTo := Some {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    "snapshots" at nexus + "content/repositories/snapshots"
  else
    "releases" at nexus + "service/local/staging/deploy/maven2"
}

pomExtra :=
  <url>https://github.com/johanandren/futiles</url>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:johanandren/futiles.git</url>
      <connection>scm:git:git@github.com:johanandren/futiles.git</connection>
    </scm>
    <developers>
      <developer>
        <id>johanandren</id>
        <name>Johan Andrén</name>
        <email>johan@markatta.com</email>
        <url>https://markatta.com/johan/codemonkey</url>
      </developer>
    </developers>
