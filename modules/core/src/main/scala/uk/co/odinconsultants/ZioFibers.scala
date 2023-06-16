package uk.co.odinconsultants
import zio.{Schedule, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.Console.*
import zio.*

object ZioFibers extends ZIOAppDefault {
  override def run: ZIO[Any & ZIOAppArgs & Scope, Any, Any] = for {
    _ <- (ZIO.logInfo("Sleeping") *> ZIO.sleep(5.seconds) *> ZIO.logInfo("woken")).fork
    _ <- ZIO.logInfo("heartbeat").repeat(Schedule.fixed(1.second))
  } yield {
    println("yield")
  }
}
