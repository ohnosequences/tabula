package ohnosequences.tabula

import ohnosequences.pointless._, AnyTaggedType._, AnyTypeUnion._

/*
  ## Tables

  A table contains only the static part of a table, things hat cannot be changed once the the table is created. Dynamic data lives in `AnyTableState`. The only exception to this is the `Account`; this is so because normally it is something that is retrieved dynamically from the environment.
*/

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

object AnyTable {
  type inRegion[R <: AnyRegion] = AnyTable { type Region = R }

  type withHashKey      = AnyTable { type PrimaryKey <: AnyHashKey }
  type withCompositeKey = AnyTable { type PrimaryKey <: AnyCompositeKey }
}


/*
  Tables can have two types of primary keys: simple or composite. This is static and affects the operations that can be performed on them. For example, a `query` operation only makes sense on a table with a composite key.
*/
sealed trait AnyPrimaryKey

sealed trait AnyHashKey extends AnyPrimaryKey {
  type Hash <: AnyProperty
  val  hash: Hash

  // should be provided implicitly:
  val hashHasValidType: RawOf[Hash] isOneOf PrimaryKeyValues
}

case class HashKey[H <: AnyProperty]
  (val hash: H)(implicit 
    val hashHasValidType: RawOf[H] isOneOf PrimaryKeyValues
  ) 
  extends AnyHashKey{ 
    type Hash = H 
  }

sealed trait AnyCompositeKey extends AnyPrimaryKey {
  type Hash <: AnyProperty
  val  hash: Hash

  type Range <: AnyProperty
  val  range: Range

  // should be provided implicitly:
  val  hashHasValidType:  RawOf[Hash] isOneOf PrimaryKeyValues
  val rangeHasValidType: RawOf[Range] isOneOf PrimaryKeyValues
}

case class CompositeKey[H <: AnyProperty, R <: AnyProperty]
  (val hash: H, val range: R)(implicit
    val  hashHasValidType: RawOf[H] isOneOf PrimaryKeyValues,
    val rangeHasValidType: RawOf[R] isOneOf PrimaryKeyValues
  )
  extends AnyCompositeKey { 
    type Hash = H 
    type Range = R 
  }

sealed trait PrimaryKeyValue[PK <: AnyPrimaryKey]
case class HashKeyValue[K <: AnyHashKey](hash: RawOf[K#Hash]) extends PrimaryKeyValue[K]
case class CompositeKeyValue[K <: AnyCompositeKey](hash: RawOf[K#Hash], range: RawOf[K#Range]) extends PrimaryKeyValue[K]
