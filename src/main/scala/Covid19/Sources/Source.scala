package Covid19.Sources

import Covid19.Protocol.{CovidData, Response}
import cats.effect.IO

trait Source {
  def baseUrl: String
  def getInfected: IO[Response]
  def getInfectedByLocation(isoCode: String): IO[Option[CovidData]]
}
