package mes.domain

import cats.Show

sealed trait DrugClass

case object Anthracycline extends DrugClass
case object Platinum extends DrugClass
case object TopoisomeraseI extends DrugClass
case object TopoisomeraseII extends DrugClass

object DrugClass {
  implicit val drugClassShow: Show[DrugClass] = {
    case Anthracycline => "Anthracycline"
    case Platinum => "Platinum"
    case TopoisomeraseI => "Topoisomerase I"
    case TopoisomeraseII => "Topoisomerase II"
  }
}