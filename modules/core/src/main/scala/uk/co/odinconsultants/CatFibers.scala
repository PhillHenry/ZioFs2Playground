package uk.co.odinconsultants

import cats.effect.{IO, IOApp, OutcomeIO}

import scala.concurrent.duration.*
import cats.effect.kernel.Outcome.*

object CatFibers extends IOApp.Simple {

  def run: IO[Unit] = for {
    _ <- (IO.println("Sleeping") *> IO.sleep(5.second) *> IO.println("Awake")).background.use {
           (outcome: IO[OutcomeIO[Unit]]) =>
             val heartbeats: IO[Nothing] = (IO.println("heartbeat") *> IO.sleep(1.second)).foreverM
             val end: IO[Unit]           = outcome.flatMap { (outcome: OutcomeIO[Unit]) =>
               val end: IO[Unit] = outcome match {
                 case Succeeded(_) => IO.println("succeeded")
                 case _            => IO.println("didn't succeed")
               }
               end
             }
//             heartbeats *> end // end is never reached
             heartbeats *> end
         }
  } yield println("yield") // never reached
}
