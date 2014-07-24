package ohnosequences.tabula.impl

import ohnosequences.tabula._, ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._

case class CreateTableExecutor[Action <: AnyCreateTable]
  (dynamoClient: AnyDynamoDBClient) extends Executor[Action] {

  type OutC[X] = X

  def apply(action: Action)(inputState: action.InputState): OutC[ExecutorResult[action.Output, action.OutputState]] = {
    println("executing: " + action)

    val throughput = new ProvisionedThroughput(
      inputState.throughputStatus.readCapacity, 
      inputState.throughputStatus.writeCapacity
    )

    val prerequest = new CreateTableRequest()
      .withTableName(action.table.name)
      .withProvisionedThroughput(throughput)

    val request = action.table.primaryKey match {
      case HashKey(hash) => {
        prerequest
          .withKeySchema(new KeySchemaElement(hash.label, "HASH"))
          .withAttributeDefinitions(getAttrDef(hash))
      }
      case CompositeKey(hash, range) => {
        prerequest
          .withKeySchema(new KeySchemaElement(hash.label, "HASH"), 
                         new KeySchemaElement(range.label, "RANGE"))
          .withAttributeDefinitions(getAttrDef(hash), 
                                    getAttrDef(range))
      }
    }
    

    try {
      dynamoClient.client.createTable(request)
    } catch {
      case e: ResourceInUseException => println("warning: table " + action.table.name + " is in use")
    }

    ExecutorResult(None, inputState.creating)
  }
}

// case class CreateCompositeKeyTableExecutor[A <: AnyCreateTable with AnyTableAction.withCompositeKeyTable](a: A)
//   (dynamoClient: AnyDynamoDBClient) extends Executor(a) {

//   type OutC[X] = X

//   def apply(): Out = {
//     println("executing: " + action)

//     val hashAttributeDefinition = getAttrDef(a.table.hashKey)
//     val rangeAttributeDefinition = getAttrDef(a.table.rangeKey)
//     val hashSchemaElement = new KeySchemaElement(action.table.hashKey.label, "HASH")
//     val rangeSchemaElement = new KeySchemaElement(action.table.rangeKey.label, "RANGE")
//     val throughput = new ProvisionedThroughput(
//       inputState.throughputStatus.readCapacity, 
//       inputState.throughputStatus.writeCapacity
//     )
//     val request = new CreateTableRequest()
//       .withTableName(action.table.name)
//       .withProvisionedThroughput(throughput)
//       .withKeySchema(hashSchemaElement, rangeSchemaElement)
//       .withAttributeDefinitions(hashAttributeDefinition, rangeAttributeDefinition)

//     try {
//       dynamoClient.client.createTable(request)
//     } catch {
//       case t: ResourceInUseException => println("already exists")
//     }

//     ExecutorResult(None, action.table, inputState.creating)
//   }
// }
