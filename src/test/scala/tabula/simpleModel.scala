package ohnosequences.tabula.test

import ohnosequences.cosas._, typeSets._, properties._, records._
import ohnosequences.tabula._, attributes._

import shapeless.test._

object simpleModel {

  object id extends Attribute[Num]("id")
  object name extends Attribute[String]("name")
  object age extends Attribute[Num]("age")
  object email extends Attribute[String]("email")
  object serializedCrap extends Attribute[Bytes]("serializedCrap")
  object departments extends Attribute[Set[String]]("departments")

  // departments property cannot be a primary key:
  illTyped("""
  object WrongHashTable extends HashKeyTable (
    name = "users",
    hashKey = departments,
    region = EU
  )
  """)

  case object UsersTable extends Table (
    name = "users",
    primaryKey = HashKey(name),
    region = EU
  )

  case object RandomTable extends Table (
    name = "someStuff",
    primaryKey = CompositeKey(id, name),
    region = EU
  )

  // you can't create a property of a non-valid type
  illTyped("""
  case object boolProperty extends Attribute[Boolean]("boolProperty")
  """)

  // // but you cannot use it for creating an Item, because it's one of `ValidValues` type union
  // illTyped("""
  // case object WrongItem extends Item("wrongItem", UsersTable, id :~: boolProperty :~: ∅)
  // """)

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
