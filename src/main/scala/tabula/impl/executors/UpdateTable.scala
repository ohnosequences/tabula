package ohnosequences.tabula.impl

import ohnosequences.tabula._, AttributeImplicits._
import com.amazonaws.services.dynamodbv2.model._
import java.util.Date

case class UpdateTableExecutor[A <: AnyUpdateTableAction](a: A)
  (dynamoClient: AnyDynamoDBClient) 
    extends Executor[A](a) {

  type OutC[X] = X

  def apply(): Out = {
    println("executing: " + action)

    //CREATING, UPDATING, DELETING, ACTIVE

    // TODO: add checks for inputState!!!
    dynamoClient.client.updateTable(action.table.name, 
      new ProvisionedThroughput(
        action.newReadThroughput, 
        action.newWriteThroughput
      )
    )

    val oldThroughputStatus =  action.inputState.throughputStatus

    var throughputStatus = ohnosequences.tabula.ThroughputStatus (
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

    val newState = Updating(action.table, action.inputState.account, throughputStatus)

    ExecutorResult(None, action.table, newState)
  }

}

