package Covid19.Sources

import Covid19.Protocol.{CategoryName, Confirmed, Dead, InfectedCategory, CovidData, Recovered}
import Covid19.Countries.countries
import cats.effect.{ContextShift, IO}
import sttp.client._
import sttp.client.{SttpBackend, basicRequest}

sealed trait WorldSource {
  def baseUrl: String
  def getSummaryByCountry(countryCode: String): IO[CovidData]
}

final class Jhu(implicit backend: SttpBackend[Identity, Nothing, NothingT], implicit val cs: ContextShift[IO]) extends WorldSource {
  override val baseUrl: String = "https://raw.githubusercontent.com/CSSEGISandData/2019-nCoV/master/csse_covid_19_data/csse_covid_19_time_series/"

  override def getSummaryByCountry(countryCode: String): IO[CovidData] = {
    val countryNames = getNamesByCountryCode(countryCode)

    for {
      confFib <- getConfirmedByCountry(countryNames).start
      deadFib <- getDeadByCountry(countryNames).start
      recFib <- getRecoveredByCountry(countryNames).start
      recovered <- recFib.join
      dead <- deadFib.join
      confirmed <- confFib.join
    } yield CovidData(countryNames.head, countryCode, confirmed, recovered, dead)
  }

  private def getConfirmedByCountry(countryNames: Seq[String]): IO[Confirmed] =
    getSummaryByCountryByCategory[Confirmed](countryNames).map(Confirmed)

  private def getDeadByCountry(countryNames: Seq[String]): IO[Dead] =
    getSummaryByCountryByCategory[Dead](countryNames).map(Dead)

  private def getRecoveredByCountry(countryNames: Seq[String]): IO[Recovered] =
    getSummaryByCountryByCategory[Recovered](countryNames).map(Recovered)

  private def getSummaryByCountryByCategory[C <: InfectedCategory: CategoryName](countryNames: Seq[String]): IO[Int] = {
    val requestIo = IO.pure(basicRequest.get(uri"${baseUrl}time_series_covid19_${CategoryName[C].name}_global.csv"))

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
      map(_.last.trim.toInt).sum
  }

  private def getNamesByCountryCode(countryCode: String): List[String] = {
    countries.filter {
      case (_, code) => code == countryCode.toUpperCase
    }.map {
      case (name, _) => name
    }.toList
  }
}