package Covid19

class JavaWrapperTest extends org.scalatest.funsuite.AnyFunSuite {
  test("return infected in russia") {
    val request = new JavaWrapper().getSummaryByCountry("ru").toCompletableFuture
    while (!request.isDone) {}
    val result = request.get()

    assert(result.confirmed > 0 && result.deaths > 0 && result.recovered > 0 && result.country == "ru")
  }

  test("return infected in russia by region") {
    val request = new JavaWrapper().getInfectedInRussia.toCompletableFuture
    while (!request.isDone) {}
    val result = request.get().items.find(_.isoCode.contains("RU-KDA"))

    assert(result.isDefined)

    val data = result.get

    assert(data.confirmed > 0 && data.deaths > 0 && data.recovered > 0)
  }
}
