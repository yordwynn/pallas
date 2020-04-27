package Covid19

import Covid19.Protocol.{Response, Summary}
import Covid19.Sources.Jhu
import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.FutureConverters._

class javaWrapper {
  implicit val context: ExecutionContext = ExecutionContext.global
  implicit val sttpBackend: SttpBackend[Future, Nothing, WebSocketHandler] = AsyncHttpClientFutureBackend()
  val source = new Jhu()

  def getSummaryByCountry(countryCode: String): java.util.concurrent.CompletionStage[Response] = {
    source.getSummaryByCountry(countryCode).asJava
  }
}
