
```scala
package ohnosequences.tabula.impl

import ohnosequences.tabula._, AttributeImplicits._
import com.amazonaws.services.dynamodbv2.model._

// TODO check region of clients
object DynamoDBExecutors {
```

CREATE TABLE

```scala
  implicit def createHashKeyTableExecutor
    [A <: AnyCreateTable with AnyTableAction.withHashKeyTable](a: A)
      (implicit dynamoClient: AnyDynamoDBClient): 
        CreateHashKeyTableExecutor[A] =
        CreateHashKeyTableExecutor[A](a)(dynamoClient)

  implicit def createCompositeKeyTableExecutor
    [A <: AnyCreateTable with AnyTableAction.withCompositeKeyTable](a: A)
      (implicit dynamoClient: AnyDynamoDBClient): 
        CreateCompositeKeyTableExecutor[A] =
        CreateCompositeKeyTableExecutor[A](a)(dynamoClient)
```

DELETE TABLE

```scala
  implicit def deleteTableExecutor[A <: AnyDeleteTable](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      DeleteTableExecutor[A] =
      DeleteTableExecutor[A](a)(dynamoClient)
```

DESCRIBE TABLE

```scala
  implicit def describeTableExecutor[A <: AnyDescribeTable](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      DescribeTableExecutor[A] =
      DescribeTableExecutor[A](a)(dynamoClient)
```

UPDATE TABLE

```scala
  implicit def updateTableExecutor[A <: AnyUpdateTableAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      UpdateTableExecutor[A] =
      UpdateTableExecutor[A](a)(dynamoClient)
```

PUT ITEM

```scala
  implicit def putItemExecutor[A <: AnyPutItemAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      PutItemExecutor[A] =
      PutItemExecutor[A](a)(dynamoClient)
```

GET ITEM

```scala
  implicit def getItemHashKeyExecutor[A <: AnyGetItemHashKeyAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      GetItemHashKeyExecutor[A] =
      GetItemHashKeyExecutor[A](a)(dynamoClient)

  implicit def getItemCompositeKeyExecutor[A <: AnyGetItemCompositeKeyAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      GetItemCompositeKeyExecutor[A] =
      GetItemCompositeKeyExecutor[A](a)(dynamoClient)
```

DELETE ITEM

```scala
  implicit def deleteItemHashKeyExecutor[A <: AnyDeleteItemHashKeyAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      DeleteItemHashKeyExecutor[A] =
      DeleteItemHashKeyExecutor[A](a)(dynamoClient)

  implicit def deleteItemCompositeKeyExecutor[A <: AnyDeleteItemCompositeKeyAction](a: A)
    (implicit dynamoClient: AnyDynamoDBClient): 
      DeleteItemCompositeKeyExecutor[A] =
      DeleteItemCompositeKeyExecutor[A](a)(dynamoClient)

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

[test/scala/tabula/simpleModel.scala]: ../../../../test/scala/tabula/simpleModel.scala.md
[test/scala/tabula/resourceLists.scala]: ../../../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../../../test/scala/tabula/impl/irishService.scala.md
[main/scala/tabula.scala]: ../../tabula.scala.md
[main/scala/tabula/predicates.scala]: ../predicates.scala.md
[main/scala/tabula/accounts.scala]: ../accounts.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: DynamoDBExecutors.scala.md
[main/scala/tabula/impl/Configuration.scala]: Configuration.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: executors/GetItem.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: executors/UpdateTable.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: executors/PutItem.scala.md
[main/scala/tabula/impl/AttributeImplicits.scala]: AttributeImplicits.scala.md
[main/scala/tabula/regions.scala]: ../regions.scala.md
[main/scala/tabula/states.scala]: ../states.scala.md
[main/scala/tabula/actions/CreateTable.scala]: ../actions/CreateTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: ../actions/GetItem.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: ../actions/UpdateTable.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: ../actions/DeleteTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: ../actions/DeleteItem.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: ../actions/DescribeTable.scala.md
[main/scala/tabula/actions/PutItem.scala]: ../actions/PutItem.scala.md
[main/scala/tabula/executors.scala]: ../executors.scala.md
[main/scala/tabula/items.scala]: ../items.scala.md
[main/scala/tabula/resources.scala]: ../resources.scala.md
[main/scala/tabula/actions.scala]: ../actions.scala.md
[main/scala/tabula/tables.scala]: ../tables.scala.md
[main/scala/tabula/attributes.scala]: ../attributes.scala.md
[main/scala/tabula/services.scala]: ../services.scala.md
[main/scala/tabula/conditions.scala]: ../conditions.scala.md