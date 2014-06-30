
```scala
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

```


------

### Index

+ src
  + test
    + scala
      + tabula
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]
        + [resourceLists.scala][test/scala/tabula/resourceLists.scala]
        + impl
          + [irishService.scala][test/scala/tabula/impl/irishService.scala]
  + main
    + scala
      + [tabula.scala][main/scala/tabula.scala]
      + tabula
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + impl
          + [DynamoDBExecutors.scala][main/scala/tabula/impl/DynamoDBExecutors.scala]
          + [Configuration.scala][main/scala/tabula/impl/Configuration.scala]
          + executors
            + [CreateTable.scala][main/scala/tabula/impl/executors/CreateTable.scala]
            + [GetItem.scala][main/scala/tabula/impl/executors/GetItem.scala]
            + [UpdateTable.scala][main/scala/tabula/impl/executors/UpdateTable.scala]
            + [DeleteTable.scala][main/scala/tabula/impl/executors/DeleteTable.scala]
            + [DeleteItem.scala][main/scala/tabula/impl/executors/DeleteItem.scala]
            + [DescribeTable.scala][main/scala/tabula/impl/executors/DescribeTable.scala]
            + [PutItem.scala][main/scala/tabula/impl/executors/PutItem.scala]
          + [AttributeImplicits.scala][main/scala/tabula/impl/AttributeImplicits.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [states.scala][main/scala/tabula/states.scala]
        + actions
          + [CreateTable.scala][main/scala/tabula/actions/CreateTable.scala]
          + [GetItem.scala][main/scala/tabula/actions/GetItem.scala]
          + [UpdateTable.scala][main/scala/tabula/actions/UpdateTable.scala]
          + [DeleteTable.scala][main/scala/tabula/actions/DeleteTable.scala]
          + [DeleteItem.scala][main/scala/tabula/actions/DeleteItem.scala]
          + [DescribeTable.scala][main/scala/tabula/actions/DescribeTable.scala]
          + [PutItem.scala][main/scala/tabula/actions/PutItem.scala]
        + [executors.scala][main/scala/tabula/executors.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
        + [attributes.scala][main/scala/tabula/attributes.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]

[test/scala/tabula/simpleModel.scala]: simpleModel.scala.md
[test/scala/tabula/resourceLists.scala]: resourceLists.scala.md
[test/scala/tabula/impl/irishService.scala]: impl/irishService.scala.md
[main/scala/tabula.scala]: ../../../main/scala/tabula.scala.md
[main/scala/tabula/predicates.scala]: ../../../main/scala/tabula/predicates.scala.md
[main/scala/tabula/accounts.scala]: ../../../main/scala/tabula/accounts.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: ../../../main/scala/tabula/impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/Configuration.scala]: ../../../main/scala/tabula/impl/Configuration.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: ../../../main/scala/tabula/impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: ../../../main/scala/tabula/impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: ../../../main/scala/tabula/impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: ../../../main/scala/tabula/impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: ../../../main/scala/tabula/impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: ../../../main/scala/tabula/impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: ../../../main/scala/tabula/impl/executors/PutItem.scala.md
[main/scala/tabula/impl/AttributeImplicits.scala]: ../../../main/scala/tabula/impl/AttributeImplicits.scala.md
[main/scala/tabula/regions.scala]: ../../../main/scala/tabula/regions.scala.md
[main/scala/tabula/states.scala]: ../../../main/scala/tabula/states.scala.md
[main/scala/tabula/actions/CreateTable.scala]: ../../../main/scala/tabula/actions/CreateTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: ../../../main/scala/tabula/actions/GetItem.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: ../../../main/scala/tabula/actions/UpdateTable.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: ../../../main/scala/tabula/actions/DeleteTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: ../../../main/scala/tabula/actions/DeleteItem.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: ../../../main/scala/tabula/actions/DescribeTable.scala.md
[main/scala/tabula/actions/PutItem.scala]: ../../../main/scala/tabula/actions/PutItem.scala.md
[main/scala/tabula/executors.scala]: ../../../main/scala/tabula/executors.scala.md
[main/scala/tabula/items.scala]: ../../../main/scala/tabula/items.scala.md
[main/scala/tabula/resources.scala]: ../../../main/scala/tabula/resources.scala.md
[main/scala/tabula/actions.scala]: ../../../main/scala/tabula/actions.scala.md
[main/scala/tabula/tables.scala]: ../../../main/scala/tabula/tables.scala.md
[main/scala/tabula/attributes.scala]: ../../../main/scala/tabula/attributes.scala.md
[main/scala/tabula/services.scala]: ../../../main/scala/tabula/services.scala.md
[main/scala/tabula/conditions.scala]: ../../../main/scala/tabula/conditions.scala.md