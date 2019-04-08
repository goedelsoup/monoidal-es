package mes

import ciris.Secret
import org.scalacheck.Gen

import mes.domain._

object gen {

  val mrnGen: Gen[Secret[String]] = Gen
    .alphaNumStr
    .map(Secret.apply)

  val drugIdGen: Gen[DrugId] =
    for {
      a <- Gen.frequency(
        98 -> Gen.const("C"),
        2 -> Gen.const("R")
      )
      _b <- Gen.chooseNum(3, 7, 9)
      b <- Gen.listOfN(_b, Gen.numChar).map(_.mkString)
    } yield a + b

  val drugClassGen: Gen[DrugClass] =
    Gen.oneOf(
      Anthracycline,
      Platinum,
      TopoisomeraseI,
      TopoisomeraseII)

  val modalityGen: Gen[Modality] = Gen
    .oneOf(Intravenous, Oral, Intramuscular)

  val volumeGen: Gen[Double] = Gen
    .posNum[Double]

  val adminGen: Gen[Administration] =
    for {
      i <- drugIdGen
      m <- modalityGen
      v <- volumeGen
    } yield Administration(i, m, v)

}
