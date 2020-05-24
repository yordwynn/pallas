package Covid19.Protocol

import enumeratum._

sealed abstract class InfectedCategory(override val entryName: String) extends EnumEntry

object InfectedCategory extends Enum[InfectedCategory] {
  val values: IndexedSeq[InfectedCategory] = findValues

  case object Confirmed extends InfectedCategory("confirmed")
  case object Recovered extends InfectedCategory("recovered")
  case object Dead extends InfectedCategory("deaths")
}
