import de.heikoseeberger.sbtheader.license.Apache2_0

scalaVersion := "2.11.5"
version := "1.0-SNAPSHOT"
name := "futiles"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

headers := Map(
  "scala" -> Apache2_0("2015", "Heiko Seeberger")
)