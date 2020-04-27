package Covid19

import Covid19.Protocol.Summary
import Covid19.Sources.Jhu
import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.FutureConverters._

class JavaWrapper {
  implicit val context: ExecutionContext = ExecutionContext.global
  implicit val sttpBackend: SttpBackend[Future, Nothing, WebSocketHandler] = AsyncHttpClientFutureBackend()
  val source = new Jhu()

  def getSummaryByCountry(countryCode: String): java.util.concurrent.CompletionStage[Summary] = {
    source.getSummaryByCountry(countryCode).map(x => x.asInstanceOf[Summary]).asJava
  }
}
