
```scala
package ohnosequences.tabula.impl

import ohnosequences.tabula._, AttributeImplicits._
import com.amazonaws.services.dynamodbv2.model._

case class CreateHashKeyTableExecutor[A <: AnyCreateTable with AnyTableAction.withHashKeyTable](a: A)
  (implicit
    dynamoClient: AnyDynamoDBClient
  ) extends Executor[A](a) {

  type OutC[X] = X

  def apply(): Out = {
    println("executing: " + action)

    val attributeDefinition = getAttrDef(a.table.hashKey)
    val keySchemaElement = new KeySchemaElement(action.table.hashKey.label, "HASH")
    val throughput = new ProvisionedThroughput(
      action.inputState.throughputStatus.readCapacity, 
      action.inputState.throughputStatus.writeCapacity
    )
    val request = new CreateTableRequest()
      .withTableName(action.table.name)
      .withProvisionedThroughput(throughput)
      .withKeySchema(keySchemaElement)
      .withAttributeDefinitions(attributeDefinition)

    try {
      dynamoClient.client.createTable(request)
    } catch {
      case e: ResourceInUseException => println("warning: table " + action.table.name + " is in use")
    }

    ExecutorResult(None, action.table, action.inputState.creating)
  }
}

case class CreateCompositeKeyTableExecutor[A <: AnyCreateTable with AnyTableAction.withCompositeKeyTable](a: A)(
    dynamoClient: AnyDynamoDBClient
  ) extends Executor(a) {

  type OutC[X] = X

  def apply(): Out = {
    println("executing: " + action)

    val hashAttributeDefinition = getAttrDef(a.table.hashKey)
    val rangeAttributeDefinition = getAttrDef(a.table.rangeKey)
    val hashSchemaElement = new KeySchemaElement(action.table.hashKey.label, "HASH")
    val rangeSchemaElement = new KeySchemaElement(action.table.rangeKey.label, "RANGE")
    val throughput = new ProvisionedThroughput(
      action.inputState.throughputStatus.readCapacity, 
      action.inputState.throughputStatus.writeCapacity
    )
    val request = new CreateTableRequest()
      .withTableName(action.table.name)
      .withProvisionedThroughput(throughput)
      .withKeySchema(hashSchemaElement, rangeSchemaElement)
      .withAttributeDefinitions(hashAttributeDefinition, rangeAttributeDefinition)

    try {
      dynamoClient.client.createTable(request)
    } catch {
      case t: ResourceInUseException => println("already exists")
    }

    ExecutorResult(None, action.table, action.inputState.creating)
  }
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

[test/scala/tabula/simpleModel.scala]: ../../../../../test/scala/tabula/simpleModel.scala.md
[test/scala/tabula/resourceLists.scala]: ../../../../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../../../../test/scala/tabula/impl/irishService.scala.md
[main/scala/tabula.scala]: ../../../tabula.scala.md
[main/scala/tabula/predicates.scala]: ../../predicates.scala.md
[main/scala/tabula/accounts.scala]: ../../accounts.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: ../DynamoDBExecutors.scala.md
[main/scala/tabula/impl/Configuration.scala]: ../Configuration.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: CreateTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: GetItem.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: UpdateTable.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: DeleteTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: DeleteItem.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: DescribeTable.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: PutItem.scala.md
[main/scala/tabula/impl/AttributeImplicits.scala]: ../AttributeImplicits.scala.md
[main/scala/tabula/regions.scala]: ../../regions.scala.md
[main/scala/tabula/states.scala]: ../../states.scala.md
[main/scala/tabula/actions/CreateTable.scala]: ../../actions/CreateTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: ../../actions/GetItem.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: ../../actions/UpdateTable.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: ../../actions/DeleteTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: ../../actions/DeleteItem.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: ../../actions/DescribeTable.scala.md
[main/scala/tabula/actions/PutItem.scala]: ../../actions/PutItem.scala.md
[main/scala/tabula/executors.scala]: ../../executors.scala.md
[main/scala/tabula/items.scala]: ../../items.scala.md
[main/scala/tabula/resources.scala]: ../../resources.scala.md
[main/scala/tabula/actions.scala]: ../../actions.scala.md
[main/scala/tabula/tables.scala]: ../../tables.scala.md
[main/scala/tabula/attributes.scala]: ../../attributes.scala.md
[main/scala/tabula/services.scala]: ../../services.scala.md
[main/scala/tabula/conditions.scala]: ../../conditions.scala.md