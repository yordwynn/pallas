package Covid19.Protocol

sealed trait Infected

final case class Confirmed(count: Int) extends Infected
final case class Dead(count: Int) extends Infected
final case class Recovered(count: Int) extends Infected