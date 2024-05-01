package uk.co.odinconsultants

import cats.syntax.all.*
import cats.effect.*
import fs2.Stream
import fs2.concurrent.Channel
import scala.concurrent.duration.*

/**
 * https://discord.com/channels/632277896739946517/632310980449402880/1234292041182220328
 */
object Ex extends IOApp.Simple {
  def run: IO[Unit] =
    IO.println("hello") >>
      (
        IO.ref(0),
        IO.deferred[Unit],
        Channel.unbounded[IO, String]
      ).flatMapN { (counter, awaitTermination, channel) =>
        val producer =
          Stream
            .repeatEval(counter.updateAndGet(x => x + 1))
            .map(n => s"message $n")
            .metered(100.millis)
            .evalMap { msg =>
              channel.send(msg) <* IO.println(s"produced $msg")
            }

        val consumer =
          channel
            .stream
            .metered(200.millis)
            .evalMap(msg => IO.println(s"consumed $msg"))

        val ctrlC =
          IO.println("SIMULATING Ctrl-C NOW!!!")
            .delayBy(3.seconds)

        // this is where we assemble the graceful behaviour
        // I've decided to add a timeout to the graceful shutdown so that
        // after a while it will actually kill things if the consumer is too slow.
        // You can change this timeout or remove it.
        val detachedConsumer =
          consumer
            .compile
            .drain
            .guarantee(awaitTermination.complete(()).void)
            .start

        val gracefulProducer =
          producer.compile.drain.guarantee {
            channel.close >>
              awaitTermination.get.timeout(10.seconds)
          }

        (detachedConsumer >> gracefulProducer)
          .race(ctrlC)
          .void
      }

}