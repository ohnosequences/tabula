
```scala
package ohnosequences.tabula.impl.actions

import ohnosequences.typesets._, AnyTag._
import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.{AttributeValueUpdate, AttributeValue}
import ohnosequences.tabula._, impl._, ImplicitConversions._

case class InHashKeyTable[T <: Singleton with AnyHashKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class putItem[I <: Singleton with AnyItem with AnyItem.ofTable[T]](i: I) {

    case class withValue(
      val itemRep: i.Rep
    )(implicit
      val transf: From.Item[i.type, SDKRep],
      val hasHashKey:  t.HashKey ? i.record.Properties
    ) 
    extends AnyPutItemAction with SDKRepGetter {

      type Table = T
      val  table = t:t.type

      type Item = I
      val  item = i:i.type

      val  input = itemRep
      val  getSDKRep = (r:i.Rep) => transf(r)

      val inputState = inputSt

      override def toString = s"InTable ${t.name} putItem ${i.label} withValue ${itemRep}"
    }

  }

}


case class InCompositeKeyTable[T <: Singleton with AnyCompositeKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class putItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withValue(itemRep: i.Rep)(implicit
      val transf: From.Item[i.type, SDKRep],
      val hasHashKey:  t.HashKey  ? i.record.Properties,
      val hasRangeKey: t.RangeKey ? i.record.Properties 
    ) extends AnyPutItemAction with SDKRepGetter {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val  input = itemRep
      val  getSDKRep = (r: i.Rep) => transf(r)

      val inputState = inputSt

      override def toString = s"InTable ${t.name} putItem ${i.label} withValue ${itemRep}"
    }

  }

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

[main/scala/tabula/accounts.scala]: ../../accounts.scala.md
[main/scala/tabula/actions/CreateTable.scala]: ../../actions/CreateTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: ../../actions/DeleteItem.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: ../../actions/DeleteTable.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: ../../actions/DescribeTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: ../../actions/GetItem.scala.md
[main/scala/tabula/actions/PutItem.scala]: ../../actions/PutItem.scala.md
[main/scala/tabula/actions/Query.scala]: ../../actions/Query.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: ../../actions/UpdateTable.scala.md
[main/scala/tabula/actions.scala]: ../../actions.scala.md
[main/scala/tabula/conditions.scala]: ../../conditions.scala.md
[main/scala/tabula/executors.scala]: ../../executors.scala.md
[main/scala/tabula/impl/actions/GetItem.scala]: GetItem.scala.md
[main/scala/tabula/impl/actions/PutItem.scala]: PutItem.scala.md
[main/scala/tabula/impl/actions/Query.scala]: Query.scala.md
[main/scala/tabula/impl/Configuration.scala]: ../Configuration.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: ../DynamoDBExecutors.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: ../executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: ../executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: ../executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: ../executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: ../executors/GetItem.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: ../executors/PutItem.scala.md
[main/scala/tabula/impl/executors/Query.scala]: ../executors/Query.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: ../executors/UpdateTable.scala.md
[main/scala/tabula/impl/ImplicitConversions.scala]: ../ImplicitConversions.scala.md
[main/scala/tabula/items.scala]: ../../items.scala.md
[main/scala/tabula/predicates.scala]: ../../predicates.scala.md
[main/scala/tabula/regions.scala]: ../../regions.scala.md
[main/scala/tabula/resources.scala]: ../../resources.scala.md
[main/scala/tabula/services.scala]: ../../services.scala.md
[main/scala/tabula/states.scala]: ../../states.scala.md
[main/scala/tabula/tables.scala]: ../../tables.scala.md
[main/scala/tabula.scala]: ../../../tabula.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../../../../test/scala/tabula/impl/irishService.scala.md
[test/scala/tabula/items.scala]: ../../../../../test/scala/tabula/items.scala.md
[test/scala/tabula/resourceLists.scala]: ../../../../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/simpleModel.scala]: ../../../../../test/scala/tabula/simpleModel.scala.md