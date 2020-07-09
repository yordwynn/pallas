package covid19

import covid19.model.{CovidData, Response}
import covid19.sources.{RussianSource, WorldSource}
import cats.effect.{ContextShift, IO}
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}

import scala.concurrent.ExecutionContext
import scala.jdk.FutureConverters._

class JavaWrapper {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val sttpBackend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

  val worldSource = new WorldSource()
  val russianSource = new RussianSource()

  def getSummaryByCountry(countryCode: String): java.util.concurrent.CompletionStage[CovidData] = {
    worldSource.getInfectedByLocation(countryCode).unsafeToFuture().asJava
  }

  def getInfectedInWorld:  java.util.concurrent.CompletionStage[Response] = {
    worldSource.getInfected.unsafeToFuture().asJava
  }

  def getInfectedInRussia: java.util.concurrent.CompletionStage[Response] = {
    russianSource.getInfected.unsafeToFuture().asJava
  }
}
