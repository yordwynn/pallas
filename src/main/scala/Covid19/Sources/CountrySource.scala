package Covid19.Sources

import Covid19.Protocol.{CovidData, ResponseRussia}
import cats.effect.IO
import sttp.client._
import sttp.client.{SttpBackend, basicRequest}
import io.circe.parser._

sealed trait CountrySource {
  def baseUrl: String
  def getInfected: IO[ResponseRussia]
  def getInfectedByRegion(isoCode: String): IO[Option[CovidData]]
}

final class RussianSource(implicit backend: SttpBackend[Identity, Nothing, NothingT]) extends CountrySource {
  override val baseUrl: String = "https://covid19.rosminzdrav.ru/wp-json/api/mapdata/"

  override def getInfected: IO[ResponseRussia] = {
    IO.pure(basicRequest.get(uri"$baseUrl"))
      .map(request => request.send().body)
      .map(responseToResponseMinzdrav)
  }

  private def responseToResponseMinzdrav(response: Either[String, String]): ResponseRussia = {
    response
      .fold(_ => decode[ResponseRussia](""), b => decode[ResponseRussia](b))
      .fold(_ => ResponseRussia(Nil), b => b)
  }

  override def getInfectedByRegion(isoCode: String): IO[Option[CovidData]] =
    getInfected.map(rm => rm.items.find(_.isoCode.contains(isoCode)))
}
