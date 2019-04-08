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

      val maybeValues = for (_ <- Range(1, total))
        yield gen.adminGen(Gen.Parameters.default, rng.Seed.random())

      val generator =
        for {
          patients <- genPatients
          patient  <- Gen.oneOf(patients)
        } yield {
          maybeValues.flatten
            .map(patient -> _)
            .toIterator
        }

      generator.pureApply(Gen.Parameters.default, rng.Seed.random())
    }
}
