package mes.domain

import cats.Show

sealed trait Modality

case object Intravenous   extends Modality
case object Oral          extends Modality
case object Intramuscular extends Modality

object Modality {
  implicit val modalityShow: Show[Modality] = {
    case Intravenous   => "IV"
    case Oral          => "OR"
    case Intramuscular => "IM"
  }
}