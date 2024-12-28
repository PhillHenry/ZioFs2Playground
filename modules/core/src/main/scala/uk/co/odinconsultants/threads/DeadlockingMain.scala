package uk.co.odinconsultants.threads
import cats.effect.std.Dequeue
import cats.effect.{IO, IOApp}

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.*

object DeadlockingMain extends IOApp.Simple {

  def incrementer(counter: AtomicInteger) = IO { counter.addAndGet(1) }
  def pausingIncrementer(counter: AtomicInteger) = for {
    x <- incrementer(counter)
    _ <- IO.sleep(10.millis)
  } yield x
  val nRuns = 1000

  def producing(q: Dequeue[IO, Int], counter: AtomicInteger) = for {
    i <- pausingIncrementer(counter)
    _ <- q.offer(i)
  } yield i

  def transferring(from: Dequeue[IO, Int], to: Dequeue[IO, Int]) = for {
    x <- from.take
    _ <- to.offer(x)
  } yield x

  override def run: IO[Unit] = for {
    q1 <- Dequeue.unbounded[IO, Int]
    q2 <- Dequeue.unbounded[IO, Int]
    c1 = new AtomicInteger(0)
    _ <- producing(q1, c1).replicateA(nRuns)
      .race(transferring(q1, q2).foreverM)
      .race(transferring(q2, q1).foreverM)
    x <- q2.take
    y <- q1.take
  } yield {
    println(s"Counter #1 = ${c1.get}, final q2 = $x, final q1 = $y")
  }
}
