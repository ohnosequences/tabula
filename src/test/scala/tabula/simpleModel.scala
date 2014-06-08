package ohnosequences.tabula.test

import ohnosequences.tabula._
import ohnosequences.scarph._


object simpleModel {

  case object id extends Attribute[Int]
  case object name extends Attribute[String]
  object age extends Attribute[Int]
  object email extends Attribute[String]
  object serializedCrap extends Attribute[Bytes]
  // object nono extends Attribute[Traversable[Array[Float]]]

  case object UsersTable extends HashKeyTable (
    name = "users",
    hashKey = id,
    region = EU
  )
  // object WrongHashTable extends TableType (
  //   name = "users",
  //   key = Hash(serializedCrap),
  //   region = EU
  // )

  case object RandomTable extends CompositeKeyTable (
    name = "someStuff",
    hashKey = id,
    rangeKey = name,
    region = EU
  )

  case object UserItem extends ItemType(UsersTable)
  implicit val user_name = UserItem has name
  implicit val user_age = UserItem has age

  case object FunnyUserItem extends ItemType(UsersTable)
  implicit val funnyUser_name = FunnyUserItem has name
  implicit val funnyUser_email = FunnyUserItem has email
  implicit val funnyUser_serializedCrap = FunnyUserItem has serializedCrap

  // predicates
  import AnyPredicate._
  import Condition._

  val namePred = SimplePredicate(UserItem, EQ[name.type](name, "piticli"))
  val agePred = AND(namePred, age < 18)

  val emailPred = SimplePredicate(FunnyUserItem, EQ[email.type](email, "oh@uh.com"))

  val orNamePred = UserItem ? (name === "piticli") or (name === "clipiti")
  // wrong! no mixing and/or
  // val andAgePred = orNamePred and (age ≥ 5)
  val orAge = orNamePred or (age ≥ 5)

  val userHasName = UserItem ? (name isThere)

}
