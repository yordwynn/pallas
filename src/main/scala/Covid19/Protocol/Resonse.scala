package Covid19.Protocol

sealed trait Response

final case class Error(error: String) extends Response

final case class Summary(country: String, confirmed: Int, recovered: Int, deaths: Int) extends Response

object Summary {
  def apply(country: String, confirmed: Confirmed, recovered: Recovered, deaths: Dead): Summary =
    Summary(country, confirmed.count, recovered.count, deaths.count)
}

final case class InfectedRegion(
                                 region: String,
                                 isoCode: String,
                                 confirmed: Int,
                                 recovered: Int,
                                 deaths: Int) extends Response

object InfectedRegion {
  def apply(region: String, isoCode: String, confirmed: Confirmed, recovered: Recovered, deaths: Dead): InfectedRegion =
    InfectedRegion(region, isoCode, confirmed.count, recovered.count, deaths.count)
}
