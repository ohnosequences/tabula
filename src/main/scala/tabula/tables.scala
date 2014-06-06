package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._

/*
  ## Tables

  A table contains only the static part of a table, things hat cannot be changed once the the table is created. Dynamic data lives in `AnyTableState`. The only exception to this is the `Account`; this is so because normally it is something that is retrieved dynamically from the environment.
*/
trait AnyTable extends AnyDynamoDBResource {

  type ResourceType = Table.type
  val resourceType = Table

  type Region <: AnyRegion
  val region: Region

  type Key <: AnyPrimaryKey
  val key: Key

  val name: String
}

/*
  Tables can have two types of primary keys: simple or composite. This is static and affects the operations that can be performed on them. For example, a `query` operation onl y makes sense on a table with a composite key.
*/
trait AnyHashKeyTable extends AnyTable { 

  type Key <: AnyHash 
}

trait AnyCompositeKeyTable extends AnyTable { 

  type Key <: AnyHashRange
}

class HashKeyTable [
  K <: AnyHash,
  R <: AnyRegion
](
  val name: String,
  val key: K,
  val region: R
) extends AnyHashKeyTable {

  type Region = R
  type Key = K
}

/*
  ### table states


*/
trait AnyTableState extends AnyDynamoDBState {

  type Resource <: AnyTable
  
  val throughputStatus: ThroughputStatus

  // TODO table ARN
    
}

sealed trait ThroughputStatus {

  val readCapacity: Int
  val writeCapacity: Int
  val lastIncrease: java.util.Date
  val lastDecrease: java.util.Date
  val numberOfDecreasesToday: Int
}
  
case class InitialThroughput(
  val readCapacity: Int,
  val writeCapacity: Int,
  val lastIncrease: java.util.Date = new java.util.Date(),
  val lastDecrease: java.util.Date = new java.util.Date(),
  val numberOfDecreasesToday: Int = 0
) extends ThroughputStatus {}

case class InitialState[T <: Singleton with AnyTable](
  val resource: T,
  val account: Account,
  val initialThroughput: InitialThroughput
) extends AnyTableState {

  type Resource = T

  val throughputStatus = initialThroughput
  
}
trait Creating extends AnyTableState
trait Updating extends AnyTableState
trait Active extends AnyTableState
trait Deleting extends AnyTableState  

object AnyTable {

  type HashTable = AnyTable { type Key <: AnyHash }
  type CompositeTable = AnyTable { type Key <: AnyHashRange }
}

// Keys
sealed trait AnyPrimaryKey
  /*
    A simple hash key
  */
  trait AnyHash extends AnyPrimaryKey {
    type HashKey  <: AnyAttribute
    val hashKey: HashKey
  }
    case class Hash[HA <: AnyAttribute](val hash: HA)(implicit ev: oneOf[PrimaryKeyValues]#is[HA#Raw]) 
    extends AnyHash {    

      type HashKey = HA
      val hashKey = hash
    }
  /*
    A composite primary key
  */
  trait AnyHashRange extends AnyPrimaryKey {
    type HashKey  <: AnyAttribute
    val hashKey: HashKey
    type RangeKey <: AnyAttribute
    val rangeKey: RangeKey
  }
    case class HashRange[
      H: oneOf[ValidValues]#is: oneOf[PrimaryKeyValues]#is,
      HA <: AnyAttribute { type Raw = H },
      R: oneOf[ValidValues]#is: oneOf[PrimaryKeyValues]#is,
      RA <: AnyAttribute { type Raw = R }
    ]
    (
      val hash: HA,
      val range: RA
    ) 
    extends AnyHashRange {

      type HashKey  = HA
      val hashKey = hash
      type RangeKey = RA
      val rangeKey = range
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