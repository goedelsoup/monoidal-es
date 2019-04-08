package mes

import ciris.Secret
import mes.domain._
import org.scalacheck.Gen

object gen {

  val mrnGen: Gen[Secret[String]] = Gen
    .alphaNumStr
    .map(Secret.apply)

  val drugIdGen: Gen[DrugId] = Gen
    .alphaUpperStr

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
