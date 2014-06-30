
```scala
package ohnosequences.tabula

import ohnosequences.scarph._
import com.amazonaws.services.dynamodbv2.model.AttributeValue

sealed trait GetItemResult { type Item <: AnyItem }
case class GetItemFailure[I <: AnyItem]() extends GetItemResult { type Item = I }
case class GetItemSuccess[I <: Singleton with AnyItem](item: I#Raw) extends GetItemResult { type Item = I }
```

### Common action trait

```scala
trait AnyGetItemAction extends AnyTableAction {
  //require updating or creating
  type InputState  = AnyTableState.For[Table] with ReadyTable
  type OutputState = InputState

  type Item <: Singleton with AnyItem.ofTable[Table]
  val  item: Item

  type Output = GetItemResult

  val parseSDKRep: Map[String, AttributeValue] => item.Rep
}
```

### Hash key table

```scala
trait AnyGetItemHashKeyAction extends AnyGetItemAction {
  type Table <: Singleton with AnyHashKeyTable
  type Input = table.hashKey.Raw
}

case class FromHashKeyTable[T <: Singleton with AnyHashKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withKey(hashKeyValue: t.hashKey.Raw)
    (implicit
      hasHashKey:  i.type HasProperty t.HashKey,
      parser: Map[String, AttributeValue] => i.Rep
    ) extends AnyGetItemHashKeyAction {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val input = hashKeyValue

      val inputState = inputSt

      val parseSDKRep = parser

      override def toString = s"FromTable ${t.name} getItem ${i.label} withKey ${hashKeyValue}"
    }

  }

}
```

### Composite key table

```scala
trait AnyGetItemCompositeKeyAction extends AnyGetItemAction {
  type Table <: Singleton with AnyCompositeKeyTable
  type Input = (table.hashKey.Raw, table.rangeKey.Raw)
}

case class FromCompositeKeyTable[T <: Singleton with AnyCompositeKeyTable]
  (t: T, inputSt: AnyTableState.For[T] with ReadyTable) {

  case class getItem[I <: Singleton with AnyItem.ofTable[T]](i: I) {

    case class withKeys(
      hashKeyValue: t.hashKey.Raw,
      rangeKeyValue: t.rangeKey.Raw
    )(implicit
      hasHashKey:  i.type HasProperty t.HashKey,
      hasRangeKey: i.type HasProperty t.RangeKey,
      parse: Map[String, AttributeValue] => i.Rep
    ) extends AnyGetItemCompositeKeyAction {
      type Table = T
      val  table = t: t.type

      type Item = I
      val  item = i: i.type

      val input = (hashKeyValue, rangeKeyValue)

      val inputState = inputSt

      val parseSDKRep = parse

      override def toString = s"FromTable ${t.name} getItem ${i.label} withKeys ${(hashKeyValue, rangeKeyValue)}"
    }

  }

}

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

[test/scala/tabula/simpleModel.scala]: ../../../../test/scala/tabula/simpleModel.scala.md
[test/scala/tabula/resourceLists.scala]: ../../../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../../../test/scala/tabula/impl/irishService.scala.md
[main/scala/tabula.scala]: ../../tabula.scala.md
[main/scala/tabula/predicates.scala]: ../predicates.scala.md
[main/scala/tabula/accounts.scala]: ../accounts.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: ../impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/Configuration.scala]: ../impl/Configuration.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: ../impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: ../impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: ../impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: ../impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: ../impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: ../impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: ../impl/executors/PutItem.scala.md
[main/scala/tabula/impl/AttributeImplicits.scala]: ../impl/AttributeImplicits.scala.md
[main/scala/tabula/regions.scala]: ../regions.scala.md
[main/scala/tabula/states.scala]: ../states.scala.md
[main/scala/tabula/actions/CreateTable.scala]: CreateTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: GetItem.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: UpdateTable.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: DeleteTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: DeleteItem.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: DescribeTable.scala.md
[main/scala/tabula/actions/PutItem.scala]: PutItem.scala.md
[main/scala/tabula/executors.scala]: ../executors.scala.md
[main/scala/tabula/items.scala]: ../items.scala.md
[main/scala/tabula/resources.scala]: ../resources.scala.md
[main/scala/tabula/actions.scala]: ../actions.scala.md
[main/scala/tabula/tables.scala]: ../tables.scala.md
[main/scala/tabula/attributes.scala]: ../attributes.scala.md
[main/scala/tabula/services.scala]: ../services.scala.md
[main/scala/tabula/conditions.scala]: ../conditions.scala.md