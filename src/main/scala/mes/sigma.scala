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

        def combine(s0: Summary, s1: Summary): Summary =
          Summary(
            drugs      =      s0.drugs |+| s1.drugs,
            classes    =    s0.classes |+| s1.classes,
            modalities = s0.modalities |+| s1.modalities
          )
      }

    implicit val summaryShow: Show[Summary] =
      (s0: Summary) =>
        s"""Summary
           |-------
           |${s0.drugs}
           |${s0.classes}
           |${s0.modalities}
         """.stripMargin
  }
}
