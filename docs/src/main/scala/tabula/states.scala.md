
```scala
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
```


### throughput status


```scala
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
```


### table states


```scala
sealed trait AnyTableState extends AnyDynamoDBState {

  type Resource <: Singleton with AnyTable
  
  val throughputStatus: AnyThroughputStatus

  def deleting = Deleting(resource, account, throughputStatus)

  // TODO table ARN  
}

object AnyTableState {
  type For[T] = AnyTableState {type Resource = T}
}

case class InitialState[T <: Singleton with AnyTable](
  resource: T,
  account: Account,
  throughputStatus: InitialThroughput
) extends AnyTableState {
  type Resource = T

  def creating = Creating(resource, account, throughputStatus)
}

trait ReadyTable

case class Updating[T <: Singleton with AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState with ReadyTable { type Resource = T }

case class Creating[T <: Singleton with AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState { type Resource = T }

case class Active[T <: Singleton with AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState with ReadyTable { type Resource = T }

case class Deleting[T <: Singleton with AnyTable](
  resource: T,
  account: Account,
  throughputStatus: AnyThroughputStatus
) extends AnyTableState { type Resource = T }

```


------

### Index

+ src
  + test
    + scala
      + tabula
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]
        + [resourceLists.scala][test/scala/tabula/resourceLists.scala]
        + impl
          + [irishService.scala][test/scala/tabula/impl/irishService.scala]
  + main
    + scala
      + [tabula.scala][main/scala/tabula.scala]
      + tabula
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + impl
          + [DynamoDBExecutors.scala][main/scala/tabula/impl/DynamoDBExecutors.scala]
          + [Configuration.scala][main/scala/tabula/impl/Configuration.scala]
          + executors
            + [CreateTable.scala][main/scala/tabula/impl/executors/CreateTable.scala]
            + [GetItem.scala][main/scala/tabula/impl/executors/GetItem.scala]
            + [UpdateTable.scala][main/scala/tabula/impl/executors/UpdateTable.scala]
            + [DeleteTable.scala][main/scala/tabula/impl/executors/DeleteTable.scala]
            + [DeleteItem.scala][main/scala/tabula/impl/executors/DeleteItem.scala]
            + [DescribeTable.scala][main/scala/tabula/impl/executors/DescribeTable.scala]
            + [PutItem.scala][main/scala/tabula/impl/executors/PutItem.scala]
          + [AttributeImplicits.scala][main/scala/tabula/impl/AttributeImplicits.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [states.scala][main/scala/tabula/states.scala]
        + actions
          + [CreateTable.scala][main/scala/tabula/actions/CreateTable.scala]
          + [GetItem.scala][main/scala/tabula/actions/GetItem.scala]
          + [UpdateTable.scala][main/scala/tabula/actions/UpdateTable.scala]
          + [DeleteTable.scala][main/scala/tabula/actions/DeleteTable.scala]
          + [DeleteItem.scala][main/scala/tabula/actions/DeleteItem.scala]
          + [DescribeTable.scala][main/scala/tabula/actions/DescribeTable.scala]
          + [PutItem.scala][main/scala/tabula/actions/PutItem.scala]
        + [executors.scala][main/scala/tabula/executors.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
        + [attributes.scala][main/scala/tabula/attributes.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]

[test/scala/tabula/simpleModel.scala]: ../../../test/scala/tabula/simpleModel.scala.md
[test/scala/tabula/resourceLists.scala]: ../../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../../test/scala/tabula/impl/irishService.scala.md
[main/scala/tabula.scala]: ../tabula.scala.md
[main/scala/tabula/predicates.scala]: predicates.scala.md
[main/scala/tabula/accounts.scala]: accounts.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/Configuration.scala]: impl/Configuration.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: impl/executors/PutItem.scala.md
[main/scala/tabula/impl/AttributeImplicits.scala]: impl/AttributeImplicits.scala.md
[main/scala/tabula/regions.scala]: regions.scala.md
[main/scala/tabula/states.scala]: states.scala.md
[main/scala/tabula/actions/CreateTable.scala]: actions/CreateTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: actions/GetItem.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: actions/UpdateTable.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: actions/DeleteTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: actions/DeleteItem.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: actions/DescribeTable.scala.md
[main/scala/tabula/actions/PutItem.scala]: actions/PutItem.scala.md
[main/scala/tabula/executors.scala]: executors.scala.md
[main/scala/tabula/items.scala]: items.scala.md
[main/scala/tabula/resources.scala]: resources.scala.md
[main/scala/tabula/actions.scala]: actions.scala.md
[main/scala/tabula/tables.scala]: tables.scala.md
[main/scala/tabula/attributes.scala]: attributes.scala.md
[main/scala/tabula/services.scala]: services.scala.md
[main/scala/tabula/conditions.scala]: conditions.scala.md