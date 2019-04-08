package mes

import cats.Show
import cats.effect._
import cats.implicits._
import fs2.Pipe
import io.chrisdavenport.log4cats.Logger
import mes.service._

sealed abstract class Service[F[_]: Sync](
  val rxnorm: RxNormAlg[F],
  val log: Logger[F],
  val admin: AdminAlg[F]
) {

  def countByDrug: Pipe[F, domain.AdminEvent, sigma.DrugReport] = _
    .map(_._2)
    .foldMap(admin.aggregateId)
    .evalTap(showReport)

  def countByDrugClass: Pipe[F, domain.AdminEvent, sigma.ClassReport] = _
    .map(_._2)
    .evalMap(admin.aggregateClass(rxnorm))
    .foldMonoid
    .evalTap(showReport)

  def countByModality: Pipe[F, domain.AdminEvent, sigma.ModalityReport] = _
    .map(_._2)
    .foldMap(admin.aggregateModality)
    .evalTap(showReport)

  def showReport[A](a: A)(implicit S: Show[A]): F[Unit] =
    log.info(S.show(a))
}

object Service {

  def apply[F[_]: ConcurrentEffect: Timer]: Resource[F, Service[F]] = ???
}
