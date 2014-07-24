package ohnosequences.tabula

import ohnosequences.typesets._
import ohnosequences.scarph._

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
  type Hash <: Singleton with AnyProperty
  val  hash: Hash

  // should be provided implicitly:
  val hashHasValidType: Hash#Raw :<: PrimaryKeyValues
}

case class HashKey[H <: Singleton with AnyProperty]
  (val hash: H)(implicit 
    val hashHasValidType: H#Raw :<: PrimaryKeyValues
  ) 
  extends AnyHashKey{ 
    type Hash = H 
  }


sealed trait AnyCompositeKey extends AnyPrimaryKey {
  type Hash <: Singleton with AnyProperty
  val  hash: Hash

  type Range <: Singleton with AnyProperty
  val  range: Range

  // should be provided implicitly:
  val  hashHasValidType:  Hash#Raw :<: PrimaryKeyValues
  val rangeHasValidType: Range#Raw :<: PrimaryKeyValues
}

case class CompositeKey[H <: Singleton with AnyProperty, R <: Singleton with AnyProperty]
  (val hash: H, val range: R)(implicit
    val  hashHasValidType: H#Raw :<: PrimaryKeyValues,
    val rangeHasValidType: R#Raw :<: PrimaryKeyValues
  )
  extends AnyCompositeKey { 
    type Hash = H 
    type Range = R 
  }
