package Covid19.Protocol

import io.circe.Decoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

sealed trait Response

final case class ResponseRussia(items: List[CovidData]) extends Response

object ResponseRussia {
  implicit val customConfig: Configuration = Configuration.default.copy(
    transformMemberNames = {
      case "items" => "Items"
    }
  )
  implicit val decoder: Decoder[ResponseRussia] = deriveConfiguredDecoder
}
