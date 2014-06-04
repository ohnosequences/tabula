Nice.scalaProject

scalaVersion := "2.11.1"

organization := "ohnosequences"

name := "tabula"

description := "tabula project"

bucketSuffix := "era7.com"

libraryDependencies ++= Seq(
  "ohnosequences" %% "scarph" % "0.1.0-SNAPSHOT",
  "ohnosequences" %% "type-sets" % "0.4.0-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "2.1.3" % "test"
)
