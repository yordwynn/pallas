import Covid19.JavaWrapper

object Pallas {
  def main(args: Array[String]): Unit = {
    val x = new JavaWrapper().getSummaryByCountry("ru").toCompletableFuture
    while (!x.isDone)
    println(x.toCompletableFuture.get())
  }
}

