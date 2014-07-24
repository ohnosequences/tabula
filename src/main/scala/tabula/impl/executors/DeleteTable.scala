package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model._

case class DeleteTableExecutor[Action <: AnyDeleteTable]
  (dynamoClient: AnyDynamoDBClient) extends Executor[Action] {

  type OutC[X] = X

  def apply(action: Action)(inputState: action.InputState): OutC[ExecutorResult[action.Output, action.OutputState]] = {
    println("executing: " + action)

    try { 
      dynamoClient.client.deleteTable(action.table.name)
    } catch {
      case r: ResourceNotFoundException => println("warning: table " + action.table.name + " doesn't exist")
    }

    ExecutorResult(None, inputState.deleting)
  }
}
