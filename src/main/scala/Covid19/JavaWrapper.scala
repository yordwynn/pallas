package Covid19

import Covid19.Protocol.Summary
import Covid19.Sources.Jhu
import sttp.client.{HttpURLConnectionBackend, Identity, NothingT, SttpBackend}
import sttp.client.asynchttpclient.WebSocketHandler

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.FutureConverters._

class JavaWrapper {
  implicit val context: ExecutionContext = ExecutionContext.global
  implicit val sttpBackend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
  val source = new Jhu()

  def getSummaryByCountry(countryCode: String): java.util.concurrent.CompletionStage[Summary] = {
    source.getSummaryByCountry(countryCode).map(x => x.asInstanceOf[Summary]).asJava
  }
}
