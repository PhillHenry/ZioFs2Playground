package uk.co.odinconsultants
import cats.effect.{IO, IOApp, Resource}

/**
 * This does nothing:
 *  kill -SIGTERM $(jps | grep CatsCleanup | awk '{print $1}')
 */
object CatsCleanup extends IOApp.Simple {

  val resource = Resource.make(IO.println("acquire"))(_ => IO.println("release"))

  def run: IO[Unit] = for {
    _ <- resource.use(_ => IO.readLine)
  } yield {
    ()
  }

}
