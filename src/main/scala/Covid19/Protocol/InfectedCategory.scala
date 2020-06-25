package Covid19.Protocol

sealed trait InfectedCategory {
  def isoCode: String
  def count: Int
}
final case class Confirmed(isoCode: String, count: Int) extends InfectedCategory
final case class Dead(isoCode: String, count: Int) extends InfectedCategory
final case class Recovered(isoCode: String, count: Int) extends InfectedCategory

object InfectedCategory {
  implicit val categoryConfirmed: CategoryName[Confirmed] = CategoryName.instance[Confirmed]("confirmed")
  implicit val categoryDead: CategoryName[Dead] = CategoryName.instance[Dead]("deaths")
  implicit val categoryRecovered: CategoryName[Recovered] = CategoryName.instance[Recovered]("recovered")

  implicit val builderConfirmed: CategoryBuilder[Confirmed] = CategoryBuilder.instance[Confirmed]((isoCode, count) => Confirmed(isoCode, count))
  implicit val builderDead: CategoryBuilder[Dead] = CategoryBuilder.instance[Dead]((isoCode, count) => Dead(isoCode, count))
  implicit val builderRecovered: CategoryBuilder[Recovered] = CategoryBuilder.instance[Recovered]((isoCode, count) => Recovered(isoCode, count))
}

sealed trait CategoryName[T <: InfectedCategory] {
  def name: String
}

object CategoryName {
  def apply[T <: InfectedCategory](implicit ev: CategoryName[T]): CategoryName[T] = ev
  def instance[T <: InfectedCategory](n: String): CategoryName[T] = new CategoryName[T] { val name: String = n }
}

sealed trait CategoryBuilder[T <: InfectedCategory] {
  def build(isoCode: String, count: Int): T
}

object CategoryBuilder {
  def apply[T <: InfectedCategory](implicit ev: CategoryBuilder[T]): CategoryBuilder[T] = ev

  def instance[T <: InfectedCategory](builder: (String, Int) => T): CategoryBuilder[T] =
    new CategoryBuilder[T] {
      def build(isoCode: String, count: Int): T = builder(isoCode, count)
    }
}
