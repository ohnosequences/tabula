package ohnosequences.tabula.impl

import ohnosequences.tabula._
import com.amazonaws.services.dynamodbv2.model._

case class DescribeTableExecutor[A <: AnyDescribeTable](a: A)
  (dynamoClient: AnyDynamoDBClient) extends Executor[A](a) {

  type OutC[X] = X

  def apply(): Out = {
    println("executing: " + action)
    //CREATING, UPDATING, DELETING, ACTIVE

    val tableDescription = dynamoClient.client.describeTable(action.table.name).getTable
    val throughputDescription = tableDescription.getProvisionedThroughput

    val throughput = ohnosequences.tabula.ThroughputStatus (
      readCapacity = throughputDescription.getReadCapacityUnits.toInt,
      writeCapacity = throughputDescription.getWriteCapacityUnits.toInt,
      lastIncrease = throughputDescription.getLastIncreaseDateTime, // todo it will return null if no update actions was performed
      lastDecrease = throughputDescription.getLastDecreaseDateTime,
      numberOfDecreasesToday = throughputDescription.getNumberOfDecreasesToday.toInt
    )

    val newState: action.OutputState = dynamoClient.client.describeTable(action.table.name).getTable.getTableStatus match {
      case "ACTIVE" =>     Active(action.table, action.inputState.account, throughput)
      case "CREATING" => Creating(action.table, action.inputState.account, throughput)
      case "DELETING" => Deleting(action.table, action.inputState.account, throughput)
      case "UPDATING" => Updating(action.table, action.inputState.account, throughput)
    }

    ExecutorResult(None, action.table, newState)
  }
}
