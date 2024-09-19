package uk.co.odinconsultants.cancel
import cats.effect.{IO, IOApp, Resource}
import fs2.Stream

object StreamMain extends IOApp.Simple {

  val register   = Resource.make(IO.println("register acquire"))(_ => IO.println("register release"))
  val unregister =
    Resource.make(IO.println("unregister acquire"))(_ => IO.println("unregister release"))
  val stream     = Stream((1 to 10)*).evalMap(x => IO.println(s"element $x"))

  override def run: IO[Unit] = {
    val outer  = Stream.resource(register)
    val nested = for {
      _     <- outer
      inner <- stream
    } yield IO.println("yield")

//    Stream.resource(register).flatMap(_ => stream).compile.drain
    nested.compile.drain
  }
}
