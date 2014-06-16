Nice.scalaProject

scalaVersion := "2.11.1"

organization := "ohnosequences"

name := "tabula"

description := "tabula project"

bucketSuffix := "era7.com"

libraryDependencies ++= Seq(
  "ohnosequences" %% "scarph" % "0.1.0-SNAPSHOT",
  "ohnosequences" %% "type-sets" % "0.4.0-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "2.1.3" % "test",
  "com.amazonaws" % "aws-java-sdk" % "1.7.3"
)

dependencyOverrides += "org.apache.httpcomponents" % "httpclient" % "4.2"

dependencyOverrides += "commons-codec" % "commons-codec" % "1.7"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.1.2"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.2"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.1.2"

dependencyOverrides += "joda-time" % "joda-time" % "2.3"


