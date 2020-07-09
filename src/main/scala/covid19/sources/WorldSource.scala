package covid19.sources

import covid19.model.{CategoryBuilder, CategoryName, Confirmed, CovidData, Dead, InfectedCategory, Recovered}
import covid19.countries.countries
import covid19.model
import cats.effect.{ContextShift, IO}
import sttp.client._
import sttp.client.{SttpBackend, basicRequest}

import scala.reflect.ClassTag

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
    } yield CovidData(countryNames.head, isoCode.toUpperCase(), confirmed, recovered, dead)
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

  override def getInfected: IO[model.Response] = {
    for {
      confFib <- getSummaryByCategory[Confirmed].start
      deadFib <- getSummaryByCategory[Dead].start
      recFib <- getSummaryByCategory[Recovered].start

      recovered <- recFib.join
      dead <- deadFib.join
      confirmed <- confFib.join

      items = fuseSummary(confirmed, dead, recovered)
    } yield new model.Response(items)
  }

  def fuseSummary(confirmed: Map[String, Confirmed], dead: Map[String, Dead], recovered: Map[String, Recovered]): List[CovidData] = {
    val keys = (confirmed.keys ++ dead.keys ++ recovered.keys).toSet
    keys.map { key =>
      val location = countries.find{ case (_, code) => code == key }.fold("")(_._1)
      val confByKey = confirmed.getOrElse(key, Confirmed(key, 0))
      val deadByKey = dead.getOrElse(key, Dead(key, 0))
      val recByKey = recovered.getOrElse(key, Recovered(key, 0))

      CovidData(location, key, confByKey, recByKey, deadByKey)
    }.toList
  }

  private def getRequest[C <: InfectedCategory: CategoryName] =
    IO.pure(basicRequest.get(uri"${baseUrl}time_series_covid19_${CategoryName[C].name}_global.csv"))

  private def getSummaryByCategory[C <: InfectedCategory: CategoryName: CategoryBuilder: ClassTag]: IO[Map[String, C]] = {
    getRequest
      .map(request => request.send().body)
      .map {
        response => response.fold(_ => List.empty[C], extract[C])
      }
      .map {
        data => data.groupMapReduce(_.isoCode)(x => x)((left, right) => CategoryBuilder[C].build(left.isoCode, left.count + right.count))
      }
  }

  private def extract[C <: InfectedCategory: CategoryBuilder: ClassTag](data: String): List[C] = {
    data
      .split("\n").tail
      .map(_.split(","))
      .collect{
        case a if countries.contains(a(1)) => CategoryBuilder[C].build(countries(a(1)), a.last.trim.toInt)
      }
      .toList
  }
}
