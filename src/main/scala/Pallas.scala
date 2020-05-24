import Covid19.JavaWrapper
import Covid19.Protocol.InfectedCategory

object Pallas {
  def main(args: Array[String]): Unit = {
    val x = new JavaWrapper().getSummaryByCountry("ru").toCompletableFuture
    while (!x.isDone)
    println(x.toCompletableFuture.get())
    println(InfectedCategory.Recovered)
  }
}

