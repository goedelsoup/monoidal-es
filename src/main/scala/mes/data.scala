package mes

import cats.effect.IO
import ciris.Secret
import fs2._
import org.scalacheck._
import mes.domain._

object data {

  def generateAdmins(
    total: Int = 1000,
    patients: Int = 10,
    drugs: Int = 100)
  : Stream[IO, AdminEvent] =
    Stream.fromIterator[IO, AdminEvent] {

      val genPatients = Gen
        .listOfN(patients, gen.mrnGen)

      val admins = (for (_ <- Range(1, total))
        yield gen.adminGen(Gen.Parameters.default, rng.Seed.random()))
        .flatten

      val generator =
        for {
          patients <- genPatients
          patient  <- Gen.oneOf(patients)
          result    = admins
            .map(patient -> _)
            .toIterator
        } yield result

      generator.pureApply(Gen
        .Parameters.default,
        rng.Seed.random())
    }
}
