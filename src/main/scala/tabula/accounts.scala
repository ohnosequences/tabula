package ohnosequences.tabula

case object accounts {

  trait AnyAccount {

    val id: String
    val canonical_id: String
  }

  case class Account(val id: String, val canonical_id: String) extends AnyAccount

}
