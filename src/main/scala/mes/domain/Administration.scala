package mes.domain

final case class Administration(
  drug: DrugId,
  modality: Modality,
  volume: Double
)
