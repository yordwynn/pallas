package Covid19

import Covid19.Protocol.{CovidData, Response}
import Covid19.Sources.{RussianSource, WorldSource}
import cats.effect.{ContextShift, IO}
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}

import scala.concurrent.ExecutionContext
import scala.jdk.FutureConverters._

import Covid19.Protocol.Russia._

class JavaWrapper {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val sttpBackend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
  val source = new WorldSource()

  def getSummaryByCountry(countryCode: String): java.util.concurrent.CompletionStage[CovidData] = {
    source.getInfectedByLocation(countryCode).unsafeToFuture().asJava
  }

  def getInfectedInWorld:  java.util.concurrent.CompletionStage[Response] = {
    new WorldSource().getInfected.unsafeToFuture().asJava
  }

  def getInfectedInRussia: java.util.concurrent.CompletionStage[Response] = {
    new RussianSource().getInfected.unsafeToFuture().asJava
  }
}
