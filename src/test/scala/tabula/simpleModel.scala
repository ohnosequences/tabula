package ohnosequences.tabula.test

import ohnosequences.tabula._
import ohnosequences.scarph._


object simpleModel {

  object id extends Attribute[Int]
  object name extends Attribute[String]
  object age extends Attribute[Int]
  object email extends Attribute[String]
  object serializedCrap extends Attribute[Bytes]
  // object nono extends Attribute[Traversable[Array[Float]]]

  case object UsersTable extends HashKeyTableType (
    name = "users",
    key = Hash(id),
    region = EU
  ) {}
  // object WrongHashTable extends TableType (
  //   name = "users",
  //   key = Hash(serializedCrap),
  //   region = EU
  // )

  case object UserItem extends ItemType(UsersTable)
  implicit val user_name = UserItem has name
  implicit val user_age = UserItem has age

  case object FunnyUserItem extends ItemType(UsersTable)
  implicit val funnyUser_name = FunnyUserItem has name
  implicit val funnyUser_email = FunnyUserItem has email
  implicit val funnyUser_serializedCrap = FunnyUserItem has serializedCrap

  // predicates

  // TODO add typeclasses so that we can write name EQ "piticli". This will improve type inference too
  val pred = UserItem.AND(UserItem.EMPTY, EQ[name.type](name, "piticli"))
  // val wrongPred = UserItem.AND(UserItem.EMPTY, EQ[email.type](email, "oh@uh.com"))
  val nowOK = FunnyUserItem.AND(FunnyUserItem.EMPTY, EQ[email.type](email, "oh@uh.com"))

  // ideally I'd like something like
  // val pred = UserItem ? (name EQ "piticli")

  // alt predicates
  val apred = PredicateOn(UserItem) and EQ[name.type](name, "piticli")
  // val wrongapred = PredicateOn(UserItem) and EQ[email.type](email, "oh@uh.com")
  val anowOK = PredicateOn(FunnyUserItem) and EQ[email.type](email, "oh@uh.com")

  import AnyPredicate._
  import Condition._

  val superPred = UserItem ? (name === "piticli") and (age ≤ 34)

}