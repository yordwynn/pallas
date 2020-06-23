package Covid19.Sources

import Covid19.Protocol.{CategoryName, CategoryBuilder, Confirmed, CovidData, Dead, InfectedCategory, Recovered}
import Covid19.Countries.countries
import Covid19.Protocol
import cats.effect.{ContextShift, IO}
import sttp.client._
import sttp.client.{SttpBackend, basicRequest}

final class WorldSource(implicit backend: SttpBackend[Identity, Nothing, NothingT], implicit val cs: ContextShift[IO]) extends Source {
  override val baseUrl: String = "https://raw.githubusercontent.com/CSSEGISandData/2019-nCoV/master/csse_covid_19_data/csse_covid_19_time_series/"

  override def getInfectedByLocation(isoCode: String): IO[CovidData] = {
    val countryNames = getNamesByCountryCode(isoCode)

    for {
      confFib <- getConfirmedByCountry(countryNames).start
      deadFib <- getDeadByCountry(countryNames).start
      recFib <- getRecoveredByCountry(countryNames).start
      recovered <- recFib.join
      dead <- deadFib.join
      confirmed <- confFib.join
    } yield CovidData(countryNames.head, isoCode, confirmed, recovered, dead)
  }

  private def getConfirmedByCountry(countryNames: Seq[String]): IO[Confirmed] =
    getSummaryByCountryByCategory[Confirmed](countryNames).map(Confirmed("", _))

  private def getDeadByCountry(countryNames: Seq[String]): IO[Dead] =
    getSummaryByCountryByCategory[Dead](countryNames).map(Dead("", _))

  private def getRecoveredByCountry(countryNames: Seq[String]): IO[Recovered] =
    getSummaryByCountryByCategory[Recovered](countryNames).map(Recovered("", _))

  private def getSummaryByCountryByCategory[C <: InfectedCategory: CategoryName](countryNames: Seq[String]): IO[Int] = {
    val requestIo = IO.pure(basicRequest.get(uri"${baseUrl}time_series_covid19_${CategoryName[C].name}_global.csv"))

    requestIo
      .map(request => request.send().body)
      .map {
        response => response.fold(_ => 0, extractByCountry(_, countryNames))
      }
  }

  private def extractByCountry(data: String, countryNames: Seq[String]): Int = {
    data
      .split("\n")
      .map(_.split(","))
      .filter(l => countryNames.contains(l(1)))
      .map(_.last.trim.toInt)
      .sum
  }

  private def getNamesByCountryCode(countryCode: String): List[String] = {
    countries.filter {
      case (_, code) => code == countryCode.toUpperCase
    }.map {
      case (name, _) => name
    }.toList
  }

  override def getInfected: IO[Protocol.Response] = ???

  // I dont like all the code below
  private def getConfirmed: IO[Map[String, Confirmed]] =
    for {
      data <- getSummaryByCategory[Confirmed]
    } yield data.groupMapReduce(_.isoCode)(x => x)((left, right) => Confirmed(left.isoCode, left.count + right.count))

  private def getDead: IO[Map[String, Dead]] =
    for {
      data <- getSummaryByCategory[Dead]
    } yield data.groupMapReduce(_.isoCode)(x => x)((left, right) => Dead(left.isoCode, left.count + right.count))

  private def getRecovered: IO[Map[String, Recovered]] =
    for {
      data <- getSummaryByCategory[Recovered]
    } yield data.groupMapReduce(_.isoCode)(x => x)((left, right) => Recovered(left.isoCode, left.count + right.count))

  private def getRequest[C <: InfectedCategory: CategoryName] =
    IO.pure(basicRequest.get(uri"${baseUrl}time_series_covid19_${CategoryName[C].name}_global.csv"))

  private def getSummaryByCategory[C <: InfectedCategory: CategoryName]: IO[List[C]] = {
    getRequest
      .map(request => request.send().body)
      .map {
        response => response.fold(_ => List.empty: List[C], extract) // trouble is here
      }
  }

  private def extract[C <: InfectedCategory: CategoryBuilder](data: String): List[C] = {
    data
      .split("\n")
      .map(_.split(","))
      .map(a => (countries(a(1)), a.last.trim.toInt))
      .map {
        case (isoCode, count) => CategoryBuilder[C].build(isoCode, count)
      }
      .toList
  }
}
