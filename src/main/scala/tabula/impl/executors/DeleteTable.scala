package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model._

case class DeleteTableExecutor[Action <: AnyDeleteTable](a: Action)
  (dynamoClient: AnyDynamoDBClient) extends Executor[Action](a) {

  type OutC[X] = X

  def apply(inputState: Action#InputState): Out = {
    println("executing: " + action)

    try { 
      dynamoClient.client.deleteTable(action.table.name)
    } catch {
      case r: ResourceNotFoundException => println("warning: table " + action.table.name + " doesn't exist")
    }

    ExecutorResult[Action](None, inputState.deleting)
  }
}
