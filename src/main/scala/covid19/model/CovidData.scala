package covid19.model

import io.circe.Decoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

final case class CovidData(
  locationName: String,
  isoCode: Option[String],
  confirmed: Int,
  recovered: Int,
  deaths: Int
)

object CovidData {
  implicit val customConfig: Configuration = Configuration.default.copy(
    transformMemberNames = {
      case "locationName" => "LocationName"
      case "isoCode" => "IsoCode"
      case "confirmed" => "Confirmed"
      case "recovered" => "Recovered"
      case "deaths" => "Deaths"
    }
  )

  implicit val decoder: Decoder[CovidData] = deriveConfiguredDecoder

  def apply(region: String, isoCode: String, confirmed: Confirmed, recovered: Recovered, deaths: Dead): CovidData =
    CovidData(region, Option(isoCode), confirmed.count, recovered.count, deaths.count)
}
