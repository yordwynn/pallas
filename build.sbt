import Dependencies._

name := "pallas"

version := "2.0.0"

scalaVersion := "2.13.1"

libraryDependencies ++= sttp ++ catsEffect ++ circe ++ circeExtras ++ scalaTest