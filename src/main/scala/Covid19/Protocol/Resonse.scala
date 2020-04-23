package Covid19.Protocol

sealed trait Response

final case class Error(error: String) extends Response
final case class Dummy(data: String) extends Response
final case class Summary(country: String, confirmed: Int, recovered: Int, deaths: Int) extends Response
