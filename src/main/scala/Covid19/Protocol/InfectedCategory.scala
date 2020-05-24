package Covid19.Protocol

import enumeratum._

sealed abstract class InfectedCategory extends EnumEntry.Lowercase

object InfectedCategory extends Enum[InfectedCategory] {
  val values: IndexedSeq[InfectedCategory] = findValues

  case object Confirmed extends InfectedCategory
  case object Recovered extends InfectedCategory
  case object Deaths extends InfectedCategory
}
