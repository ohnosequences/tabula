Nice.scalaProject

name := "tabula"
organization := "ohnosequences"
description := "Fancy API for the Amazon DynamoDB"
bucketSuffix := "era7.com"

scalaVersion := "2.11.7"
// crossScalaVersions  := Seq("2.10.5", scalaVersion.value)

libraryDependencies ++= Seq(
  "ohnosequences" %% "cosas" % "0.6.0",
  "com.amazonaws" %  "aws-java-sdk-dynamodb" % "1.10.8",
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)

dependencyOverrides ++= Set(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.4"
)

// FIXME:
wartremoverErrors in (Compile, compile) := Seq()
