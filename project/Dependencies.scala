import sbt._

object Version {
  val sttp        = "2.0.1"
  val catsEffect  = "2.1.3"
  val enumeratum  = "1.6.1"
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
}