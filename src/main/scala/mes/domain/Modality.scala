package mes.domain

sealed trait Modality

case object Intravenous   extends Modality
case object Oral          extends Modality
case object Intramuscular extends Modality
