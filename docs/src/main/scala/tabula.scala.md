
```scala
package ohnosequences

import ohnosequences.typesets._

// in package object only type-aliases
package object tabula {
  
  type Bytes = Seq[Byte]
  type Num   = Int
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

[test/scala/tabula/simpleModel.scala]: ../../test/scala/tabula/simpleModel.scala.md
[test/scala/tabula/resourceLists.scala]: ../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../test/scala/tabula/impl/irishService.scala.md
[main/scala/tabula.scala]: tabula.scala.md
[main/scala/tabula/predicates.scala]: tabula/predicates.scala.md
[main/scala/tabula/accounts.scala]: tabula/accounts.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: tabula/impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/Configuration.scala]: tabula/impl/Configuration.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: tabula/impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: tabula/impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: tabula/impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: tabula/impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: tabula/impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: tabula/impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: tabula/impl/executors/PutItem.scala.md
[main/scala/tabula/impl/AttributeImplicits.scala]: tabula/impl/AttributeImplicits.scala.md
[main/scala/tabula/regions.scala]: tabula/regions.scala.md
[main/scala/tabula/states.scala]: tabula/states.scala.md
[main/scala/tabula/actions/CreateTable.scala]: tabula/actions/CreateTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: tabula/actions/GetItem.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: tabula/actions/UpdateTable.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: tabula/actions/DeleteTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: tabula/actions/DeleteItem.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: tabula/actions/DescribeTable.scala.md
[main/scala/tabula/actions/PutItem.scala]: tabula/actions/PutItem.scala.md
[main/scala/tabula/executors.scala]: tabula/executors.scala.md
[main/scala/tabula/items.scala]: tabula/items.scala.md
[main/scala/tabula/resources.scala]: tabula/resources.scala.md
[main/scala/tabula/actions.scala]: tabula/actions.scala.md
[main/scala/tabula/tables.scala]: tabula/tables.scala.md
[main/scala/tabula/attributes.scala]: tabula/attributes.scala.md
[main/scala/tabula/services.scala]: tabula/services.scala.md
[main/scala/tabula/conditions.scala]: tabula/conditions.scala.md