package Covid19.Sources

import Covid19.Protocol.Summary
import cats.effect.IO

sealed trait CountrySource {
  def baseUrl: String
  def getInfectedByRegion(regionCode: String): IO[Summary]
}

final case class RussianSource() extends CountrySource {
  override val baseUrl: String = "https://covid19.rosminzdrav.ru/wp-json/api/mapdata/"

  override def getInfectedByRegion(regionCode: String): IO[Summary] = ???
}
