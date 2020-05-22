package Covid19

import Covid19.Protocol.Summary
import Covid19.Sources.Jhu
import cats.effect.{ContextShift, IO}
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}

import scala.concurrent.{ExecutionContext}
import scala.jdk.FutureConverters._

class JavaWrapper {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val sttpBackend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
  val source = new Jhu()

  def getSummaryByCountry(countryCode: String): java.util.concurrent.CompletionStage[Summary] = {
    source.getSummaryByCountry(countryCode).map(x => x.asInstanceOf[Summary]).unsafeToFuture().asJava
  }
}
