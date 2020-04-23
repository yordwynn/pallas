package Covid19.Sources

import Covid19.Protocol.Summary

import scala.concurrent.{ExecutionContext, Future}

sealed trait Source {
  def baseUrl: String
  def getSummaryByCountry(country: String)(implicit context: ExecutionContext): Future[Summary]
}

sealed class Jhu extends Source {
  override val baseUrl: String = "https://raw.githubusercontent.com/CSSEGISandData/2019-nCoV/master/csse_covid_19_data/csse_covid_19_time_series/"

  override def getSummaryByCountry(country: String)(implicit context: ExecutionContext): Future[Summary] = {
    Future(Summary("", 0, 0, 0))
  }
}

sealed class CovidApi extends Source {
  override val baseUrl: String = "https://api.covid19api.com/"

  override def getSummaryByCountry(country: String)(implicit context: ExecutionContext): Future[Summary] = ???
}