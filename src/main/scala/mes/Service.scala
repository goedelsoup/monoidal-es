package mes

import cats.Show
import cats.effect._
import cats.implicits._
import fs2.Pipe
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
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
    .evalTap(showReport[Map[String, Double]])

  def countByDrugClass: Pipe[F, domain.AdminEvent, ClassReport] = _
    .map(_._2)
    .evalMap(admin.aggregateClass(rxnorm))
    .foldMonoid
    .evalTap(showReport[Map[String, Double]])

  def countByModality: Pipe[F, domain.AdminEvent, ModalityReport] = _
    .map(_._2)
    .foldMap(admin.aggregateModality)
    .evalTap(showReport[ModalityReport])

  def showReport[A](a: A)
                   (implicit S: Show[A]): F[Unit] =
    log.info(S.show(a))
}

object Service {

  def apply[F[_]: ConcurrentEffect: Timer]: Resource[F, Service[F]] =
    for {
      r <- Resource.liftF(RxNormAlg.interpreterForApi[F])
      l  = Slf4jLogger.getLogger[F]
      a  = AdminAlg[F]
    } yield new Service[F](r, l, a)
}
