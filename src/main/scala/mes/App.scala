package mes

import cats.effect._
import cats.implicits._

object App extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    Service[IO].use { implicit S =>

      import S._
      import data._

      generateAdmins(
        total = 80000 + 1,
        patients = 5)
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
