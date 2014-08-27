package ohnosequences.tabula

// TODO experiment with treating states as denotations of resources
trait AnyDynamoDBState { state =>

  type Resource <: AnyDynamoDBResource
  val resource: Resource

  val account: Account

  lazy val arn: DynamoDBARN[Resource] = DynamoDBARN(resource, account)
}

object AnyDynamoDBState {
  type of[R <: AnyDynamoDBResource] = AnyDynamoDBState { type Resource = R }
}

/*
  ### throughput status
*/
sealed trait AnyThroughputStatus {

  val readCapacity: Int
  val writeCapacity: Int
  val lastIncrease: java.util.Date
  val lastDecrease: java.util.Date
  val numberOfDecreasesToday: Int
}

case class ThroughputStatus(
  readCapacity: Int,
  writeCapacity: Int,
  lastIncrease: java.util.Date = new java.util.Date(),
  lastDecrease: java.util.Date = new java.util.Date(),
  numberOfDecreasesToday: Int = 0
) extends AnyThroughputStatus
  
case class InitialThroughput(
  readCapacity: Int,
  writeCapacity: Int,
  lastIncrease: java.util.Date = new java.util.Date(),
  lastDecrease: java.util.Date = new java.util.Date(),
  numberOfDecreasesToday: Int = 0
) 
extends AnyThroughputStatus {}


/*
  ### table states
*/
sealed trait AnyTableState extends AnyDynamoDBState {

  type Resource <: AnyTable
  
  val throughputStatus: AnyThroughputStatus

  def deleting = Deleting(resource, account, throughputStatus)

  // TODO table ARN  
}

object AnyTableState {
  type For[T] = AnyTableState {type Resource = T}
}

case class InitialState[T <: AnyTable](
  resource: T,
  account: Account,
  throughputStatus: InitialThroughput
) extends AnyTableState {
  type Resource = T

  def creating = Creating(resource, account, throughputStatus)
}

trait ReadyTable

case class Updating[T <: AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState with ReadyTable { type Resource = T }

case class Creating[T <: AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState { type Resource = T }

case class Active[T <: AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState with ReadyTable { type Resource = T }

case class Deleting[T <: AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState { type Resource = T }
