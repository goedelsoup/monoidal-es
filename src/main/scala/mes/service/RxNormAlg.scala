package mes.service

import cats.effect.Sync
import mes.domain._

trait RxNormAlg[F[_]] {

  /*
  Retrieve the drug class of a given ID
   */
  def getDrugClass(id: DrugId): F[DrugClass]
}

object RxNormAlg {

  /*
  Here we imagine that we are calling out to an external micro-service to perform the
  classification of a giving drug. Hence, we must wrap this in an effectful context
   */
  def interpreterForApi[F[_]](
    implicit S: Sync[F]
  ): RxNormAlg[F] =
    new RxNormAlg[F] {
      def getDrugClass(id: DrugId): F[DrugClass] =
        Sync[F].pure("platinum compound")
    }
}