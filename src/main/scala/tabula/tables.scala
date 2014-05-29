package ohnosequences.tabula

import ohnosequences.scarph._

trait AnyTableType {

  type Region <: AnyRegion
  val region: Region

  type Key <: AnyPrimaryKey
  val key: Key

  val name: String
}

trait AnyHashKeyTableType extends AnyTableType { type Key <: AnyHash }
trait AnyCompositeKeyTableType extends AnyTableType { type Key <: AnyHashRange }

abstract class HashKeyTableType[ 
  K <: AnyHash,
  R <: AnyRegion
](
  val name: String,
  val key: K,
  val region: R
) extends AnyHashKeyTableType {

  type Region = R
  type Key = K
}

object AnyTableType {

  type HashTable = AnyTableType { type Key <: AnyHash }
}

trait AnyTable extends Denotation[AnyTableType] {

  // type Tpe <: AnyTableType

  // TODO methods here for reading items through the key, retrieving attributes etc
  // same pattern as for vertices for example
  /*
    get an item of this table by type
  */
}

trait AnyHashKeyTable extends AnyTable { table =>
  
  type Tpe <: AnyHashKeyTableType

  trait AnyGetItem {

    // an item of this table
    type Item <: AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }
    val item: Item

    def apply(rep: table.Rep, hash: table.tpe.key.hashKey.Rep): item.Rep
  }
  abstract class GetItem[I <: Singleton with AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }](val item: I) 
    extends AnyGetItem { type Item = I }

  case class TableOps(val rep: table.Rep) {

    def get[
      I <: Singleton with AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }
    ](
      item: I,
      hash: table.tpe.key.hashKey.Rep
    )(implicit
      mkGetItem: I => GetItem[I]
    ): I#Rep = {

      val getItem = mkGetItem(item)
      getItem(rep, hash)
    }
  }

}

trait AnyCompositeKeyTable extends AnyTable { table =>
  
  type Tpe <: AnyCompositeKeyTableType

  trait AnyGetItem {

    // an item of this table
    type Item <: AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }
    val item: Item

    def apply(rep: table.Rep, hash: table.tpe.key.hashKey.Rep, range: table.tpe.key.rangeKey.Rep): item.Rep
  }
  abstract class GetItem[I <: Singleton with AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }](val item: I) 
    extends AnyGetItem { type Item = I }

  case class TableOps(val rep: table.Rep) {

    def get[
      I <: Singleton with AnyItem { type Tpe <: AnyItemType.of[table.Tpe] }
    ](
      item: I,
      hash: table.tpe.key.hashKey.Rep,
      range: table.tpe.key.rangeKey.Rep
    )(implicit
      mkGetItem: I => GetItem[I]
    ): I#Rep = {

      val getItem = mkGetItem(item)
      getItem(rep, hash, range)
    }
  }

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