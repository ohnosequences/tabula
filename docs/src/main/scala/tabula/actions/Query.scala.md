
```scala
package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula.Condition._
import ohnosequences.tabula.impl.ImplicitConversions._

sealed trait AnyQueryResult { type Item <: AnyItem }
abstract class QueryResult[I <: AnyItem] extends AnyQueryResult { type Item = I }
case class QueryFailure[I <: AnyItem](msg: String) extends QueryResult[I]
case class QuerySuccess[I <: Singleton with AnyItem](item: List[I#Rep]) extends QueryResult[I]
```

### Common action trait

```scala
trait AnyQueryAction extends AnyTableItemAction { action =>
  // quieries make sense only for the composite key tables
  type Table <: Singleton with AnyCompositeKeyTable

  val hasHashKey: table.HashKey ? item.record.Properties

  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  // TODO: restrict this type better
  type Input <: AnyPredicate.On[Item]
  type Output = QueryResult[Item]
}

trait AnySimpleQueryAction extends AnyQueryAction {

  type Input = SimplePredicate[Item, EQ[table.HashKey]]
}

// the range key condition is optional
trait AnyNormalQueryAction extends AnyQueryAction {
  
  type RangeCondition <: Condition.On[table.RangeKey] with KeyCondition
  val  rangeCondition: RangeCondition

  type Input = AND[SimplePredicate[Item, EQ[table.HashKey]], RangeCondition]
}

```


------

### Index

+ src
  + main
    + scala
      + tabula
        + [accounts.scala][main/scala/tabula/accounts.scala]
        + actions
          + [CreateTable.scala][main/scala/tabula/actions/CreateTable.scala]
          + [DeleteItem.scala][main/scala/tabula/actions/DeleteItem.scala]
          + [DeleteTable.scala][main/scala/tabula/actions/DeleteTable.scala]
          + [DescribeTable.scala][main/scala/tabula/actions/DescribeTable.scala]
          + [GetItem.scala][main/scala/tabula/actions/GetItem.scala]
          + [PutItem.scala][main/scala/tabula/actions/PutItem.scala]
          + [Query.scala][main/scala/tabula/actions/Query.scala]
          + [UpdateTable.scala][main/scala/tabula/actions/UpdateTable.scala]
        + [actions.scala][main/scala/tabula/actions.scala]
        + [conditions.scala][main/scala/tabula/conditions.scala]
        + [executors.scala][main/scala/tabula/executors.scala]
        + impl
          + actions
            + [GetItem.scala][main/scala/tabula/impl/actions/GetItem.scala]
            + [PutItem.scala][main/scala/tabula/impl/actions/PutItem.scala]
            + [Query.scala][main/scala/tabula/impl/actions/Query.scala]
          + [Configuration.scala][main/scala/tabula/impl/Configuration.scala]
          + [DynamoDBExecutors.scala][main/scala/tabula/impl/DynamoDBExecutors.scala]
          + executors
            + [CreateTable.scala][main/scala/tabula/impl/executors/CreateTable.scala]
            + [DeleteItem.scala][main/scala/tabula/impl/executors/DeleteItem.scala]
            + [DeleteTable.scala][main/scala/tabula/impl/executors/DeleteTable.scala]
            + [DescribeTable.scala][main/scala/tabula/impl/executors/DescribeTable.scala]
            + [GetItem.scala][main/scala/tabula/impl/executors/GetItem.scala]
            + [PutItem.scala][main/scala/tabula/impl/executors/PutItem.scala]
            + [Query.scala][main/scala/tabula/impl/executors/Query.scala]
            + [UpdateTable.scala][main/scala/tabula/impl/executors/UpdateTable.scala]
          + [ImplicitConversions.scala][main/scala/tabula/impl/ImplicitConversions.scala]
        + [items.scala][main/scala/tabula/items.scala]
        + [predicates.scala][main/scala/tabula/predicates.scala]
        + [regions.scala][main/scala/tabula/regions.scala]
        + [resources.scala][main/scala/tabula/resources.scala]
        + [services.scala][main/scala/tabula/services.scala]
        + [states.scala][main/scala/tabula/states.scala]
        + [tables.scala][main/scala/tabula/tables.scala]
      + [tabula.scala][main/scala/tabula.scala]
  + test
    + scala
      + tabula
        + impl
          + [irishService.scala][test/scala/tabula/impl/irishService.scala]
        + [items.scala][test/scala/tabula/items.scala]
        + [resourceLists.scala][test/scala/tabula/resourceLists.scala]
        + [simpleModel.scala][test/scala/tabula/simpleModel.scala]

[main/scala/tabula/accounts.scala]: ../accounts.scala.md
[main/scala/tabula/actions/CreateTable.scala]: CreateTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: DeleteItem.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: DeleteTable.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: DescribeTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: GetItem.scala.md
[main/scala/tabula/actions/PutItem.scala]: PutItem.scala.md
[main/scala/tabula/actions/Query.scala]: Query.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: UpdateTable.scala.md
[main/scala/tabula/actions.scala]: ../actions.scala.md
[main/scala/tabula/conditions.scala]: ../conditions.scala.md
[main/scala/tabula/executors.scala]: ../executors.scala.md
[main/scala/tabula/impl/actions/GetItem.scala]: ../impl/actions/GetItem.scala.md
[main/scala/tabula/impl/actions/PutItem.scala]: ../impl/actions/PutItem.scala.md
[main/scala/tabula/impl/actions/Query.scala]: ../impl/actions/Query.scala.md
[main/scala/tabula/impl/Configuration.scala]: ../impl/Configuration.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: ../impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: ../impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: ../impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: ../impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: ../impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: ../impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: ../impl/executors/PutItem.scala.md
[main/scala/tabula/impl/executors/Query.scala]: ../impl/executors/Query.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: ../impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/ImplicitConversions.scala]: ../impl/ImplicitConversions.scala.md
[main/scala/tabula/items.scala]: ../items.scala.md
[main/scala/tabula/predicates.scala]: ../predicates.scala.md
[main/scala/tabula/regions.scala]: ../regions.scala.md
[main/scala/tabula/resources.scala]: ../resources.scala.md
[main/scala/tabula/services.scala]: ../services.scala.md
[main/scala/tabula/states.scala]: ../states.scala.md
[main/scala/tabula/tables.scala]: ../tables.scala.md
[main/scala/tabula.scala]: ../../tabula.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../../../test/scala/tabula/impl/irishService.scala.md
[test/scala/tabula/items.scala]: ../../../../test/scala/tabula/items.scala.md
[test/scala/tabula/resourceLists.scala]: ../../../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/simpleModel.scala]: ../../../../test/scala/tabula/simpleModel.scala.md