package Covid19.Sources

import Covid19.Protocol.{InfectedRegion, ResponseMinzdrav}
import cats.effect.IO
import sttp.client._
import sttp.client.{SttpBackend, basicRequest}
import io.circe.parser._

sealed trait CountrySource {
  def baseUrl: String
  def getInfected: IO[ResponseMinzdrav]
  def getInfectedByRegion(isoCode: String): IO[Option[InfectedRegion]]
}

final class RussianSource(implicit backend: SttpBackend[Identity, Nothing, NothingT]) extends CountrySource {
  override val baseUrl: String = "https://covid19.rosminzdrav.ru/wp-json/api/mapdata/"

  override def getInfected: IO[ResponseMinzdrav] = {
    IO.pure(basicRequest.get(uri"$baseUrl"))
      .map(request => request.send().body)
      .map(responseToResponseMinzdrav)
  }

  private def responseToResponseMinzdrav(response: Either[String, String]): ResponseMinzdrav = {
    response
      .fold(_ => decode[ResponseMinzdrav](""), b => decode[ResponseMinzdrav](b))
      .fold(_ => ResponseMinzdrav(Nil), b => b)
  }

  override def getInfectedByRegion(isoCode: String): IO[Option[InfectedRegion]] =
    getInfected.map(rm => rm.items.find(_.isoCode.contains(isoCode)))
}
