package mes.domain

final case class Name(
  first: Option[String],
  middle: Option[String],
  last: Option[String]
)
