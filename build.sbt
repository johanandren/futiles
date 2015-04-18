import de.heikoseeberger.sbtheader.license.Apache2_0
import sbtrelease.ReleasePlugin.ReleaseKeys

name := "futiles"
organization := "com.markatta"

scalaVersion := "2.11.5"
crossScalaVersions := Seq(scalaVersion.value, "2.10.5")
scalacOptions ++= Seq("-feature", "-deprecation", "-Xfatal-warnings", "-Xlint")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

headers := Map(
  "scala" -> Apache2_0("2015", "Johan Andrén")
)

// releasing
releaseSettings
sonatypeSettings
ReleaseKeys.crossBuild := true
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
