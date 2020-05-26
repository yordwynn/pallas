package Covid19

import Covid19.Protocol.{InfectedCountry, ResponseMinzdrav}
import Covid19.Sources.{Jhu, RussianSource}
import cats.effect.{ContextShift, IO}
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}

import scala.concurrent.ExecutionContext
import scala.jdk.FutureConverters._

class JavaWrapper {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val sttpBackend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
  val source = new Jhu()

  def getSummaryByCountry(countryCode: String): java.util.concurrent.CompletionStage[InfectedCountry] = {
    source.getSummaryByCountry(countryCode).unsafeToFuture().asJava
  }

  def getInfectedInRussia: java.util.concurrent.CompletionStage[ResponseMinzdrav] = {
    new RussianSource().getInfected.unsafeToFuture().asJava
  }
}
