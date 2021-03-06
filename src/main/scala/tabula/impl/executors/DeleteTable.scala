package ohnosequences.tabula.impl

import ohnosequences.tabula._, actions._, executors._
import com.amazonaws.services.dynamodbv2.model._

case class DeleteTableExecutor[A <: action.AnyDeleteTable]
  (dynamoClient: AnyDynamoDBClient) extends ExecutorFor[A] {

  type OutC[X] = X

  def apply(action: A)(inputState: A#InputState): Out = {
    println("executing: " + action)

    try {
      dynamoClient.client.deleteTable(action.table.name)
    } catch {
      case r: ResourceNotFoundException => println("warning: table " + action.table.name + " doesn't exist")
    }

    ExecutorResult[A](None, inputState.deleting)
  }
}
