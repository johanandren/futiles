ThisBuild / name         := "futiles"
ThisBuild / organization := "com.markatta"

ThisBuild / crossScalaVersions := Seq("2.12.18", "2.13.12")
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
Test / publishArtifact := false
pomIncludeRepository   := { _ => false }

scmInfo := Some(
  ScmInfo(url("https://github.com/johanandren/futiles"), "git@github.com:johanandren/futiles.git")
)

developers := List(
  Developer("johanandren", "Johan Andrén", "johan@markatta.com", url("https://markatta.com/johan/codemonkey"))
)

ThisBuild / githubWorkflowJavaVersions := List(
  JavaSpec.temurin("11"),
  JavaSpec.temurin("17")
)

ThisBuild / githubWorkflowTargetBranches := Seq("main")
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(RefPredicate.StartsWith(Ref.Tag("v")))
ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(List("clean", "coverage", "test"), name = Some("Build project"))
)

ThisBuild / githubWorkflowBuildPostamble ++= Seq(
  // See https://github.com/scoverage/sbt-coveralls#github-actions-integration
  WorkflowStep.Sbt(
    List("coverageReport", "coverageAggregate", "coveralls"),
    name = Some("Upload coverage data to Coveralls"),
    env = Map(
      "COVERALLS_REPO_TOKEN" -> "${{ secrets.GITHUB_TOKEN }}",
      "COVERALLS_FLAG_NAME"  -> "Scala ${{ matrix.scala }}"
    )
  )
)
