package mes

import cats.effect._
import cats.implicits._

object App extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    Service[IO].use { implicit S =>
      import S._

      data
        .generateAdmins(100000)
        .broadcastThrough(
          processDrugAggregate,
          processClassAggregate,
          processModalityAggregate
        )
        .foldMonoid
        .evalTap(r =>
          log.debug(r.show))
        .compile
        .drain as ExitCode.Success
    }
}
