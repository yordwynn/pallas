package Covid19

import Covid19.Protocol.{CovidData, ResponseRussia}
import Covid19.Sources.{WorldSource, RussianSource}
import cats.effect.{ContextShift, IO}
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}

import scala.concurrent.ExecutionContext
import scala.jdk.FutureConverters._

class JavaWrapper {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val sttpBackend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
  val source = new WorldSource()

  def getSummaryByCountry(countryCode: String): java.util.concurrent.CompletionStage[CovidData] = {
    source.getInfectedByLocation(countryCode).unsafeToFuture().asJava
  }

  def getInfectedInRussia: java.util.concurrent.CompletionStage[ResponseRussia] = {
    new RussianSource().getInfected.unsafeToFuture().asJava
  }
}
