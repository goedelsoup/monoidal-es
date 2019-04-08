package mes

import cats.Show
import cats.effect._
import cats.implicits._
import fs2._
import mes.domain._

object App extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    Service[IO].use { implicit S =>
      import S._

      val txs: Stream[IO, AdminEvent] = data
        .generateAdmins

      val aggregates =
        txs.through(countByDrug)
          .evalTap(saveToDb[IO, Map[String, Double]])
          .concurrently(
        txs.through(countByDrugClass))
          .concurrently(
        txs.through(countByModality))

      aggregates.compile.drain

    } as ExitCode.Success

  /*
  A mock call to a data store, e.g. a database or blob store
   */
  private[this] def saveToDb[F[_]: Sync, A](a: A)
                                           (implicit
                                            S: Service[F],
                                            H: Show[A])
  : F[Unit] =
    S.log.info(show"saving to db: $a")
}
