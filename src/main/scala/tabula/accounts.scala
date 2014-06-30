package ohnosequences.tabula

trait AnyAccount {

  val id: String
  val canonical_id: String
}

case class Account(val id: String, val canonical_id: String) extends AnyAccount