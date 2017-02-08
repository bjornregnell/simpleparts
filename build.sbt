import sbt._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "se.lth.cs",
      scalaVersion := "2.11.8",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "simpleparts",
    libraryDependencies += "jline" % "jline" % "2.14.3",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )
