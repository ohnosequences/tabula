package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._

/*
  ## Tables

  A table contains only the static part of a table, things hat cannot be changed once the the table is created. Dynamic data lives in `AnyTableState`. The only exception to this is the `Account`; this is so because normally it is something that is retrieved dynamically from the environment.
*/
trait AnyTable extends AnyDynamoDBResource {
  val name: String

  type HashKey <: Singleton with AnyProperty
  val  hashKey: HashKey

  type ResourceType = Table.type
  val  resourceType = Table

  type Region <: AnyRegion
  val  region: Region
}

/*
  Tables can have two types of primary keys: simple or composite. This is static and affects the operations that can be performed on them. For example, a `query` operation only makes sense on a table with a composite key.
*/
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
