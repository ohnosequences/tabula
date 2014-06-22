package ohnosequences.tabula.test

import ohnosequences.typesets._
import ohnosequences.tabula._
import ohnosequences.scarph._
import shapeless.test._

object simpleModel {

  case object id extends Attribute[Int]
  case object name extends Attribute[String]
  object age extends Attribute[Int]
  object email extends Attribute[String]
  object serializedCrap extends Attribute[Bytes]
  object departments extends Attribute[Set[String]]

  // Float is not a valid type for an attribute
  illTyped("""
  object nono extends Attribute[Traversable[Array[Float]]]
  """)

  // departments attribute cannot be a primary key:
  illTyped("""
  object WrongHashTable extends HashKeyTable (
    name = "users",
    hashKey = departments,
    region = EU
  )
  """)

  case object UsersTable extends HashKeyTable (
    name = "users",
    hashKey = id,
    region = EU
  )

  case object RandomTable extends CompositeKeyTable (
    name = "someStuff",
    hashKey = id,
    rangeKey = name,
    region = EU
  )

  case object UserItem extends Item(UsersTable)
  implicit val user_props = UserItem has name :~: age :~: ∅

  case object FunnyUserItem extends Item(UsersTable)
  implicit val funnyUser_props = FunnyUserItem has name :~: email :~: serializedCrap :~: departments :~: ∅

  // predicates
  import AnyPredicate._
  import Condition._

  val namePred = SimplePredicate(UserItem, EQ[name.type](name, "piticli"))
  val ageAndPred = AND(namePred, age < 18)
  val  ageOrPred =  OR(namePred, age > 18)
  val agePred = namePred and (age < 18)

  val emailPred = SimplePredicate(FunnyUserItem, EQ[email.type](email, "oh@uh.com"))

  val longOrPred  = UserItem ? (name === "piticli") or (name === "clipiti") or (age < 10) or (age > 34)
  val longAndPred = UserItem ? (name === "piticli") and (name === "clipiti") and (age < 10) and (age > 34)
  val orAgeExt = longOrPred or (age ≥ 5)
  // No mixing and/or
  illTyped("longOrPred and (age ≥ 5)")

  val userHasName = UserItem ? (name isThere)

  val userNotInDpt = FunnyUserItem ? (departments ∌ "sales")
  val userInDpt = FunnyUserItem ? (departments ∋ "IT")

  // import OnlyWitnKeyConditions._
  implicitly[OnlyWitnKeyConditions[namePred.type]]   //(OnlyWitnKeyConditions.simple)
  implicitly[OnlyWitnKeyConditions[ageOrPred.type]]  //(OnlyWitnKeyConditions2.or)
  implicitly[OnlyWitnKeyConditions[ageAndPred.type]] //(OnlyWitnKeyConditions2.and)
  implicitly[OnlyWitnKeyConditions[agePred.type]]
  illTyped("implicitly[OnlyWitnKeyConditions[userHasName.type]]")
  illTyped("implicitly[OnlyWitnKeyConditions[userInDpt.type]]")
}
