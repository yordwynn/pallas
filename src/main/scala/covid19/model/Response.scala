package covid19.model

import io.circe.Decoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder

class Response(val items: List[CovidData])

object Russia {
  implicit val customConfig: Configuration = Configuration.default.copy(
    transformMemberNames = {
      case "items" => "Items"
    }
  )
  implicit val decoder: Decoder[Response] = deriveConfiguredDecoder
}
