package uk.co.odinconsultants

import cats.effect.{IO, IOApp, OutcomeIO}

import scala.concurrent.duration.*
import cats.effect.kernel.Outcome.*

object CatFibers extends IOApp.Simple {

  def run: IO[Unit] = for {
    t <- (IO.println("Sleeping") *> IO.sleep(5.seconds) *> IO.println("Awake")).start
    _ <- (IO.println("heartbeat") *> IO.sleep(1.second)).foreverM.start //background.use(_ => IO.println("use"))
    _ <- t.join
  } yield {
    println("This terminates after t finishes")
  }

}
