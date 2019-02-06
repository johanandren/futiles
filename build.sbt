name := "futiles"
organization := "com.markatta"

scalaVersion := "2.12.3"
crossScalaVersions := Seq(scalaVersion.value, "2.11.11", "2.10.5", "2.13.0-M5")
scalacOptions ++= Seq("-feature", "-deprecation", "-Xfatal-warnings", "-Xlint")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.6-SNAP6" % "test"
)

// releasing
releaseCrossBuild := true
licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
homepage := Some(url("https://github.com/johanandren/futiles"))
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
publishTo := Some {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    "snapshots" at nexus + "content/repositories/snapshots"
  else
    "releases" at nexus + "service/local/staging/deploy/maven2"
}

pomExtra :=
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
