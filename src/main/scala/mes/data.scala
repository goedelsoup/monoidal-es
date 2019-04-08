package mes

import cats.effect.IO
import fs2._
import mes.domain._
import org.scalacheck._

object data {

  def generateAdmins: Stream[IO, AdminEvent] =
    Stream.fromIterator[IO, AdminEvent] {

      val patient: MRN = gen.mrnGen
        .pureApply(Gen.Parameters.default, rng.Seed.random())

      val maybeValues = for (_ <- Range(1, 1000))
        yield gen.adminGen(Gen.Parameters.default, rng.Seed.random())

      maybeValues
        .flatten
        .map(patient -> _)
        .toIterator
    }
}
