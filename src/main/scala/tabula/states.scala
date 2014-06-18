package ohnosequences.tabula

/*
  ### table states

*/
sealed trait AnyTableState extends AnyDynamoDBState {

  type Resource <: Singleton with AnyTable
  
  val throughputStatus: AnyThroughputStatus

  def deleting = Deleting(resource, account, throughputStatus)

  // TODO table ARN  
}

object AnyTableState {
  type For[T] = AnyTableState {type Resource = T}
}

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

case class InitialState[T <: Singleton with AnyTable](
  resource: T,
  account: Account,
  initialThroughput: InitialThroughput
)

extends AnyTableState {

  type Resource = T

  val throughputStatus = initialThroughput

  def creating = Creating(resource, account, initialThroughput)

}


case class Updating[T <: Singleton with AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState {
  type Resource = T
  //val throughputStatus = initialThroughput
}

case class Creating[T <: Singleton with AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState {
  type Resource = T
 // val throughputStatus = ThroughputStatus
}

case class Active[T <: Singleton with AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState {
  type Resource = T
  //val throughputStatus = initialThroughput

 // def deleting = Deleting(resource, account, throughputStatus)
}

case class Deleting[T <: Singleton with AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState {
  type Resource = T
 // val throughputStatus = initialThroughput
}
