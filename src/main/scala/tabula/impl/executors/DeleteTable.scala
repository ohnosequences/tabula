package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model._

case class DeleteTableExecutor[A <: AnyDeleteTable](a: A)(
    dynamoClient: AnyDynamoDBClient
  ) extends Executor(a) {
  type OutC[X] = X

  def apply(): Out = {
    println("executing: " + action)
    try { 
      dynamoClient.client.deleteTable(action.table.name)
    } catch {
      case r: ResourceNotFoundException => println("warning: table " + action.table.name + " doesn't exist")
    }
    ExecutorResult(None, action.table, action.inputState.deleting)
  }
}
