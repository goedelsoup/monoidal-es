package mes

import cats.Show
import cats.effect._
import cats.implicits._
import cats.kernel.Monoid
import fs2.Pipe
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import mes.domain._
import mes.service._
import mes.sigma._

class Service[F[_]: Sync](
  val rxnorm: RxNormAlg[F],
  val log: Logger[F],
  val admin: AdminAlg[F]
) {

  def countByDrug: Pipe[F, domain.AdminEvent, DrugReport] = _
    .map(_._2)
    .foldMap(admin.aggregateId)
    .evalTap(trace[Map[String, Double]])

  val processDrugAggregate: Pipe[F, AdminEvent, Summary] = _
    .through(countByDrug)
    .evalTap(saveToDb[Map[String, Double]])
    .map(r => Monoid[Summary]
      .empty
      .copy(drugs = r))

  def countByDrugClass: Pipe[F, domain.AdminEvent, ClassReport] = _
    .map(_._2)
    .evalMap(admin.aggregateClass(rxnorm))
    .foldMonoid
    .evalTap(trace[Map[String, Double]])

  val processClassAggregate: Pipe[F, AdminEvent, Summary] = _
    .through(countByDrugClass)
    .map(r => Monoid[Summary]
      .empty
      .copy(classes = r))

  def countByModality: Pipe[F, domain.AdminEvent, ModalityReport] = _
    .map(_._2)
    .foldMap(admin.aggregateModality)
    .evalTap(trace[ModalityReport])

  val processModalityAggregate: Pipe[F, AdminEvent, Summary] = _
    .through(countByModality)
    .map(r => Monoid[Summary]
      .empty
      .copy(modalities = r))

  /*
  A mock call to a data store, e.g. a database or blob store
   */
  private[this] def saveToDb[A](a: A)
                               (implicit
                                H: Show[A]): F[Unit] =
    log.debug(show"saving to db: $a")

  private[this] def trace[A](a: A)
                            (implicit S: Show[A]): F[Unit] =
    log.trace(S.show(a))
}

object Service {

  def apply[F[_]: ConcurrentEffect: Timer]: Resource[F, Service[F]] =
    for {
      r <- Resource.liftF(RxNormAlg.interpreterForApi[F])
      l  = Slf4jLogger.getLogger[F]
      a  = AdminAlg[F]
    } yield new Service[F](r, l, a)
}
