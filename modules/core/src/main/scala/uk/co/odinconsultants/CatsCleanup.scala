package uk.co.odinconsultants
import cats.effect.{IO, IOApp, Resource}

/**
 * This does nothing in Cats effects 3.3.12:
 *  kill -SIGTERM $(jps | grep CatsCleanup | awk '{print $1}')
 * but in 3.5.4 the Resource is released and the output looks like:
acquire
release

Process finished with exit code 143 (interrupted by signal 15:SIGTERM)
 */
object CatsCleanup extends IOApp.Simple {

  val resource = Resource.make(IO.println("acquire"))(_ => IO.println("release"))

  def run: IO[Unit] = for {
    _ <- resource.use(_ => IO.readLine)
  } yield {
    ()
  }

}
