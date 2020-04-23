import sttp.client._
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object Pallas {
  def main(args: Array[String]): Unit = {
    implicit val context: ExecutionContext = ExecutionContext.global
    implicit val sttpBackend: SttpBackend[Future, Nothing, WebSocketHandler] = AsyncHttpClientFutureBackend()

    val country: String = "Russia"
    val request = basicRequest.get(
      uri"https://raw.githubusercontent.com/CSSEGISandData/2019-nCoV/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv"
    )
    val response = request.send().map(_.body).map {
      case Left(_) => 0
      case Right(s) => parse(s, country)
    }.map(print(_))
    Await.result(response, Duration.Inf)
  }

  def parse(data: String, country: String): Int = {
    Option(data.split("\n").toList).
      map {
        l1 => l1.map(s => s.split(",").toList).
          filter(l2 => l2(1) == country).
          map(_.last.toInt).sum
      }.getOrElse(0)
  }
}
