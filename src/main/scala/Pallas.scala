import Covid19.JavaWrapper

object Pallas {
  def main(args: Array[String]): Unit = {
    val jw = new JavaWrapper()

    val x = jw.getSummaryByCountry("ru").toCompletableFuture
    val y = jw.getInfectedInRussia.toCompletableFuture

    while (!x.isDone || !y.isDone)

    println(x.get())
    println(y.get())
  }
}

