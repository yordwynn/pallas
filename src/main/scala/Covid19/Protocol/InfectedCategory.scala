package Covid19.Protocol

sealed trait InfectedCategory
final case class Confirmed(count: Int) extends InfectedCategory
final case class Dead(count: Int) extends InfectedCategory
final case class Recovered(count: Int) extends InfectedCategory

object InfectedCategory {
  implicit val categoryConfirmed: CategoryName[Confirmed] = CategoryName.instance[Confirmed]("confirmed")
  implicit val categoryDead: CategoryName[Dead] = CategoryName.instance[Dead]("deaths")
  implicit val categoryRecovered: CategoryName[Recovered] = CategoryName.instance[Recovered]("recovered")
}

trait CategoryName[T <: InfectedCategory] {
  def name: String
}

object CategoryName {
  def apply[T <: InfectedCategory](implicit ev: CategoryName[T]): CategoryName[T] = ev
  def instance[T <: InfectedCategory](n: String): CategoryName[T] = new CategoryName[T] { val name: String = n }
}
