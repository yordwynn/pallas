package Covid19.Protocol

import io.circe.Decoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

sealed trait Response

final case class Error(error: String) extends Response

final case class InfectedCountry(country: String, confirmed: Int, recovered: Int, deaths: Int) extends Response

object InfectedCountry {
  def apply(country: String, confirmed: Confirmed, recovered: Recovered, deaths: Dead): InfectedCountry =
    InfectedCountry(country, confirmed.count, recovered.count, deaths.count)
}

final case class ResponseMinzdrav(items: List[InfectedRegion]) extends Response

object ResponseMinzdrav {
  implicit val customConfig: Configuration = Configuration.default.copy(
    transformMemberNames = {
      case "items" => "Items"
    }
  )
  implicit val responseMinzdrav: Decoder[ResponseMinzdrav] = deriveConfiguredDecoder
}

final case class InfectedRegion(
                                 locationName: String,
                                 isoCode: Option[String],
                                 confirmed: Int,
                                 recovered: Int,
                                 deaths: Int) extends Response

object InfectedRegion {
  implicit val customConfig: Configuration = Configuration.default.copy(
    transformMemberNames = {
      case "locationName" => "LocationName"
      case "isoCode" => "IsoCode"
      case "confirmed" => "Confirmed"
      case "recovered" => "Recovered"
      case "deaths" => "Deaths"
    }
  )
  implicit val infectedRegion: Decoder[InfectedRegion] = deriveConfiguredDecoder

  def apply(region: String, isoCode: String, confirmed: Confirmed, recovered: Recovered, deaths: Dead): InfectedRegion =
    InfectedRegion(region, Option(isoCode), confirmed.count, recovered.count, deaths.count)
}
