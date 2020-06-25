package Covid19

import Covid19.Protocol.CovidData

class JavaWrapperTest extends org.scalatest.funsuite.AnyFunSuite {
  val jw = new JavaWrapper()

  test("return infected in the world") {
    val worldFuture = jw.getInfectedInWorld.toCompletableFuture
    while (!worldFuture.isDone) {}
    val worldResult = worldFuture.get()
    val russiaFromWorld = worldResult.items.find(_.isoCode.contains("RU"))

    val russiaFuture = jw.getSummaryByCountry("RU").toCompletableFuture
    while (!russiaFuture.isDone) {}
    val russiaResult = russiaFuture.get()

    assert(worldResult.items.nonEmpty && russiaFromWorld.contains(russiaResult))
  }

  test("return infected in russia") {
    val request = new JavaWrapper().getSummaryByCountry("ru").toCompletableFuture
    while (!request.isDone) {}
    val result = request.get()

    assert(result.confirmed > 0 && result.deaths > 0 && result.recovered > 0 && result.isoCode.contains("RU"))
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
