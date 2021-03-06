import sbt._

object Version {
  val sttp        = "2.0.1"
  val catsEffect  = "2.1.3"
  val enumeratum  = "1.6.1"
  val circeVersion = "0.12.3"
  val circeExtrasVersion = "0.12.2"
  val scalaTest = "3.1.1"
}

object Dependencies {
  val sttp: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.client" %% "core" % Version.sttp,
    "com.softwaremill.sttp.client" %% "async-http-client-backend-future" % Version.sttp
  )

  val catsEffect: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-effect" % Version.catsEffect
  )

  val enumeratum: Seq[ModuleID] = Seq(
    "com.beachape" %% "enumeratum" % Version.enumeratum
  )

  val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % Version.circeVersion)

  val circeExtras: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-generic-extras" % Version.circeExtrasVersion
  )

  val scalaTest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % Version.scalaTest % "test"
  )
}
