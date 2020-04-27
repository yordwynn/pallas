package Covid19.Sources

import java.util.Locale.IsoCountryCode

import Covid19.Protocol.{Response, Summary}
import Covid19.Countries.countries
import sttp.client._
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.{SttpBackend, basicRequest}

import scala.concurrent.{ExecutionContext, Future}

sealed trait Source {
  def baseUrl: String
  def getSummaryByCountry(countryCode: String)(implicit context: ExecutionContext, backend: SttpBackend[Future, Nothing, WebSocketHandler]): Future[Response]
}

final class Jhu extends Source {
  override val baseUrl: String = "https://raw.githubusercontent.com/CSSEGISandData/2019-nCoV/master/csse_covid_19_data/csse_covid_19_time_series/"

  override def getSummaryByCountry(countryCode: String)(implicit context: ExecutionContext, backend: SttpBackend[Future, Nothing, WebSocketHandler]): Future[Response] = {
    val filter = getFilter(countryCode)

     Future.traverse(Seq(
      getSummaryByCountryByCategory(filter, "confirmed"),
      getSummaryByCountryByCategory(filter, "deaths"),
      getSummaryByCountryByCategory(filter, "recovered")
    ))(y => y).map{
       case List(confirmed, deaths, recovered) => Summary(countryCode, confirmed, deaths, recovered)
     }
  }

  private def getSummaryByCountryByCategory(filter: Seq[String], category: String)(implicit context: ExecutionContext, backend: SttpBackend[Future, Nothing, WebSocketHandler]): Future[Int] = {
    val request = basicRequest.get(uri"${baseUrl}time_series_covid19_${category}_global.csv")
    request.send().map(_.body).map {
      case Left(_) => 0
      case Right(s) => extract(s, filter)
    }
  }

  private def extract(data: String, filter: Seq[String]): Int = {
    data.
      split("\n").toList.
      map(_.split(",").toList).
      filter(l => filter.contains(l(1))).
      map(_.last.toInt).sum
  }

  private def getFilter(countryCode: String): List[String] = {
    countries.filter {
      case (_, code) => code == countryCode.toUpperCase
    }.map {
      case (name, _) => name
    }.toList
  }
}

final class CovidApi extends Source {
  override val baseUrl: String = "https://api.covid19api.com/"

  override def getSummaryByCountry(countryCode: String)(implicit context: ExecutionContext, backend: SttpBackend[Future, Nothing, WebSocketHandler]): Future[Response] = ???
}