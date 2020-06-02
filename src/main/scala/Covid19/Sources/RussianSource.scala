package Covid19.Sources

import Covid19.Protocol.{CovidData, Response}
import cats.effect.IO
import io.circe.parser.decode
import sttp.client.{Identity, NothingT, SttpBackend, basicRequest}
import sttp.client._

final class RussianSource(implicit backend: SttpBackend[Identity, Nothing, NothingT]) extends Source {
  override val baseUrl: String = "https://covid19.rosminzdrav.ru/wp-json/api/mapdata/"

  override def getInfected: IO[Response] = {
    IO.pure(basicRequest.get(uri"$baseUrl"))
      .map(request => request.send().body)
      .map(responseToResponseRussia)
  }

  private def responseToResponseRussia(response: Either[String, String]): Response = {
    response
      .fold(_ => decode[Response](""), b => decode[Response](b))
      .fold(_ => new Response(Nil), b => b)
  }

  override def getInfectedByLocation(isoCode: String): IO[Option[CovidData]] =
    getInfected.map(rm => rm.items.find(_.isoCode.contains(isoCode)))
}
