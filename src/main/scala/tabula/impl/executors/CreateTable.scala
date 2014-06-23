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
