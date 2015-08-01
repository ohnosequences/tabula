
```scala
package ohnosequences

import ohnosequences.typesets._

// in package object only type-aliases
package object tabula {
  
  type Bytes = Array[Byte]
  type Num   = Integer
  // not documented; the API informs you about it if you try not to adhere to it
  type NotSetValues = either[Num]#or[String]#or[Bytes]
  type SetValues = either[Set[Num]]#or[Set[String]]#or[Set[Bytes]]

  type PrimaryKeyValues = NotSetValues
  type ValidValues = NotSetValues#or[Set[Num]]#or[Set[String]]#or[Set[Bytes]]
  type ValuesWithPrefixes = either[String]#or[Bytes]

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

[main/scala/tabula/accounts.scala]: tabula/accounts.scala.md
[main/scala/tabula/actions/CreateTable.scala]: tabula/actions/CreateTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: tabula/actions/DeleteItem.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: tabula/actions/DeleteTable.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: tabula/actions/DescribeTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: tabula/actions/GetItem.scala.md
[main/scala/tabula/actions/PutItem.scala]: tabula/actions/PutItem.scala.md
[main/scala/tabula/actions/Query.scala]: tabula/actions/Query.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: tabula/actions/UpdateTable.scala.md
[main/scala/tabula/actions.scala]: tabula/actions.scala.md
[main/scala/tabula/conditions.scala]: tabula/conditions.scala.md
[main/scala/tabula/executors.scala]: tabula/executors.scala.md
[main/scala/tabula/impl/actions/GetItem.scala]: tabula/impl/actions/GetItem.scala.md
[main/scala/tabula/impl/actions/PutItem.scala]: tabula/impl/actions/PutItem.scala.md
[main/scala/tabula/impl/actions/Query.scala]: tabula/impl/actions/Query.scala.md
[main/scala/tabula/impl/Configuration.scala]: tabula/impl/Configuration.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: tabula/impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: tabula/impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: tabula/impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: tabula/impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: tabula/impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: tabula/impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: tabula/impl/executors/PutItem.scala.md
[main/scala/tabula/impl/executors/Query.scala]: tabula/impl/executors/Query.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: tabula/impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/ImplicitConversions.scala]: tabula/impl/ImplicitConversions.scala.md
[main/scala/tabula/items.scala]: tabula/items.scala.md
[main/scala/tabula/predicates.scala]: tabula/predicates.scala.md
[main/scala/tabula/regions.scala]: tabula/regions.scala.md
[main/scala/tabula/resources.scala]: tabula/resources.scala.md
[main/scala/tabula/services.scala]: tabula/services.scala.md
[main/scala/tabula/states.scala]: tabula/states.scala.md
[main/scala/tabula/tables.scala]: tabula/tables.scala.md
[main/scala/tabula.scala]: tabula.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../test/scala/tabula/impl/irishService.scala.md
[test/scala/tabula/items.scala]: ../../test/scala/tabula/items.scala.md
[test/scala/tabula/resourceLists.scala]: ../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/simpleModel.scala]: ../../test/scala/tabula/simpleModel.scala.md