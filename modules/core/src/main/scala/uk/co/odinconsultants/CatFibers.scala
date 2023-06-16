package uk.co.odinconsultants

import cats.effect.{IO, IOApp, OutcomeIO}

import scala.concurrent.duration.*

object CatFibers extends IOApp.Simple {

  def run: IO[Unit]                  = for {
    _ <- (IO.println("Sleeping") *> IO.sleep(5.second) *> IO.println("Awake")).background.use {(_: IO[OutcomeIO[Unit]]) =>
      (IO.println("heartbeat") *> IO.sleep(1.second)).foreverM
    }
  } yield {
    println("yield") // never reached
  }
}
