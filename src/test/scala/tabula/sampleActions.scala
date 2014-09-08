package ohnosequences.tabula.sample

import ohnosequences.pointless._, AnyType._, AnyProperty._, AnyTypeSet._, AnyRecord._, AnyTypeUnion._, ops.typeSet._
import ohnosequences.tabula._, AnyPredicate._
import shapeless.test.illTyped

object id extends Property[Num]
object name extends Property[String]
object age extends Property[Num]
object email extends Property[String]
object serializedCrap extends Property[Bytes]
object departments extends Property[Set[String]]

// departments property cannot be a primary key:
// illTyped("""
// object WrongHashTable extends HashKeyTable (
//   name = "users",
//   hashKey = departments,
//   region = EU
// )
// """)

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

object UserItem extends Item (
  "user", 
  UsersTable, 
  name :~: age :~: ∅
)

object FunnyUserItem extends Item(
  "funnyUser",
  UsersTable,
  name :~: email :~: departments :~: ∅
)

// predicates
import AnyPredicate._
import Condition._

object samplePredicates {

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

