package mes

import cats.Show
import cats.effect._
import cats.implicits._
import mes.sigma._

object App extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    Service[IO].use { implicit S =>
      import S._

      val txs = Mock.TreatmentStream

      val aggregates =
        txs.through(countByDrug)
          .evalTap(saveToDb[Map[String, Double]])
          .concurrently(
        txs.through(countByDrugClass))
          .concurrently(
        txs.through(countByModality))

      aggregates.compile.drain

    } as ExitCode.Success


  def saveToDb[A](a: A)(implicit S: Show[A]): IO[Unit] = IO.unit
}
