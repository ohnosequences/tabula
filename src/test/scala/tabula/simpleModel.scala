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
    key = Hash(id),
    region = EU
  ) {}
  // object WrongHashTable extends TableType (
  //   name = "users",
  //   key = Hash(serializedCrap),
  //   region = EU
  // )

  // keys
  object tableId extends Hash(id)
  object compositeKey extends HashRange(id,name)
  // creating values
  val z = tableId ->> (id ->> 2234)
  // this also works
  val z0 = tableId ->> 231231

  // composite key; again both named and raw are possible
  val ckv = compositeKey ->> (
    id ->> 32424, 
    name ->> "Salustiano"
  )
  val ckv0 = compositeKey ->> (312312, "Antonio")

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
  val andAgePred = orNamePred and (age ≥ 5)

  val userHasName = UserItem ? (name isThere)

}
