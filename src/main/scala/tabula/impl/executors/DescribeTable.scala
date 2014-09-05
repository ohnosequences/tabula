package ohnosequences.tabula.impl

import ohnosequences.tabula._, AnyAction._
import com.amazonaws.services.dynamodbv2.model._

case class DescribeTableExecutor[T <: AnyTable, A <: DescribeTable[T]]
  (dynamoClient: AnyDynamoDBClient) extends ExecutorFor[A] {

  type OutC[X] = X

  def apply(action: A)(inputState: InputStateOf[A]): Out = {
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

    val newState: OutputStateOf[A] = dynamoClient.client.describeTable(action.table.name).getTable.getTableStatus match {
      case "ACTIVE" =>     Active(action.table, inputState.account, throughput)
      case "CREATING" => Creating(action.table, inputState.account, throughput)
      case "DELETING" => Deleting(action.table, inputState.account, throughput)
      case "UPDATING" => Updating(action.table, inputState.account, throughput)
    }

    ExecutorResult[A](None, newState)
  }
}
