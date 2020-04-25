import Covid19.Sources.Jhu
import sttp.client._
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object Pallas {
  def main(args: Array[String]): Unit = {
    implicit val context: ExecutionContext = ExecutionContext.global
    implicit val sttpBackend: SttpBackend[Future, Nothing, WebSocketHandler] = AsyncHttpClientFutureBackend()

    val country: String = "Russia"

    val confirmed = new Jhu().getSummaryByCountry(country)
    Await.result(confirmed, Duration.Inf)

    confirmed.onComplete{
      case Success(value) => print(value)
      case Failure(exception) => print(exception)
    }
  }
}

