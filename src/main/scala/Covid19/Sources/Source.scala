package Covid19.Sources

import Covid19.Protocol.{Confirmed, Dead, InfectedCategory, Recovered, Summary}
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
    val countryNames = getNamesByCountryCode(countryCode)

    for {
      confFib <- getConfirmedByCountry(countryNames).start
      deadFib <- getDeadByCountry(countryNames).start
      recFib <- getRecoveredByCountry(countryNames).start
      recovered <- recFib.join
      dead <- deadFib.join
      confirmed <- confFib.join
    } yield Summary(countryCode, confirmed, recovered, dead)
  }

  private def getConfirmedByCountry(countryNames: Seq[String]): IO[Confirmed] =
    getSummaryByCountryByCategory(countryNames, InfectedCategory.Confirmed).map(Confirmed)

  private def getDeadByCountry(countryNames: Seq[String]): IO[Dead] =
    getSummaryByCountryByCategory(countryNames, InfectedCategory.Deaths).map(Dead)

  private def getRecoveredByCountry(countryNames: Seq[String]): IO[Recovered] =
    getSummaryByCountryByCategory(countryNames, InfectedCategory.Recovered).map(Recovered)

  private def getSummaryByCountryByCategory(countryNames: Seq[String], category: InfectedCategory): IO[Int] = {
    val requestIo = IO.pure(basicRequest.get(uri"${baseUrl}time_series_covid19_${category.entryName}_global.csv"))

    requestIo
      .map(request => request.send().body)
      .map {
        response => response.fold(_ => 0, extract(_, countryNames))
      }
  }

  private def extract(data: String, countryNames: Seq[String]): Int = {
    data.
      split("\n").toList.
      map(_.split(",").toList).
      filter(l => countryNames.contains(l(1))).
      map(_.last.toInt).sum
  }

  private def getNamesByCountryCode(countryCode: String): List[String] = {
    countries.filter {
      case (_, code) => code == countryCode.toUpperCase
    }.map {
      case (name, _) => name
    }.toList
  }
}