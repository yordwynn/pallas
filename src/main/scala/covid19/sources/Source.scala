package covid19.sources

import covid19.model.{CovidData, Response}
import cats.effect.IO

trait Source {
  def baseUrl: String
  def getInfected: IO[Response]
  def getInfectedByLocation(isoCode: String): IO[CovidData]
}
