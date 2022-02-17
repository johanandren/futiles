ThisBuild / name         := "futiles"
ThisBuild / organization := "com.markatta"

ThisBuild / crossScalaVersions := Seq("2.12.15", "2.11.12", "2.13.8")
ThisBuild / scalaVersion       := crossScalaVersions.value.last
ThisBuild / scalacOptions ++= Seq("-feature", "-deprecation", "-Xfatal-warnings", "-Xlint")

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
