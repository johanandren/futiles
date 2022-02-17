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
pomIncludeRepository := { _ =>
  false
}
publishTo := Some {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    "snapshots" at nexus + "content/repositories/snapshots"
  else
    "releases" at nexus + "service/local/staging/deploy/maven2"
}

scmInfo := Some(
  ScmInfo(url("https://github.com/johanandren/futiles"), "git@github.com:johanandren/futiles.git")
)

developers := List(
  Developer("johanandren", "Johan Andrén", "johan@markatta.com", url("https://markatta.com/johan/codemonkey"))
)

// Disable publish for now
ThisBuild / githubWorkflowPublishTargetBranches := Seq()

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
