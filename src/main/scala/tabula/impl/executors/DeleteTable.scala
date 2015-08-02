package ohnosequences.tabula.impl

import ohnosequences.tabula._, tables._, executors._
import com.amazonaws.services.dynamodbv2.model._

case class DeleteTableExecutor[T <: AnyTable](dynamoClient: AnyDynamoDBClient)
extends ExecutorFor[action.DeleteTable[T]] {

  type OutC[X] = X

  def apply(action: Action)(inputState: Action#InputState): Out = {
    println("executing: " + action)

    try {
      dynamoClient.client.deleteTable(action.table.name)
    } catch {
      case r: ResourceNotFoundException => println("warning: table " + action.table.name + " doesn't exist")
    }

    ExecutorResult[Action](None, inputState.deleting)
  }
}
