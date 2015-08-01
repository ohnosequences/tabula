
```scala
package ohnosequences.tabula

import org.scalatest.FunSuite

import ohnosequences.typesets._, Property._, AnyTag._, TagsOf._
import ohnosequences.scarph._
import ohnosequences.tabula._
import ohnosequences.tabula.impl._, ImplicitConversions._

import shapeless._, poly._
import shapeless.test.typed
import AnyTag._

object TestSetting {
  case object id extends Property[Num]
  case object name extends Property[String]
  case object simpleUserRecord extends Record(id :~: name :~: ?)
  case object normalUserRecord extends Record(id :~: name :~: email :~: color :~: ?)

  object table extends CompositeKeyTable("foo_table", id, name, EU)

  case object simpleUser extends Item(table, simpleUserRecord)

  // more properties:
  case object email extends Property[String]
  case object color extends Property[String]

  case object normalUser extends Item(table, normalUserRecord)

  // creating item is easy and neat:
  val user1 = simpleUser fields (
    (id ->> 123) :~: 
    (name ->> "foo") :~: 
    ?
  )

  // this way the order of properties doesn't matter
  val user2 = normalUser fields (
    (name is "foo") :~: 
    (color is "orange") :~:
    (id is 123) :~: 
    (email is "foo@bar.qux") :~:
    ?
  )

}

class itemsSuite extends FunSuite {
  import TestSetting._

  test("accessing item properties") {

    assert (

      user1.get(id) === 123
    )
    assert(user1.get(name) === "foo")
  }

  test("tags/keys of a representation") {
    // won't work; need the alias :-|
    // val keys = implicitly[Keys.Aux[id.Rep :~: name.Rep :~: ?, id.type :~: name.type :~: ?]]
    val tags = TagsOf[TaggedWith[id.type] :~: TaggedWith[name.type] :~: ?]
    assert(tags(user1) === simpleUser.record.properties)
    assert(tags(user1) === (id :~: name :~: ?))
  }

  test("items serialization") {
    // transforming simpleUser to Map
    val tr = FromProperties[
      id.type :~: name.type :~: ?, 
      TaggedWith[id.type]  :~: TaggedWith[name.type]  :~: ?,
      toSDKRep.type,
      SDKRep
    ]
    val map1 = tr(user1)
    println(map1)

    val t = implicitly[FromProperties.Aux[simpleUser.record.Properties, simpleUser.Raw, toSDKRep.type, SDKRep]]
    val ti = implicitly[From.ItemAux[simpleUser.type, toSDKRep.type, SDKRep]]
    val map2 = ti(user1)
    println(map2)
    assert(map1 == map2)

    // forming simpleUser from Map
    val form = ToProperties[SDKRep, simpleUser.record.Properties, simpleUser.Raw, fromSDKRep.type](ToProperties.cons)
    val i2 = form(map2, simpleUser.record.properties)
    println(i2)
    assert(i2 == user1)
  }

  test("item projection") {
    assertResult(user1) {
      user2 as simpleUser.record
    }

    assertTypeError("""
    val wrong = user1 as normalUser
    """)
  }

  test("item extension") {

    case object more extends Record(simpleUser.record.properties ? (email :~: color :~: ?))

    case object extendedUser extends Item(table, more)

    assertResult(user2) {
      user1 as (normalUser.record,
        (color is "orange") :~:
        (email is "foo@bar.qux") :~:
        ?
      )
    }

    // you cannot provide less that it's missing
    assertTypeError("""
    val less = user1 as (normalUser,
        (email is "foo@bar.qux") :~:
        ?
      )
    """)

    // neither you can provide more that was missing
    assertTypeError("""
    val more = user1 as (normalUser,
        (color is "orange") :~:
        (id is 4012) :~:
        (email is "foo@bar.qux") :~:
        ?
      )
    """)
  }

  test("item update") {
    val martin = normalUser fields (
      (name is "Martin") :~:
      (id is 1) :~:
      (color is "dark-salmon") :~:
      (email is "coolmartin@scala.org") :~:
      ?
    )

    assert((user2 update (name is "qux")) === 
      (normalUser fields (
          (id is user2.get(id)) :~: 
          (name is "qux") :~: 
          (color is user2.get(color)) :~:
          (email is user2.get(email)) :~:
          ?
        ))
    )

    assert((user2 update ((name is "qux") :~: (id is 42) :~: ?)) === 
      (normalUser fields (
          (id is 42) :~: 
          (name is "qux") :~: 
          (color is user2.get(color)) :~:
          (email is user2.get(email)) :~:
          ?
        ))
    )

    assert((user2 update (martin: normalUser.Raw)) === martin)
  }

}

```


------

### Index

+ src
  + main
    + scala
      + tabula
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + actions
          + [CreateTable.scala][main/scala/tabula/actions/CreateTable.scala]
          + [DeleteItem.scala][main/scala/tabula/actions/DeleteItem.scala]
          + [DeleteTable.scala][main/scala/tabula/actions/DeleteTable.scala]
          + [DescribeTable.scala][main/scala/tabula/actions/DescribeTable.scala]
          + [GetItem.scala][main/scala/tabula/actions/GetItem.scala]
          + [PutItem.scala][main/scala/tabula/actions/PutItem.scala]
          + [Query.scala][main/scala/tabula/actions/Query.scala]
          + [UpdateTable.scala][main/scala/tabula/actions/UpdateTable.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]
        + [executors.scala][main/scala/tabula/executors.scala]
        + impl
          + actions
            + [GetItem.scala][main/scala/tabula/impl/actions/GetItem.scala]
            + [PutItem.scala][main/scala/tabula/impl/actions/PutItem.scala]
            + [Query.scala][main/scala/tabula/impl/actions/Query.scala]
          + [Configuration.scala][main/scala/tabula/impl/Configuration.scala]
          + [DynamoDBExecutors.scala][main/scala/tabula/impl/DynamoDBExecutors.scala]
          + executors
            + [CreateTable.scala][main/scala/tabula/impl/executors/CreateTable.scala]
            + [DeleteItem.scala][main/scala/tabula/impl/executors/DeleteItem.scala]
            + [DeleteTable.scala][main/scala/tabula/impl/executors/DeleteTable.scala]
            + [DescribeTable.scala][main/scala/tabula/impl/executors/DescribeTable.scala]
            + [GetItem.scala][main/scala/tabula/impl/executors/GetItem.scala]
            + [PutItem.scala][main/scala/tabula/impl/executors/PutItem.scala]
            + [Query.scala][main/scala/tabula/impl/executors/Query.scala]
            + [UpdateTable.scala][main/scala/tabula/impl/executors/UpdateTable.scala]
          + [ImplicitConversions.scala][main/scala/tabula/impl/ImplicitConversions.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [states.scala][main/scala/tabula/states.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
      + [tabula.scala][main/scala/tabula.scala]
  + test
    + scala
      + tabula
        + impl
          + [irishService.scala][test/scala/tabula/impl/irishService.scala]
        + [items.scala][test/scala/tabula/items.scala]
        + [resourceLists.scala][test/scala/tabula/resourceLists.scala]
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]

[main/scala/tabula/accounts.scala]: ../../../main/scala/tabula/accounts.scala.md
[main/scala/tabula/actions/CreateTable.scala]: ../../../main/scala/tabula/actions/CreateTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: ../../../main/scala/tabula/actions/DeleteItem.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: ../../../main/scala/tabula/actions/DeleteTable.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: ../../../main/scala/tabula/actions/DescribeTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: ../../../main/scala/tabula/actions/GetItem.scala.md
[main/scala/tabula/actions/PutItem.scala]: ../../../main/scala/tabula/actions/PutItem.scala.md
[main/scala/tabula/actions/Query.scala]: ../../../main/scala/tabula/actions/Query.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: ../../../main/scala/tabula/actions/UpdateTable.scala.md
[main/scala/tabula/actions.scala]: ../../../main/scala/tabula/actions.scala.md
[main/scala/tabula/conditions.scala]: ../../../main/scala/tabula/conditions.scala.md
[main/scala/tabula/executors.scala]: ../../../main/scala/tabula/executors.scala.md
[main/scala/tabula/impl/actions/GetItem.scala]: ../../../main/scala/tabula/impl/actions/GetItem.scala.md
[main/scala/tabula/impl/actions/PutItem.scala]: ../../../main/scala/tabula/impl/actions/PutItem.scala.md
[main/scala/tabula/impl/actions/Query.scala]: ../../../main/scala/tabula/impl/actions/Query.scala.md
[main/scala/tabula/impl/Configuration.scala]: ../../../main/scala/tabula/impl/Configuration.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: ../../../main/scala/tabula/impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: ../../../main/scala/tabula/impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: ../../../main/scala/tabula/impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: ../../../main/scala/tabula/impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: ../../../main/scala/tabula/impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: ../../../main/scala/tabula/impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: ../../../main/scala/tabula/impl/executors/PutItem.scala.md
[main/scala/tabula/impl/executors/Query.scala]: ../../../main/scala/tabula/impl/executors/Query.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: ../../../main/scala/tabula/impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/ImplicitConversions.scala]: ../../../main/scala/tabula/impl/ImplicitConversions.scala.md
[main/scala/tabula/items.scala]: ../../../main/scala/tabula/items.scala.md
[main/scala/tabula/predicates.scala]: ../../../main/scala/tabula/predicates.scala.md
[main/scala/tabula/regions.scala]: ../../../main/scala/tabula/regions.scala.md
[main/scala/tabula/resources.scala]: ../../../main/scala/tabula/resources.scala.md
[main/scala/tabula/services.scala]: ../../../main/scala/tabula/services.scala.md
[main/scala/tabula/states.scala]: ../../../main/scala/tabula/states.scala.md
[main/scala/tabula/tables.scala]: ../../../main/scala/tabula/tables.scala.md
[main/scala/tabula.scala]: ../../../main/scala/tabula.scala.md
[test/scala/tabula/impl/irishService.scala]: impl/irishService.scala.md
[test/scala/tabula/items.scala]: items.scala.md
[test/scala/tabula/resourceLists.scala]: resourceLists.scala.md
[test/scala/tabula/simpleModel.scala]: simpleModel.scala.md