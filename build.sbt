Nice.scalaProject

organization := "ohnosequences"

name := "tabula"

description := "tabula project"

bucketSuffix := "era7.com"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "ohnosequences" %% "scarph" % "0.2.0",
  "ohnosequences" %% "type-sets" % "0.5.0",
  "org.scalatest" %% "scalatest" % "2.2.2" % "test",
  "com.amazonaws" % "aws-java-sdk" % "1.8.9.1"
)

dependencyOverrides ++= Set(
  "org.apache.httpcomponents" % "httpclient" % "4.2",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.1.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.2",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.1.2",
  "joda-time" % "joda-time" % "2.4"
)
