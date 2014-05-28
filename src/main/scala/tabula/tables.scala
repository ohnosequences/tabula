package ohnosequences.tabula

import ohnosequences.scarph._

trait AnyTableType {

  type Region <: AnyRegion
  val region: Region

  type Key <: AnyPrimaryKey
  val key: Key

  val name: String
}

trait Table extends Denotation[AnyTableType] {

  // TODO methods here for reading items through the key, retrieving attributes etc
  // same pattern as for vertices for example
}

sealed trait AnyPrimaryKey
  /*
    A simple hash key
  */
  trait AnyHash extends AnyPrimaryKey {
    type HashKey  <: AnyAttribute
    val hashKey: HashKey
  }
    case class Hash
    [
      H: oneOf[ValidValues]#is: oneOf[PrimaryKeyValues]#is,
      HA <: AnyAttribute { type Raw = H }
    ]
    (
      val hash: HA
    ) 
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