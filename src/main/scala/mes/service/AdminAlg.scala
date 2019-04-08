package mes.service

import cats.Functor
import cats.implicits._
import mes.domain._
import mes.sigma._

/*
A better description of this would be as follows:

def aggregateId(a0: Administration)
               (implicit I: cats.Id[F]): F[DrugReport] =
  I.point(Map[DrugId, Double](a0.drug, a0.volume))
 */
trait AdminAlg[F[_]] {

  /*
  Aggregate volume by ID
   */
  def aggregateId(a0: Administration): DrugReport =
    Map[DrugId, Double](a0.drug -> a0.volume)

  def aggregateClass(R: RxNormAlg[F])
                    (a0: Administration)
                    (implicit F: Functor[F]): F[ClassReport] = R
    .getDrugClass(a0.drug)
    .map(c => Map(c -> a0.volume))

  def aggregateModality(a0: Administration): ModalityReport =
    Map[Modality, Double](a0.modality -> a0.volume)
}

object AdminAlg {

  def apply[F[_]] =
    new AdminAlg[F] {}
}
