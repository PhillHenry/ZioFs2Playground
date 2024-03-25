package uk.co.odinconsultants
import zio.Console.*
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

/**
 * This does nothing:
kill -SIGTERM $(jps | grep ZioCleanup | awk '{print $1}')

 */
object ZioCleanup extends ZIOAppDefault {

  val release = ZIO.logInfo("release")

  val resource = ZIO.acquireRelease(ZIO.logInfo("acquire"))(_ => {

      release
  })

  override def run: ZIO[Any & ZIOAppArgs & Scope, Any, Any] = for {
    _ <- resource
    _ <-zio.Console.readLine("press a key")
  } yield ()

}
