package Covid19.Sources

import Covid19.Protocol.Summary
import sttp.client._
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.{SttpBackend, basicRequest}

import scala.concurrent.{ExecutionContext, Future}

sealed trait Source {
  def baseUrl: String
  def getSummaryByCountry(country: String)(implicit context: ExecutionContext, backend: SttpBackend[Future, Nothing, WebSocketHandler]): Future[Summary]
}

final class Jhu extends Source {
  override val baseUrl: String = "https://raw.githubusercontent.com/CSSEGISandData/2019-nCoV/master/csse_covid_19_data/csse_covid_19_time_series/"

  override def getSummaryByCountry(country: String)(implicit context: ExecutionContext, backend: SttpBackend[Future, Nothing, WebSocketHandler]): Future[Summary] = {
     Future.traverse(Seq(
      getSummaryByCountryByCategory(country, "confirmed"),
      getSummaryByCountryByCategory(country, "deaths"),
      getSummaryByCountryByCategory(country, "recovered")
    ))(y => y).map{
       case List(confirmed, deaths, recovered) => Summary(country, confirmed, deaths, recovered)
       case _ => Summary(country, 0, 0, 0)
     }
  }

  private def getSummaryByCountryByCategory(country: String, category: String)(implicit context: ExecutionContext, backend: SttpBackend[Future, Nothing, WebSocketHandler]): Future[Int] = {
    val request = basicRequest.get(uri"${baseUrl}time_series_covid19_${category}_global.csv")
    request.send().map(_.body).map {
      case Left(_) => 0
      case Right(s) => extract(s, country)
    }
  }

  private def extract(data: String, country: String): Int = {
    Option(data.split("\n").toList).
      map {
        l1 => l1.map(_.split(",").toList).
          filter(l2 => l2(1) == country).
          map(_.last.toInt).sum
      }.getOrElse(0)
  }
}

final class CovidApi extends Source {
  override val baseUrl: String = "https://api.covid19api.com/"

  override def getSummaryByCountry(country: String)(implicit context: ExecutionContext, backend: SttpBackend[Future, Nothing, WebSocketHandler]): Future[Summary] = ???
}