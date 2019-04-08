package mes

import cats.Show
import cats.kernel._
import cats.implicits._
import mes.domain._

object sigma {

  type DrugReport     = Map[DrugId, Double]
  type ClassReport    = Map[DrugClass, Double]
  type ModalityReport = Map[Modality, Double]

  final case class Summary(
    drugs: DrugReport,
    classes: ClassReport,
    modalities: ModalityReport
  )

  object Summary {

    implicit val summaryMonoid: Monoid[Summary] =
      new Monoid[Summary] {
        def empty: Summary = Summary(Map.empty, Map.empty, Map.empty)

        def combine(s0: Summary, s1: Summary): Summary = ???
      }
  }
}
