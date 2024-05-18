package uk.co.odinconsultants.scala3
import cats.Functor

case class ForecastInt(x: Int)
case class ForecastString(x: String)
case class TradeInt(x: Int)
case class TradeString(x: String)

type ForecastIn = ForecastInt | ForecastString
type TradeIn    = TradeInt | TradeString

class ForecastingTracer {
  def commonFunctionNameNotInherited: Unit = println(
    "ForecastingTracer.commonFunctionNameNotInherited"
  )
}

class TradingTracer {
  def commonFunctionNameNotInherited: Unit = println("TradingTracer.commonFunctionNameNotInherited")
}

type Duck[In] = In match
  case ForecastIn => ForecastingTracer
  case TradeIn    => TradingTracer

object UnionTypesMain {

  def main(args: Array[String]): Unit = {
    unionTypes(List(new ForecastingTracer))
    implicit val forecastingTracer = new ForecastingTracer
    doDuckType(List(ForecastInt(1)))
  }

  /**
   * Uncommenting TradeIn means a compilation failure on x.commonFunctionNameNotInherited even though
   * it's a funtcion on TradingTracer
   */
  def doDuckType[A <: ForecastIn/* | TradeIn*/](xs: List[A])(implicit x: Duck[A]): Unit = {
    println(x.commonFunctionNameNotInherited) // because of the defn of Duck, we know this must be a ForecastingTracer
  }

  def unionTypes[A <: ForecastingTracer | TradingTracer](xs: List[A]): Unit = {
  }

}
