package mes

import cats.Show
import cats.effect._
import cats.implicits._
import cats.kernel.Monoid
import fs2._
import mes.domain._
import mes.sigma.Summary

object App extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    Service[IO].use { implicit S =>
      import S._

      val txs: Stream[IO, AdminEvent] = data
        .generateAdmins()

      txs
        .broadcastThrough(
          (a: Stream[IO, AdminEvent]) => countByDrug(a)
            .evalTap(saveToDb[IO, Map[String, Double]])
            .map(r => Monoid[Summary].empty
            .copy(drugs = r)),
          (b: Stream[IO, AdminEvent]) => countByDrugClass(b)
            .map(r => Monoid[Summary].empty
            .copy(classes = r)),
          (c: Stream[IO, AdminEvent]) => countByModality(c)
            .map(r => Monoid[Summary].empty
            .copy(modalities = r))
        )
        .foldMonoid
        .evalTap(r => log.info(r.show))
        .compile
        .drain

    } as ExitCode.Success

  /*
  A mock call to a data store, e.g. a database or blob store
   */
  private[this] def saveToDb[F[_]: Sync, A](
    a: A
  )(implicit
    S: Service[F],
    H: Show[A]): F[Unit] =
    S.log.info(show"saving to db: $a")
}
