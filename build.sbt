ThisBuild / name         := "futiles"
ThisBuild / organization := "com.markatta"

ThisBuild / crossScalaVersions := Seq("2.12.15", "2.11.12", "2.13.8")
ThisBuild / scalaVersion       := crossScalaVersions.value.last

val flagsFor11 = Seq(
  "-Xlint:_",
  "-feature",
  "-deprecation",
  "-Yconst-opt",
  "-Ywarn-infer-any",
  "-Yclosure-elim",
  "-Ydead-code"
)

val flagsFor12 = Seq(
  "-Xlint:_",
  "-feature",
  "-deprecation",
  "-Ywarn-infer-any",
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-opt-inline-from:<sources>",
  "-opt:l:method"
)

val flagsFor13 = Seq(
  "-Xlint:_",
  "-feature",
  "-deprecation",
  "-opt-inline-from:<sources>",
  "-opt:l:method"
)

ThisBuild / scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n == 13 =>
      flagsFor13
    case Some((2, n)) if n == 12 =>
      flagsFor12
    case Some((2, n)) if n == 11 =>
      flagsFor11
  }
}

libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "3.0.8" % "test")

// releasing
releaseCrossBuild := true
licenses := Seq(
  "Apache License, Version 2.0" -> url(
    "http://www.apache.org/licenses/LICENSE-2.0"
  )
)
homepage               := Some(url("https://github.com/johanandren/futiles"))
publishMavenStyle      := true
Test / publishArtifact := false
pomIncludeRepository   := { _ => false }
publishTo              := sonatypePublishTo.value

scmInfo := Some(
  ScmInfo(url("https://github.com/johanandren/futiles"), "git@github.com:johanandren/futiles.git")
)

developers := List(
  Developer("johanandren", "Johan Andrén", "johan@markatta.com", url("https://markatta.com/johan/codemonkey"))
)

// Disable publish for now
ThisBuild / githubWorkflowPublishTargetBranches := Seq()
