package mes

import ciris.Secret

package object domain {

  type MRN = Secret[String]

  type DrugId = String

  type DrugClass = String

  type AdminEvent = (MRN, Administration)
}
