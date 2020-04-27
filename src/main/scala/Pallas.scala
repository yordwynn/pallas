import Covid19.javaWrapper

object Pallas {
  def main(args: Array[String]): Unit = {
    val x = new javaWrapper().getSummaryByCountry("ru").toCompletableFuture
    while (!x.isDone)
    println(x.toCompletableFuture.get())
  }
}

