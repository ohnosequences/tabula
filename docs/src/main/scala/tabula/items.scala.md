
```scala
package ohnosequences.tabula

import ohnosequences.typesets._, AnyTag._
import ohnosequences.scarph._
import shapeless._
```


## Items

This is the type of items of a given table. A table can hold different kinds of records, as you could want to restrict access to some items for example; there's even functionality in IAM for this. By separating the item type from the table we can easily model this scenario as different item types for the same table.


```scala
trait AnyItem extends Representable { item =>

  val label: String
```

The table is accessible through the item type

```scala
  type Table <: Singleton with AnyTable
  val  table: Table

  // TODO remove Singleton
  type Record <: Singleton with AnyRecord
  val record: Record

  type Raw = Record#Raw

  implicit val propertiesHaveValidTypes: everyElementOf[Record#Raw]#isOneOf[ValidValues]

  // double tagging FTW!
  final def fields[R <: TypeSet](r: R)(implicit 
    p: R ~> record.Raw
  ): item.Rep = item ->> (record ->> p(r))

  implicit def propertyOps(rep: Record#Rep): AnyRecord.OtherPropertyOps[Record] = 
    AnyRecord.OtherPropertyOps[Record] (
      (record:Record) ->> rep
    )
}

abstract class Item[T <: Singleton with AnyTable, R <: Singleton with AnyRecord](val table: T, val rc: R)
(implicit 
  val propertiesHaveValidTypes: everyElementOf[R#Raw]#isOneOf[ValidValues]
) 
  extends AnyItem 
{

  val label = this.toString

  type Table = T
  type Record = R
  val record = rc: rc.type
}

object AnyItem {

  type RawOf[R <: Singleton with AnyRecord] = R#Raw
  type ofTable[T <: AnyTable] = AnyItem { type Table = T }
  type withRecord[R <: AnyRecord] = AnyItem { type Record = R }

  type RecordOf[I <: AnyItem] = I#Record
  type PropertiesOf[I <: AnyItem] = RecordOf[I]#Properties

  implicit def propertyOps[R <: Singleton with AnyItem](entry: TaggedWith[R])(implicit
    getItem: TaggedWith[R] => R
  ): AnyRecord.OtherPropertyOps[R#Record] = {

    val uh = getItem(entry)

    AnyRecord.OtherPropertyOps(

        (uh.record:R#Record) ->> entry
      )

  }
      
}

//////////////////////////////////////////////

trait ToItem[In, I <: Singleton with AnyItem] {

  type Out = I#Rep
  type Fun <: Singleton with Poly
  def apply(in: In, i: I): Out
}

object ToItem {

  import AnyItem.PropertiesOf

  type Aux[In, I <: Singleton with AnyItem, F <: Singleton with Poly] = ToItem[In, I] { type Fun = F }

  implicit def buah[In, I <: Singleton with AnyItem, F <: Singleton with Poly, Out](implicit 
    fr: ToProperties.Aux[In, I#Record#Properties, I#Raw, F]
  ): ToItem.Aux[In, I, F] =

      new ToItem[In, I] {

        type Fun = F

        def apply(in: In, i: I): I#Rep = {

          val rec = i.record
          val props = rec.properties
          val itemV = fr(in, props)

          ((i:I) ->> itemV)
        }
      }
}

object From {

  type Item[I <: Singleton with AnyItem, Out] = FromProperties[I#Record#Properties, Out] { type Reps = I#Record#Raw }

  type ItemAux[I <: Singleton with AnyItem, F <: Poly, Out] = 
  
    FromProperties[I#Record#Properties, Out] { 

      type Reps = I#Record#Raw
      type Fun = F
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

[main/scala/tabula/accounts.scala]: accounts.scala.md
[main/scala/tabula/actions/CreateTable.scala]: actions/CreateTable.scala.md
[main/scala/tabula/actions/DeleteItem.scala]: actions/DeleteItem.scala.md
[main/scala/tabula/actions/DeleteTable.scala]: actions/DeleteTable.scala.md
[main/scala/tabula/actions/DescribeTable.scala]: actions/DescribeTable.scala.md
[main/scala/tabula/actions/GetItem.scala]: actions/GetItem.scala.md
[main/scala/tabula/actions/PutItem.scala]: actions/PutItem.scala.md
[main/scala/tabula/actions/Query.scala]: actions/Query.scala.md
[main/scala/tabula/actions/UpdateTable.scala]: actions/UpdateTable.scala.md
[main/scala/tabula/actions.scala]: actions.scala.md
[main/scala/tabula/conditions.scala]: conditions.scala.md
[main/scala/tabula/executors.scala]: executors.scala.md
[main/scala/tabula/impl/actions/GetItem.scala]: impl/actions/GetItem.scala.md
[main/scala/tabula/impl/actions/PutItem.scala]: impl/actions/PutItem.scala.md
[main/scala/tabula/impl/actions/Query.scala]: impl/actions/Query.scala.md
[main/scala/tabula/impl/Configuration.scala]: impl/Configuration.scala.md
[main/scala/tabula/impl/DynamoDBExecutors.scala]: impl/DynamoDBExecutors.scala.md
[main/scala/tabula/impl/executors/CreateTable.scala]: impl/executors/CreateTable.scala.md
[main/scala/tabula/impl/executors/DeleteItem.scala]: impl/executors/DeleteItem.scala.md
[main/scala/tabula/impl/executors/DeleteTable.scala]: impl/executors/DeleteTable.scala.md
[main/scala/tabula/impl/executors/DescribeTable.scala]: impl/executors/DescribeTable.scala.md
[main/scala/tabula/impl/executors/GetItem.scala]: impl/executors/GetItem.scala.md
[main/scala/tabula/impl/executors/PutItem.scala]: impl/executors/PutItem.scala.md
[main/scala/tabula/impl/executors/Query.scala]: impl/executors/Query.scala.md
[main/scala/tabula/impl/executors/UpdateTable.scala]: impl/executors/UpdateTable.scala.md
[main/scala/tabula/impl/ImplicitConversions.scala]: impl/ImplicitConversions.scala.md
[main/scala/tabula/items.scala]: items.scala.md
[main/scala/tabula/predicates.scala]: predicates.scala.md
[main/scala/tabula/regions.scala]: regions.scala.md
[main/scala/tabula/resources.scala]: resources.scala.md
[main/scala/tabula/services.scala]: services.scala.md
[main/scala/tabula/states.scala]: states.scala.md
[main/scala/tabula/tables.scala]: tables.scala.md
[main/scala/tabula.scala]: ../tabula.scala.md
[test/scala/tabula/impl/irishService.scala]: ../../../test/scala/tabula/impl/irishService.scala.md
[test/scala/tabula/items.scala]: ../../../test/scala/tabula/items.scala.md
[test/scala/tabula/resourceLists.scala]: ../../../test/scala/tabula/resourceLists.scala.md
[test/scala/tabula/simpleModel.scala]: ../../../test/scala/tabula/simpleModel.scala.md