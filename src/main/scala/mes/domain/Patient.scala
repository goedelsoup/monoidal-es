package mes.domain

final case class Patient(
  mrn: MRN,
  name: Option[Name]
)
