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
      .map(_.fold(_ => "", b => b))
      .map(decode[ResponseMinzdrav](_))
      .map(_.fold(_ => ResponseMinzdrav(Nil), b => b))
  }

  override def getInfectedByRegion(isoCode: String): IO[Option[InfectedRegion]] =
    getInfected.map(rm => rm.items.find(_.isoCode.contains(isoCode)))
}
