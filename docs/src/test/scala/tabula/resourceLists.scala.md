
```scala
package ohnosequences.tabula.test

import ohnosequences.tabula._

object ResourceLists {
  
  import simpleModel._

  type TT = AnyTable :+: AnyTable :+: RNil 
  val uh: TT = UsersTable :+: RandomTable :+: RNil

  trait AnyAction {

    type Resources <: ResourceList
  }
  trait AnyJoinTables extends AnyAction {

    type Resources <: AnyTable :+: AnyTable :+: RNil
    val resources: Resources

    val scndTblKey = resources.tail.head.hashKey
  }

  case class JoinTables[
    T0 <: Singleton with AnyTable,
    T1 <: Singleton with AnyTable
  ]
  (
    val t0: T0,
    val t1: T1
  ) extends AnyJoinTables {

    type Resources = T0 :+: T1 :+: RNil
    val resources = t0 :+: t1 :+: RNil
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