
```scala
package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._
```


## Tables

A table contains only the static part of a table, things hat cannot be changed once the the table is created. Dynamic data lives in `AnyTableState`. The only exception to this is the `Account`; this is so because normally it is something that is retrieved dynamically from the environment.


```scala
trait AnyTable extends AnyDynamoDBResource {
  val name: String

  type HashKey <: Singleton with AnyProperty
  val  hashKey: HashKey

  type ResourceType = Table.type
  val  resourceType = Table

  type Region <: AnyRegion
  val  region: Region
}
```


Tables can have two types of primary keys: simple or composite. This is static and affects the operations that can be performed on them. For example, a `query` operation only makes sense on a table with a composite key.


```scala
sealed trait AnyHashKeyTable extends AnyTable 

object AnyHashKeyTable {

  type withKey[P <: Singleton with AnyProperty] = AnyHashKeyTable { type HashKey = P }
}

sealed trait AnyCompositeKeyTable extends AnyTable { 

  type RangeKey <: Singleton with AnyProperty
  val rangeKey: RangeKey
}

object AnyCompositeKeyTable {

  type withHashKey[P <: Singleton with AnyProperty] = AnyCompositeKeyTable { type HashKey = P }
  type withRangeKey[P <: Singleton with AnyProperty] = AnyCompositeKeyTable { type RangeKey = P }
}

class HashKeyTable [
  HK <: Singleton with AnyProperty,
  R <: AnyRegion
](val name: String,
  val hashKey: HK,
  val region: R
)(implicit
  val ev_k: HK#Raw :<: PrimaryKeyValues
) extends AnyHashKeyTable {

  type Region = R
  type HashKey = HK
}

class CompositeKeyTable [
  HK <: Singleton with AnyProperty,
  RK <: Singleton with AnyProperty,
  R <: AnyRegion
](val name: String,
  val hashKey: HK,
  val rangeKey: RK,
  val region: R
)(implicit
  val ev_h: HK#Raw :<: PrimaryKeyValues,
  val ev_r: RK#Raw :<: PrimaryKeyValues
) extends AnyCompositeKeyTable {

  type Region = R
  type HashKey = HK
  type RangeKey = RK
}

object AnyTable {
  type inRegion[R <: AnyRegion] = AnyTable { type Region = R }
}


// trait AnyHashKeyTable extends AnyTable { table =>
  
//   type Tpe <: AnyHashKeyTableType

//   trait AnyGetItem {

//     // an item of this table
//     type Item <: AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }
//     val item: Item

//     def apply(rep: table.Rep, hash: table.tpe.key.hashKey.Rep): item.Rep
//   }

//   abstract class GetItem [
//     I <: Singleton with AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }
//   ](val item: I) extends AnyGetItem { 

//     type Item = I
//   }

//   case class TableOps(val rep: table.Rep) {

//     def get[
//       I <: Singleton with AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }
//     ](
//       item: I,
//       hash: table.tpe.key.hashKey.Rep
//     )(implicit
//       mkGetItem: I => GetItem[I]
//     ): I#Rep = {

//       val getItem = mkGetItem(item)
//       getItem(rep, hash)
//     }
//   }

// }


// trait AnyCompositeKeyTable extends AnyTable { table =>
  
//   type Tpe <: AnyCompositeKeyTableType

//   trait AnyGetItem {

//     // an item of this table
//     type Item <: AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }
//     val item: Item

//     def apply(rep: table.Rep, hash: table.tpe.key.hashKey.Rep, range: table.tpe.key.rangeKey.Rep): item.Rep
//   }
//   abstract class GetItem[I <: Singleton with AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }](val item: I) 
//     extends AnyGetItem { type Item = I }

//   case class TableOps(val rep: table.Rep) {

//     def get[
//       I <: Singleton with AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }
//     ](
//       item: I,
//       hash: table.tpe.key.hashKey.Rep,
//       range: table.tpe.key.rangeKey.Rep
//     )(implicit
//       mkGetItem: I => GetItem[I]
//     ): I#Rep = {

//       val getItem = mkGetItem(item)
//       getItem(rep, hash, range)
//     }

//     /*
//       The query method lets you do per-hash retrieval of items. You fix a value of the hash key and then pass on a predicate over the range key (which could be empty). 
//     */
//     def query [
//       I <: Singleton with AnyItem { type Tpe <: AnyItemType.of[table.Tpe] },
//       RP <: AnyPredicate.Over[I#Tpe], // TODO add bound for this to be only on the range key
//       FP <: AnyPredicate.Over[I#Tpe]
//     ](
//       item: I,
//       hash: table.tpe.key.hashKey.Rep,
//       withRange: RP,
//       filter: FP
//     ): List[I#Rep] = ???
//   }
// }

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