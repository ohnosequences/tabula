Nice.scalaProject

scalaVersion := "2.11.1"

organization := "ohnosequences"

name := "tabula"

description := "tabula project"

bucketSuffix := "era7.com"

libraryDependencies ++= Seq(
  "ohnosequences" %% "scarph" % "0.1.0",
  "ohnosequences" %% "type-sets" % "0.4.0",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "com.amazonaws" % "aws-java-sdk" % "1.8.0"
)

dependencyOverrides ++= Set(
  "org.apache.httpcomponents" % "httpclient" % "4.2",
  "commons-codec" % "commons-codec" % "1.7",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.1.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.2",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.1.2",
  "joda-time" % "joda-time" % "2.3"
)
