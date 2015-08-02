package ohnosequences.tabula.impl

import ohnosequences.tabula._, states._, tables._, executors._
import ImplicitConversions._
import com.amazonaws.services.dynamodbv2.model._
import java.util.Date

case class UpdateTableExecutor[T <: AnyTable](dynamoClient: AnyDynamoDBClient)
extends ExecutorFor[action.UpdateTable[T]] {

  type OutC[X] = X

  def apply(action: Action)(inputState: Action#InputState): Out = {
    println("executing: " + action)

    //CREATING, UPDATING, DELETING, ACTIVE

    // TODO: add checks for inputState!!!
    dynamoClient.client.updateTable(action.table.name,
      new ProvisionedThroughput(
        action.newReadThroughput,
        action.newWriteThroughput
      )
    )

    val oldThroughputStatus =  inputState.throughputStatus

    var throughputStatus = ThroughputStatus (
      readCapacity = action.newReadThroughput,
      writeCapacity = action.newWriteThroughput
    )

    // TODO: check it in documentation

    //decrease
    if (oldThroughputStatus.readCapacity > action.newReadThroughput) {
      throughputStatus = throughputStatus.copy(
        numberOfDecreasesToday = throughputStatus.numberOfDecreasesToday + 1,
          lastDecrease = new Date()
      )
    }

    //decrease
    if (oldThroughputStatus.writeCapacity > action.newWriteThroughput) {
      throughputStatus = throughputStatus.copy(
        numberOfDecreasesToday = throughputStatus.numberOfDecreasesToday + 1,
        lastDecrease = new Date()
      )
    }

    //increase
    if (oldThroughputStatus.readCapacity < action.newReadThroughput) {
      throughputStatus = throughputStatus.copy(
        lastIncrease = new Date()
      )
    }

    //increase
    if (oldThroughputStatus.writeCapacity < action.newWriteThroughput) {
      throughputStatus = throughputStatus.copy(
        lastIncrease = new Date()
      )
    }

    val newState = Updating(action.table, inputState.account, throughputStatus)

    ExecutorResult[Action](None, newState)
  }

}
