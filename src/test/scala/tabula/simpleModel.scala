package ohnosequences.tabula.test

import ohnosequences.pointless._, AnyTypeSet._
import ohnosequences.tabula._

import shapeless.test._

object simpleModel {

  object id extends Property[Num]
  object name extends Property[String]
  object age extends Property[Num]
  object email extends Property[String]
  object serializedCrap extends Property[Bytes]
  object departments extends Property[Set[String]]

  // departments property cannot be a primary key:
  illTyped("""
  object WrongHashTable extends HashKeyTable (
    name = "users",
    hashKey = departments,
    region = EU
  )
  """)

  case object UsersTable extends HashKeyTable (
    name = "users",
    hashKey = name,
    region = EU
  )

  case object RandomTable extends CompositeKeyTable (
    name = "someStuff",
    hashKey = id,
    rangeKey = name,
    region = EU
  )

  // you can create a property of any type
  case object boolProperty extends Property[Boolean]
  case object recordWithBoolProperty extends Record(boolProperty :~: ∅)
  // but you cannot use it for creating an Item, because it's one of `ValidValues` type union
  illTyped("""
  case object WrongItem extends Item("wrongItem", UsersTable, recordWithBoolProperty.properties)
  """)

  case object UserItemRecord extends Record(name :~: age :~: ∅)
  case object UserItem extends Item("user", UsersTable, UserItemRecord.properties)

  case object FunnyUserItemRecord extends Record(name :~: email :~: serializedCrap :~: departments :~: ∅)
  case object FunnyUserItem extends Item("funnyUser", UsersTable, FunnyUserItemRecord.properties)

  // predicates
  import AnyPredicate._
  import Condition._

  val namePred = UserItem ? (name === "piticli")
  val ageAndPred = AND(namePred, age < 18)
  val  ageOrPred =  OR(namePred, age > 18)
  val agePred = namePred and (age < 18)

  val emailPred = FunnyUserItem ? (email === "oh@uh.com")

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
