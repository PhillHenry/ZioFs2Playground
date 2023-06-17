package uk.co.odinconsultants
import zio.{Schedule, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.Console.*
import zio.*

object ZioFibers extends ZIOAppDefault {
  override def run: ZIO[Any & ZIOAppArgs & Scope, Any, Any] = for {
    _ <- (ZIO.logInfo("Sleeping") *> ZIO.sleep(5.seconds) *> ZIO.logInfo("woken")).fork
    // .fork (after repeat - below): this terminates after just "Sleeping" and "woken"
    _ <- ZIO.logInfo("heartbeat").repeat(Schedule.fixed(1.second)) *> ZIO.logInfo("you never see this but it compiles")
  } yield {
    println("yield") // this is never reached
  }
}
