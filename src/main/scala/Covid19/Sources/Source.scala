package Covid19.Sources

import Covid19.Protocol.{Confirmed, Dead, InfectedCategory, Recovered, Response, Summary}
import Covid19.Countries.countries
import cats.effect.{ContextShift, IO}
import sttp.client._
import sttp.client.{SttpBackend, basicRequest}

sealed trait Source {
  def baseUrl: String
  def getSummaryByCountry(countryCode: String): IO[Summary]
}

final class Jhu(implicit backend: SttpBackend[Identity, Nothing, NothingT], implicit val cs: ContextShift[IO]) extends Source {
  override val baseUrl: String = "https://raw.githubusercontent.com/CSSEGISandData/2019-nCoV/master/csse_covid_19_data/csse_covid_19_time_series/"

  override def getSummaryByCountry(countryCode: String): IO[Summary] = {
    val filter = getFilter(countryCode)

    for {
      confFib <- getConfirmedByCountry(filter).start
      deadFib <- getDeadByCountry(filter).start
      recFib <- getRecoveredByCountry(filter).start
      recovered <- recFib.join
      dead <- deadFib.join
      confirmed <- confFib.join
    } yield Summary(countryCode, confirmed, recovered, dead)
  }

  private def getConfirmedByCountry(filter: Seq[String]): IO[Confirmed] =
    getSummaryByCountryByCategory(filter, InfectedCategory.Confirmed).map(Confirmed)

  private def getDeadByCountry(filter: Seq[String]): IO[Dead] =
    getSummaryByCountryByCategory(filter, InfectedCategory.Deaths).map(Dead)

  private def getRecoveredByCountry(filter: Seq[String]): IO[Recovered] =
    getSummaryByCountryByCategory(filter, InfectedCategory.Recovered).map(Recovered)

  private def getSummaryByCountryByCategory(filter: Seq[String], category: InfectedCategory): IO[Int] = {
    val requestIo = IO.pure(basicRequest.get(uri"${baseUrl}time_series_covid19_${category.entryName}_global.csv"))

    requestIo
      .map(request => request.send().body)
      .map {
        response => response.fold(_ => 0, extract(_, filter))
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