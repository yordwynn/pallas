package Covid19.Sources

import Covid19.Protocol.{Response, Summary}
import Covid19.Countries.countries
import cats.data.NonEmptyList
import cats.effect.{ContextShift, IO}
import cats.syntax.parallel._
import sttp.client._
import sttp.client.{SttpBackend, basicRequest}

sealed trait Source {
  def baseUrl: String
  def getSummaryByCountry(countryCode: String): IO[Response]
}

final class Jhu(implicit backend: SttpBackend[Identity, Nothing, NothingT], implicit val cs: ContextShift[IO]) extends Source {
  override val baseUrl: String = "https://raw.githubusercontent.com/CSSEGISandData/2019-nCoV/master/csse_covid_19_data/csse_covid_19_time_series/"

  override def getSummaryByCountry(countryCode: String): IO[Response] = {
    val filter = getFilter(countryCode)

    val requests = NonEmptyList.of(
      getSummaryByCountryByCategory(filter, "confirmed"),
      getSummaryByCountryByCategory(filter, "deaths"),
      getSummaryByCountryByCategory(filter, "recovered")
    )

    requests.parSequence.map(x => x.toList).map {
      case List(confirmed, deaths, recovered) => Summary(countryCode, confirmed, recovered, deaths)
    }
  }

  private def getSummaryByCountryByCategory(filter: Seq[String], category: String): IO[Int] = {
    val requestIo = IO.pure(basicRequest.get(uri"${baseUrl}time_series_covid19_${category}_global.csv"))

    requestIo.flatMap(request => IO(request.send())).flatMap(response => IO(response.body)).flatMap {
      case Left(_) => IO.pure(0)
      case Right(s) => IO(extract(s, filter))
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

  override def getSummaryByCountry(countryCode: String): IO[Response] = ???
}