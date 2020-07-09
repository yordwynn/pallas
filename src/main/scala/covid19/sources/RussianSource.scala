package covid19.sources

import covid19.model.{CovidData, Response}
import cats.effect.IO
import io.circe.Decoder
import io.circe.parser.decode
import sttp.client.{Identity, NothingT, SttpBackend, basicRequest}
import sttp.client._

final class RussianSource(implicit backend: SttpBackend[Identity, Nothing, NothingT], decoder: Decoder[Response]) extends Source {
  override val baseUrl: String = "https://covid19.rosminzdrav.ru/wp-json/api/mapdata/"

  override def getInfected: IO[Response] = {
    IO.pure(basicRequest.get(uri"$baseUrl"))
      .map(request => request.send().body)
      .map(responseToResponseRussia)
  }

  private def responseToResponseRussia(response: Either[String, String])(implicit decoder: Decoder[Response]): Response = {
    response
      .fold(_ => decode[Response](""), b => decode[Response](b))
      .fold(_ => new Response(Nil), b => b)
  }

  override def getInfectedByLocation(isoCode: String): IO[CovidData] =
    getInfected.map(
      rm => rm
        .items
        .find(_.isoCode.contains(isoCode))
        .getOrElse(new CovidData("", Some(isoCode), 0, 0, 0))
    )
}
