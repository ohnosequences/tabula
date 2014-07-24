package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._

/*
  ## Tables

  A table contains only the static part of a table, things hat cannot be changed once the the table is created. Dynamic data lives in `AnyTableState`. The only exception to this is the `Account`; this is so because normally it is something that is retrieved dynamically from the environment.
*/
sealed trait AnyPrimaryKey

sealed trait AnyHashKey extends AnyPrimaryKey {
  type Hash <: Singleton with AnyProperty
  val  hash: Hash

  // should be provided implicitly:
  val hashHasValidType: Hash#Raw :<: PrimaryKeyValues
}
case class HashKey[HP <: Singleton with AnyProperty]
  (val hash: HP)(implicit val hashHasValidType: HP#Raw :<: PrimaryKeyValues)
    extends AnyHashKey{ type Hash = HP }


sealed trait AnyCompositeKey extends AnyPrimaryKey {
  type Hash <: Singleton with AnyProperty
  val  hash: Hash

  type Range <: Singleton with AnyProperty
  val  range: Range

  // should be provided implicitly:
  val  hashHasValidType:  Hash#Raw :<: PrimaryKeyValues
  val rangeHasValidType: Range#Raw :<: PrimaryKeyValues
}
case class CompositeKey[HP <: Singleton with AnyProperty, RP <: Singleton with AnyProperty]
  (val hash: HP, val range: RP)(implicit
    val  hashHasValidType: HP#Raw :<: PrimaryKeyValues,
    val rangeHasValidType: RP#Raw :<: PrimaryKeyValues
  )
  extends AnyCompositeKey { 
    type Hash = HP 
    type Range = RP 
  }


trait AnyTable extends AnyDynamoDBResource {
  val name: String

  type PrimaryKey <: AnyPrimaryKey
  val  primaryKey: PrimaryKey

  type ResourceType = TableResourceType.type
  val  resourceType = TableResourceType

  type Region <: AnyRegion
  val  region: Region
}

class Table [
  PK <: AnyPrimaryKey,
  R <: AnyRegion
](val name: String,
  val primaryKey: PK,
  val region: R
) extends AnyTable {

  type PrimaryKey = PK
  type Region = R
}

/*
  Tables can have two types of primary keys: simple or composite. This is static and affects the operations that can be performed on them. For example, a `query` operation only makes sense on a table with a composite key.
*/
// sealed trait AnyTable.withHashKey extends AnyTable {
//   type PrimaryKey <: HashKey
// }

// sealed trait AnyTable.withCompositeKey extends AnyTable { 

//   type RangeKey <: Singleton with AnyProperty
//   val rangeKey: RangeKey
// }

// class HashKeyTable [
//   HK <: Singleton with AnyProperty,
//   R <: AnyRegion
// ](val name: String,
//   val hashKey: HK,
//   val region: R
// )(implicit
//   val ev_k: HK#Raw :<: PrimaryKeyValues
// ) extends AnyTable.withHashKey {

//   type Region = R
//   type HashKey = HK
// }

// class CompositeKeyTable [
//   HK <: Singleton with AnyProperty,
//   RK <: Singleton with AnyProperty,
//   R <: AnyRegion
// ](val name: String,
//   val hashKey: HK,
//   val rangeKey: RK,
//   val region: R
// )(implicit
//   val ev_h: HK#Raw :<: PrimaryKeyValues,
//   val ev_r: RK#Raw :<: PrimaryKeyValues
// ) extends AnyTable.withCompositeKey {

//   type Region = R
//   type HashKey = HK
//   type RangeKey = RK
// }

object AnyTable {
  type inRegion[R <: AnyRegion] = AnyTable { type Region = R }

  type withHashKey      = AnyTable { type PrimaryKey <: AnyHashKey }
  type withCompositeKey = AnyTable { type PrimaryKey <: AnyCompositeKey }
}


// trait AnyTable.withHashKey extends AnyTable { table =>
  
//   type Tpe <: AnyTable.withHashKeyType

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


// trait AnyTable.withCompositeKey extends AnyTable { table =>
  
//   type Tpe <: AnyTable.withCompositeKeyType

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
