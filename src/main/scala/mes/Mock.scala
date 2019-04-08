package mes

import cats.effect._
import fs2._
import mes.domain._

object Mock {

  val TreatmentStream: Stream[IO, (MRN, Administration)] = ???
}
